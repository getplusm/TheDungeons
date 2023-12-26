package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public enum DungeonStage {
    FREEZE,
    CHECK,
    PREPARE,
    WAITING_PLAYERS,
    OPENING,
    OPENED,
    CLOSED,
    DELETING,
    CANCELLED,
    REMOVED,
    REBOOTED,
    ;

    public boolean isFreeze() {
        return this.equals(FREEZE);
    }

    public boolean isCheck() {
        return this.equals(CHECK);
    }

    public boolean isPrepare() {
        return this.equals(PREPARE);
    }

    public boolean isWaitingPlayers() {
        return this.equals(WAITING_PLAYERS);
    }

    public boolean isOpening() {
        return this.equals(OPENING);
    }

    public boolean isOpened() {
        return this.equals(OPENED);
    }

    public boolean isClosed() {
        return this.equals(CLOSED);
    }

    public boolean isDeleting() {
        return this.equals(DELETING);
    }

    public boolean isCancelled() {
        return this.equals(CANCELLED);
    }

    public boolean isRemoved() {
        return this.equals(REMOVED);
    }

    public boolean isRebooted() {
        return this.equals(REBOOTED);
    }

    public void tick(@NotNull Dungeon dungeon) {
        AtomicInteger timer = dungeon.getTimer();
        DungeonAPI.PLUGIN.warn("Tick the stage=" + dungeon.getId() + ", " + this.name() + ", " + timer.get() + "=" + dungeon.getStageSettings().getTime(this));
        if (timer.incrementAndGet() == dungeon.getStageSettings().getTime(this)) {
            call(dungeon, CollectionsUtil.getNext(Arrays.stream(DungeonStage.values()).toList(), this), "self class");
            timer.set(0);
        }
    }

    /**
     * Handles setting up a new stage for a dungeon
     *
     * @param dungeon - dungeon
     * @param stage   - called stage
     * @param from    - for debug messages
     */
    public static void call(@NotNull Dungeon dungeon, @NotNull DungeonStage stage, @NotNull String from) {
        DungeonPlugin plugin = dungeon.plugin();

        DungeonChangeStageEvent calledEvent = new DungeonChangeStageEvent(dungeon, stage);
        plugin.getPluginManager().callEvent(calledEvent);
        if (calledEvent.isCancelled()) {
            return;
        }
        dungeon.setStage(stage);
        if (stage.isRemoved() || stage.isCancelled() && (dungeon.getModuleManager().getModule(ChestModule.class).isPresent() && dungeon.getModuleManager().getModule(ChestModule.class).get().getActiveChests().isEmpty())) {
            dungeon.getModuleManager().getModules().forEach(AbstractModule::deactivate);
        }
        plugin.sendDebug("Call the dungeon '" + dungeon.getId() + "' from " + from + ". Change state to " + stage.name() + " from " + dungeon.getStage().name());
        dungeon.setStage(stage);

        switch (stage) {
            case CANCELLED -> {
                dungeon.cancel(false);
            }
        }
    }
}
