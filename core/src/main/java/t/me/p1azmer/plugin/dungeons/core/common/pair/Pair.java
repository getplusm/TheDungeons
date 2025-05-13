package t.me.p1azmer.plugin.dungeons.core.common.pair;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;

public class Pair<F, S> {
    private final F first;
    private final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public Pair<F, S> withFirst(F first) {
        return new Pair<>(first, second);
    }

    public Pair<F, S> withSecond(S second) {
        return new Pair<>(first, second);
    }

    public Map.Entry<F, S> toEntry() {
        return new AbstractMap.SimpleEntry<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
