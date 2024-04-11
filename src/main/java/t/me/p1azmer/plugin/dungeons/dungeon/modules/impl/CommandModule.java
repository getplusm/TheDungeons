package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class CommandModule extends AbstractModule {

    private Set<DungeonStage> stagesCache = new LinkedHashSet<>();

    public CommandModule(
            @NotNull Dungeon dungeon,
            @NotNull String id
    ) {
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
    public CompletableFuture<Boolean> onActivate(boolean force) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void update() {
        super.update();
        List<String> commands = this.getDungeon().getCommandsSettings().getCommands(this.getDungeon().getStage());
        if (!commands.isEmpty() && !this.stagesCache.contains(this.getDungeon().getStage())) {
            if (this.stagesCache == null)
                this.stagesCache = new LinkedHashSet<>();
            stagesCache.add(this.getDungeon().getStage());

            commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            debug("Commands executed");
        }
    }

    @Override
    public boolean onDeactivate(boolean force) {
        this.stagesCache.clear();
        return true;
    }
}
