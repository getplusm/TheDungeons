package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public enum DungeonStage {
    FREEZE,
    CHECK,
    PREPARE,
    CLOSED,
    WAITING_PLAYERS,
    OPENING,
    OPENED,
    DELETING,
    CANCELLED,
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

    public boolean isRebooted() {
        return this.equals(REBOOTED);
    }

    public void tick(@NotNull Dungeon dungeon) {
        AtomicInteger timer = dungeon.getSelfTick();
        if (Config.DEBUG_TICK.get()) {
            DungeonAPI.PLUGIN.warn("Tick the dungeon '" + dungeon.getId() + "'. Stage=" + this.name() + ", Tick=" + timer.get() + "/" + dungeon.getStageSettings().getTime(this));
        }
        if (timer.get() == dungeon.getStageSettings().getTime(this)) {
            call(dungeon, CollectionsUtil.next(dungeon.getStage()), "self class");
        } else {
            timer.incrementAndGet();
        }
    }

    /**
     * Handles setting up a new stage for a dungeon
     *
     * @param dungeon - dungeon
     * @param stage   - called stage
     * @param from    - for debug messages
     */
    public static boolean call(@NotNull Dungeon dungeon, @NotNull DungeonStage stage, @NotNull String from) {
        DungeonPlugin plugin = dungeon.plugin();

        DungeonChangeStageEvent calledEvent = new DungeonChangeStageEvent(dungeon, stage);
        plugin.getPluginManager().callEvent(calledEvent);
        if (calledEvent.isCancelled()) {
            return false;
        }

        if (stage.isDeleting() || stage.isCancelled()) {
            if (dungeon.getModuleManager().getModules().stream().anyMatch(module -> !module.deactivate())) {
                return false;
            }
            dungeon.cancel(false);
        }
        plugin.sendDebug("Call the dungeon '" + dungeon.getId() + "' from " + from + ". Change stage to " + stage.name() + " from " + dungeon.getStage().name());
        dungeon.setStage(stage);
        dungeon.setSelfTick(0);
        return true;
    }
}
