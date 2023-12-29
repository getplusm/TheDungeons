package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CommandModule extends AbstractModule {

    private Set<DungeonStage> stagesCache;

    public CommandModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.stagesCache = new LinkedHashSet<>();
        return aBoolean -> true;
    }

    @Override
    protected void onShutdown() {
        if (this.stagesCache != null) {
            this.stagesCache.clear();
            this.stagesCache = null;
        }
    }

    @Override
    public boolean onActivate() {
        return true;
    }

    @Override
    public void update() {
        List<String> commands = this.dungeon().getCommandsSettings().getCommands(this.dungeon().getStage());
        if (!commands.isEmpty() && !this.stagesCache.contains(this.dungeon().getStage())) {
            if (this.stagesCache == null)
                this.stagesCache = new LinkedHashSet<>();
            stagesCache.add(this.dungeon().getStage());

            commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }
        super.update();
    }

    @Override
    public boolean onDeactivate() {
        if (this.stagesCache != null) {
            this.stagesCache.clear();
        }
        return true;
    }
}
