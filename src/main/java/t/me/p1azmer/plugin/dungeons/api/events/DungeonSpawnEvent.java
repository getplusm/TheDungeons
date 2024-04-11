package t.me.p1azmer.plugin.dungeons.api.events;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public class DungeonSpawnEvent extends DungeonEvent{
    private static final HandlerList handlerList = new HandlerList();
    private Location location;

    public DungeonSpawnEvent(@NotNull Dungeon dungeon, @NotNull Location location) {
        super(dungeon, true);
        this.location = location;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
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
