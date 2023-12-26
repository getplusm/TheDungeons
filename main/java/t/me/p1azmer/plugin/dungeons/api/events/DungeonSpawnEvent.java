package t.me.p1azmer.plugin.dungeons.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public class DungeonSpawnEvent extends DungeonEvent{
    private static final HandlerList handlerList = new HandlerList();

    public DungeonSpawnEvent(@NotNull Dungeon dungeon) {
        super(dungeon);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
