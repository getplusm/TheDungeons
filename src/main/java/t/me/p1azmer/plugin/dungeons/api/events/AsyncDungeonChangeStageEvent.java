package t.me.p1azmer.plugin.dungeons.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

@Getter
@Setter
public class AsyncDungeonChangeStageEvent extends DungeonEvent {
    private static final HandlerList handlerList = new HandlerList();

    private DungeonStage stage;
    private String changeFrom;

    public AsyncDungeonChangeStageEvent(@NotNull Dungeon dungeon, @NotNull DungeonStage stage, @NotNull String changeFrom) {
        super(dungeon, true);
        this.stage = stage;
        this.changeFrom = changeFrom;
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