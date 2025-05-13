package t.me.p1azmer.plugin.dungeons.core.common.container;

import java.util.Map;

public interface IClassContainer<T> {

    <O extends T> O get(Class<O> clazz);

    <O extends T> boolean contains(Class<O> clazz);

    Map<Class<? extends T>, ? extends T> getContainer();


}
