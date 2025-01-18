package t.me.p1azmer.plugin.dungeons.task;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DungeonTickTask {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    DungeonManager manager;

    public DungeonTickTask(@NotNull DungeonManager manager) {
        this.manager = manager;

        scheduler.scheduleAtFixedRate(this::handleTickDungeons, 0, 1, TimeUnit.SECONDS);
    }

    private void handleTickDungeons() {
        int online = Bukkit.getOnlinePlayers().size();

        for (Dungeon dungeon : this.manager.getDungeons()) {
            boolean allowed = online >= dungeon.getSettings().getMinimalOnline() && dungeon.getSettings().isEnabled();
            if (!allowed) {
                dungeon.cancel(false);
                continue;
            }
            try {
                dungeon.tick();
            } catch (RuntimeException exception) {
                DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while ticking a '" + dungeon.getId() + "' dungeon", exception);
            }
        }
    }

    public void tryActivateDungeonModules(@NotNull Dungeon dungeon) {
        scheduler.execute(() -> {
            dungeon.getModuleManager().getModules().forEach(module -> module.tryActive(AbstractModule.ActionType.FORCE));
        });
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
