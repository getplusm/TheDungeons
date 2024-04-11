package t.me.p1azmer.plugin.dungeons.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public abstract class DungeonEvent extends Event implements Cancellable {

    protected final Dungeon dungeon;
    protected boolean cancelled;


    public DungeonEvent(@NotNull Dungeon dungeon){
        super(false);
        this.dungeon = dungeon;
    }
    public DungeonEvent(@NotNull Dungeon dungeon, boolean async){
        super(async);
        this.dungeon = dungeon;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public Dungeon getDungeon() {
        return dungeon;
    }
}
