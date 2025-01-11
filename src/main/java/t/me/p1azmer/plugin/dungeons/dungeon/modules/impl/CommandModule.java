package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandModule extends AbstractModule {
    Set<DungeonStage> stagesCache = new LinkedHashSet<>();

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
        Set<String> commands = this.getDungeon().getCommandsSettings().getCommands(this.getDungeon().getStage());
        if (!commands.isEmpty() && !this.stagesCache.contains(this.getDungeon().getStage())) {
            if (this.stagesCache == null)
                this.stagesCache = new LinkedHashSet<>();
            stagesCache.add(this.getDungeon().getStage());

            getDungeon().getThreadSync().sync(() -> {
                commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }).exceptionally(throwable -> {
                DungeonPlugin.getLog().log(Level.SEVERE, "An error occurred while executing commands in stage " + this.getDungeon().getStage(), throwable);
                return null;
            });
            debug("Commands executed");
        }
    }

    @Override
    public boolean onDeactivate(boolean force) {
        this.stagesCache.clear();
        return true;
    }
}
