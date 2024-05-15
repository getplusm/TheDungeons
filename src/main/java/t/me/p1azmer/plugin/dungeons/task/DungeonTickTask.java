package t.me.p1azmer.plugin.dungeons.task;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.server.AbstractTask;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public class DungeonTickTask extends AbstractTask<DungeonPlugin> {
    private final DungeonManager manager;

    public DungeonTickTask(@NotNull DungeonManager manager) {
        super(manager.plugin(), 1, true);
        this.manager = manager;
    }

    @Override
    public void action() {
        int online = plugin.getServer().getOnlinePlayers().size();

        for (Dungeon dungeon : this.manager.getDungeons()) {
            boolean allowed = online >= dungeon.getSettings().getMinimalOnline() && dungeon.getSettings().isEnabled();
            if (!allowed) {
                dungeon.cancel(false);
                continue;
            }
            dungeon.tick();
        }
    }
}
