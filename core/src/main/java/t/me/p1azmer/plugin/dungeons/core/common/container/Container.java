package t.me.p1azmer.plugin.dungeons.core.common.container;

import org.jetbrains.annotations.NotNull;

public interface Container<T> {

    T get(@NotNull String id);

    <E extends T> E get(@NotNull String id, @NotNull Class<E> type);

    boolean contains(@NotNull String id);


}
