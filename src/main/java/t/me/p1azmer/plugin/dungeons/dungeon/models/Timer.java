package t.me.p1azmer.plugin.dungeons.dungeon.models;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.StageSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Timer {
    Dungeon dungeon;

    @NonFinal
    Instant timeToNextStage;

    public Timer(@NotNull Dungeon dungeon) {
        this.dungeon = dungeon;
        this.timeToNextStage = Instant.now();
    }

    public void updateInstant(@NotNull DungeonStage stage) {
        StageSettings stageSettings = dungeon.getStageSettings();
        timeToNextStage = timeToNextStage.plusSeconds(stageSettings.getTime(stage));
    }

    public long getTimeToNextStageInSeconds() {
        return Duration.between(Instant.now(), timeToNextStage).toSeconds();
    }

    public long getTimeToNextStageInMillis() {
        return Duration.between(Instant.now(), timeToNextStage).toMillis();
    }

    public void reboot() {
        DungeonPlugin.getLog().warning("Starting reboot '" + dungeon.getId() + "' dungeon!");

        CompletableFuture.runAsync(() -> {
            DungeonStage.handleDungeonChangeStage(dungeon, REBOOTED, "Reboot from gui");
            DungeonStage.handleDungeonChangeStage(dungeon, CANCELLED, "Reboot start");
        });
    }

    public void cancel(boolean shutdown) {
        if (!shutdown) DungeonStage.handleDungeonChangeStage(dungeon, FREEZE, "cancelled and refresh");

        DungeonPlugin plugin = dungeon.plugin();
        RegionHandler regionHandler = plugin.getRegionHandler();
        GenerationType generationType = dungeon.getGenerationSettings().getGenerationType();
        AbstractModule.ActionType actionType = AbstractModule.ActionType.of(shutdown);

        dungeon.getModuleManager()
                .getModules()
                .forEach(module -> module.tryDeactivate(actionType));

        if (regionHandler != null && dungeon.getRegion().isCreated()) {
            regionHandler.delete(dungeon);
        }

        if (generationType.isDynamic()) {
            dungeon.setLocation(null);
            dungeon.setCuboid(null);
        }
    }

    public void tick() {
        dungeon.getStage().tick(dungeon);
        for (AbstractModule module : dungeon.getModuleManager().getModules()) {
            module.update();
        }
    }
}