package t.me.p1azmer.plugin.dungeons.api.models.module.container;

import org.jetbrains.annotations.UnmodifiableView;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;

import java.util.Collection;
import java.util.Optional;

public interface ModuleContainer {
    Optional<AbstractModule> getOptional(Class<AbstractModule> clazz);

    default AbstractModule getOrThrow(Class<AbstractModule> clazz) {
        return getOptional(clazz).orElseThrow(() -> new NullPointerException("Module " + clazz.getSimpleName() + " not found!"));
    }

    @UnmodifiableView
    Collection<AbstractModule> getAll();
}
