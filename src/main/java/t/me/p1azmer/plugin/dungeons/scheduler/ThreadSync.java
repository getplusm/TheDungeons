package t.me.p1azmer.plugin.dungeons.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexEngine;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThreadSync {
    DungeonPlugin plugin;

    public @NotNull CompletableFuture<Void> sync(@NotNull Runnable runnable) {
        return syncApply(() -> {
            runnable.run();
            return null;
        });
    }

    public @NotNull <T> CompletableFuture<T> syncApply(@NotNull Supplier<T> supplier) {
        if (isMainThread()) {
            return CompletableFuture.completedFuture(supplier.get()).orTimeout(1, TimeUnit.SECONDS);
        } else {
            CompletableFuture<T> future = new CompletableFuture<T>().orTimeout(1, TimeUnit.SECONDS);
            runSync(() -> {
                try {
                    future.complete(supplier.get());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
            return future;
        }
    }

    private void runSync(@NotNull Runnable runnable) {
        // TODO experimental function. need more test
        if (NexEngine.isFolia && plugin.getFoliaScheduler() != null) {
            plugin.getFoliaScheduler().global().run(runnable);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    private boolean isMainThread() {
        return Bukkit.isPrimaryThread();
    }
}
