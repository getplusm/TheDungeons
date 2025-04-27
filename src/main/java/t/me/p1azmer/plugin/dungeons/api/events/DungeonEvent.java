package t.me.p1azmer.plugin.dungeons.api.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
abstract class DungeonEvent extends Event implements Cancellable {

    final Dungeon dungeon;
    boolean cancelled;

    public DungeonEvent(@NotNull Dungeon dungeon, boolean async) {
        super(async);
        this.dungeon = dungeon;
    }
}