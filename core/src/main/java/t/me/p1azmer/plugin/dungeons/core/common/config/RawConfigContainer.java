package t.me.p1azmer.plugin.dungeons.core.common.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import t.me.p1azmer.plugin.dungeons.core.common.config.interfaces.IConfigContainer;
import t.me.p1azmer.plugin.dungeons.core.common.container.ClassContainer;

import java.io.File;
import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class RawConfigContainer extends ClassContainer<ConfigHolder<?>> implements IConfigContainer {

    File path;

    protected File toFile(String path) {
        return new File(this.path, path);
    }

    protected void loadOrCreateAndReplace() {
        getContainer().forEach((clazz, configHolder) -> {
            synchronized (container) {
                container.put(clazz, configHolder.loadOrCreateConfig());
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void loadConfig(ConfigHolder<?> configHolder) {
        synchronized (container) {
            container.put((Class<? extends ConfigHolder<?>>) configHolder.getClass(), configHolder.loadOrCreateConfig());
        }
    }

    protected void loadConfigs(ConfigHolder<?>... configs) {
        Arrays.stream(configs).forEach(this::loadConfig);
    }
}
