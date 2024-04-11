package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.StageSettings;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public enum DungeonStage {
    FREEZE(StageLang.FREEZE),
    CHECK(StageLang.CHECK),
    PREPARE(StageLang.PREPARE),
    CLOSED(StageLang.CLOSED),
    WAITING_PLAYERS(StageLang.WAITING_PLAYERS),
    OPENING(StageLang.OPENING),
    OPENED(StageLang.OPENED),
    DELETING(StageLang.DELETING),
    CANCELLED(StageLang.CANCELLED),
    REBOOTED(StageLang.REBOOTED);

    private final LangKey localization;

    DungeonStage(@NotNull LangKey localization) {
        this.localization = localization;
    }

    @NotNull
    public String getDescription(@NotNull DungeonPlugin plugin) {
        return plugin.getMessage(this.getLocalization()).normalizeLines();
    }

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
        DungeonPlugin plugin = dungeon.plugin();
        StageSettings stageSettings = dungeon.getStageSettings();

        if (Config.TICK_DEBUG.get()) {
            ModuleManager moduleManager = dungeon.getModuleManager();
            Location location = dungeon.getLocation().orElse(null);
            String locationText = location == null ? "empty" : Placeholders.forLocation(location)
                    .apply("%location_world% - %location_x%, %location_y%, %location_z%");
            String hasCuboid = Colorizer.strip(LangManager.getBoolean(dungeon.getDungeonCuboid().isPresent()));

            plugin.sendDebug(
                    "Dungeon '" + dungeon.getId() + "' | " +
                            "Stage=" + this.name() + " | " +
                            "Time=" + timer.get() + "/" + stageSettings.getTime(this) + " | " +
                            "Location=" + locationText + " | " +
                            "Cuboid=" + hasCuboid + " | " +
                            "Modules=" + moduleManager.getModules().size() + " | " +
                            "Active Modules=" + moduleManager.getActive().size()
            );
        }

        if (timer.get() == stageSettings.getTime(this)) {
            call(dungeon, CollectionsUtil.next(dungeon.getStage(), stage -> stage != dungeon.getStage()), "self class");
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
    public static void call(@NotNull Dungeon dungeon, @NotNull DungeonStage stage, @NotNull String from) {
        DungeonPlugin plugin = dungeon.plugin();

        DungeonChangeStageEvent calledEvent = new DungeonChangeStageEvent(dungeon, stage, from);
        plugin.getPluginManager().callEvent(calledEvent);
        if (calledEvent.isCancelled()) {
            plugin.sendDebug("It was not possible to change the state of the dungeon '" + dungeon.getId() + "' from \"" + from + "\", as the event was canceled");
        }
    }
}
