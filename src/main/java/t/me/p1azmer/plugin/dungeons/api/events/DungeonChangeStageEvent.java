package t.me.p1azmer.plugin.dungeons.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

public class DungeonChangeStageEvent extends DungeonEvent{
    private static final HandlerList handlerList = new HandlerList();

    private DungeonStage dungeonStage;

    public DungeonChangeStageEvent(@NotNull Dungeon dungeon, @NotNull DungeonStage stage) {
        super(dungeon);
        this.dungeonStage = stage;
    }

    @NotNull
    public DungeonStage getDungeonStage() {
        return dungeonStage;
    }

    public void setDungeonStage(@NotNull DungeonStage dungeonStage) {
        this.dungeonStage = dungeonStage;
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
