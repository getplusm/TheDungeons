package t.me.p1azmer.plugin.dungeons.core.common.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThreadSync {
    Plugin plugin;

    public CompletableFuture<Void> sync(@NotNull Runnable runnable) {
        return syncApply(() -> {
            runnable.run();
            return null;
        });
    }

    public CompletableFuture<Void> sync(@NotNull Runnable runnable, @NotNull Duration timeout) {
        return syncApply(() -> {
            runnable.run();
            return null;

        }, timeout);
    }

    public CompletableFuture<Void> sync(@NotNull Runnable runnable, long value, @NotNull TimeUnit timeUnit) {
        return syncApply(() -> {
            runnable.run();
            return null;

        }, value, timeUnit);
    }

    public <T> CompletableFuture<T> syncApply(@NotNull Supplier<T> supplier, long value, @NotNull TimeUnit timeUnit) {
        if (isPrimary()) {
            return CompletableFuture.completedFuture(supplier.get()).orTimeout(value, timeUnit);
        } else {
            CompletableFuture<T> future = new CompletableFuture<T>().orTimeout(value, timeUnit);
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

    public <T> CompletableFuture<T> syncApply(@NotNull Supplier<T> supplier, @NotNull Duration timeout) {
        if (isPrimary()) {
            return CompletableFuture.completedFuture(supplier.get()).orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            CompletableFuture<T> future = new CompletableFuture<T>().orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
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

    public <T> CompletableFuture<T> syncApply(@NotNull Supplier<T> supplier) {
        if (isPrimary()) {
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
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    private boolean isPrimary() {
        return Bukkit.isPrimaryThread();
    }
}
