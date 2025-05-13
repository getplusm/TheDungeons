package t.me.p1azmer.plugin.dungeons.schedulers;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.config.ConfigContainer;
import t.me.p1azmer.plugin.dungeons.config.configs.DungeonsConfig;
import t.me.p1azmer.plugin.dungeons.models.timer.Timer;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DungeonTickScheduler {

    Map<Dungeon, Timer> dungeonTimers = Maps.newConcurrentMap();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
            .setNameFormat("TheDungeons Tick Scheduler #%s")
            .setUncaughtExceptionHandler((t, e) -> DungeonPlugin.getLog().log(Level.SEVERE,
                    "Got an exception while tick dungeon", e))
            .setDaemon(true)
            .build());

    public DungeonTickScheduler(ServerImplementation syncScheduler, ConfigContainer configContainer) {
        scheduler.scheduleWithFixedDelay(() -> {
            DungeonsConfig dungeonsConfig = configContainer.getDungeonsConfig();
            for (Dungeon dungeon : dungeonsConfig.getDungeons().values()) {
                try {
                    //Logger.getGlobal().severe("tick " + dungeon.getId());
                    Timer timer = dungeonTimers.computeIfAbsent(dungeon, Timer::new);
                    timer.tick(syncScheduler);
                } catch (Exception exception) {
                    DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while tick " + dungeon.getId() + " dungeon", exception);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
