package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.engine.utils.collections.Lists;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.StageSettings;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DungeonStage {
    FREEZE(StageLang.FREEZE), // стадия заморозки/бездействия
    CHECK(StageLang.CHECK), // стадия проверки данжа перед отправкой на prepare
    PREPARE(StageLang.PREPARE), // подготовка данжа к спавну (генерация структуры)
    CLOSED(StageLang.CLOSED), // закрытие данжа, чтобы никто не мог в него войти
    WAITING_PLAYERS(StageLang.WAITING_PLAYERS), // стадия ожидания игроков. Дополнительная стадия для ожидания
    OPENING(StageLang.OPENING), // стадия открытия. Дополнительная стадия для ожидания
    OPENED(StageLang.OPENED), // стадия когда данж уже открыт
    DELETING(StageLang.DELETING), // удаление данжа. Очистка структуры и т.п
    CANCELLED(StageLang.CANCELLED), // стадия отмены
    REBOOTED(StageLang.REBOOTED);

    LangKey localization;

    @NotNull
    public String getDescription(@NotNull DungeonPlugin plugin) {
        return plugin.getMessage(getLocalization()).normalizeLines();
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
            call(dungeon, Lists.next(dungeon.getStage(), stage -> stage != dungeon.getStage()), "self class");
        } else {
            timer.incrementAndGet();
        }
    }

    public static void call(@NotNull Dungeon dungeon, @NotNull DungeonStage stage, @NotNull String from) {
        AsyncDungeonChangeStageEvent calledEvent = new AsyncDungeonChangeStageEvent(dungeon, stage, from);
        Bukkit.getPluginManager().callEvent(calledEvent);
        if (calledEvent.isCancelled()) {
            DungeonPlugin.getLog().severe("It was not possible to change the state of the dungeon '" + dungeon.getId() + "' from \"" + from + "\", as the event was cancelled");
        }
    }
}
