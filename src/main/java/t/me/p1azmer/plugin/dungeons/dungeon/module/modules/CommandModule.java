package t.me.p1azmer.plugin.dungeons.dungeon.module.modules;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommandModule extends AbstractModule {

    Set<DungeonStage> stagesCache = ConcurrentHashMap.newKeySet();

    public CommandModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        return aBoolean -> true;
    }

    @Override
    protected void onShutdown() {
        this.stagesCache.clear();
    }

    @Override
    public void update() {
        super.update();

        Dungeon dungeon = getDungeon();
        DungeonStage stage = dungeon.getStage();
        ArrayList<String> commands = new ArrayList<>(dungeon.getCommandsSettings().getCommands(stage));
        if (!commands.isEmpty() && !stagesCache.contains(stage)) {
            stagesCache.add(stage);
            commands.replaceAll(command -> {
                command = command
                        .replace("%dungeon_id%", dungeon.getId())
                        .replace("%dungeon_name%", dungeon.getName());

                ChestModule chestModule = dungeon.getModuleManager().getModule(ChestModule.class).orElse(null);
                if (chestModule == null) return command;

                for (int index = 0; index < chestModule.getChests().size(); index++) {
                    command = replaceSingleCheckLocation(command, chestModule, index);

                }
                return command;
            });

            dungeon.getThreadSync().sync(() -> {
                commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }).exceptionally(throwable -> {
                DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while execute commands for " + stage.name() + " stage", throwable);
                return null;
            });
        }
    }

    private static @NotNull String replaceSingleCheckLocation(String command, ChestModule chestModule, int index) {
        ChestBlock chestBlock = chestModule.getChestList().get(index);
        int position = index + 1;
        Location location = chestBlock.getLocation();
        command = command
                .replace("%chest_" + position + "_location_world%", location.getWorld().getName())
                .replace("%chest_" + position + "_location_x%", Double.toString(location.getX()))
                .replace("%chest_" + position + "_location_y%", Double.toString(location.getY()))
                .replace("%chest_" + position + "_location_z%", Double.toString(location.getZ()));
        return command;
    }

    @Override
    public boolean onDeactivate(boolean force) {
        this.stagesCache.clear();
        return true;
    }
}
