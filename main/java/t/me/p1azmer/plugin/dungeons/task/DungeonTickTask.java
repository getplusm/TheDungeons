package t.me.p1azmer.plugin.dungeons.task;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.server.AbstractTask;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

public class DungeonTickTask extends AbstractTask<DungeonPlugin> {
    private final DungeonManager manager;

    public DungeonTickTask(@NotNull DungeonManager manager) {
        super(manager.plugin(), 1, false);
        this.manager = manager;
    }

    @Override
    public void action() {
        for (Dungeon dungeon : this.manager.getDungeons()) {
            if (plugin.getServer().getOnlinePlayers().size() < dungeon.getSettings().getMinimalOnline() || !dungeon.getSettings().isEnabled()) {
                dungeon.cancel(false);
                continue;
            }
            dungeon.tick();
            if (!dungeon.getStage().isFreeze())
                dungeon.getModuleManager().getModules().forEach(AbstractModule::update);
        }
    }
}
