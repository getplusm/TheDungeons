package t.me.p1azmer.plugin.dungeons.core.common.container;

import java.util.HashMap;
import java.util.Map;

public abstract class ClassContainer<T> implements IClassContainer<T> {

    protected final Map<Class<? extends T>, T> container;

    public ClassContainer(Map<Class<? extends T>, T> container) {
        this.container = container;
    }

    public ClassContainer() {
        this(new HashMap<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O extends T> O get(Class<O> clazz) {
        return (O) container.get(clazz);
    }

    public void put(Class<? extends T> clazz, T object) {
        container.put(clazz, object);
    }

    @Override
    public <O extends T> boolean contains(Class<O> clazz) {
        return get(clazz) != null;
    }

    @Override
    public Map<Class<? extends T>, ? extends T> getContainer() {
        return new HashMap<>(container);
    }
}

