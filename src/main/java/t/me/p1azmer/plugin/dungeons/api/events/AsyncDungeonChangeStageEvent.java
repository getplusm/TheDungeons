package t.me.p1azmer.plugin.dungeons.api.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncDungeonChangeStageEvent extends DungeonEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    DungeonStage stage;
    String changeFrom;

    public AsyncDungeonChangeStageEvent(Dungeon dungeon, DungeonStage stage, String changeFrom) {
        super(dungeon, true);
        this.stage = stage;
        this.changeFrom = changeFrom;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}