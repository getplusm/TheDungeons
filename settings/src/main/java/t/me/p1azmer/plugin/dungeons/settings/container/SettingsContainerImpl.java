package t.me.p1azmer.plugin.dungeons.settings.container;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.container.SettingsContainer;
import t.me.p1azmer.plugin.dungeons.core.common.container.ClassContainer;
import t.me.p1azmer.plugin.dungeons.settings.*;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingsContainerImpl extends ClassContainer<AbstractSetting<?>> implements SettingsContainer {

    File path;

    public SettingsContainerImpl(File path) {
        super(Maps.newConcurrentMap());
        this.path = path;

        loadAll();
    }

    private void loadAll() {
        String absolutePath = path.getAbsolutePath();
        loadSettings(
                new StateSettings(absolutePath),
                new AccessSettings(absolutePath),
                new RegionSettings(absolutePath),
                new AnnounceSettings(absolutePath),
                new SchematicSettings(absolutePath)
        );
    }

    @Override
    public void reloadAll() {
        String absolutePath = path.getAbsolutePath();
        loadSettings(
                new StateSettings(absolutePath),
                new AccessSettings(absolutePath),
                new RegionSettings(absolutePath),
                new AnnounceSettings(absolutePath),
                new SchematicSettings(absolutePath)
        );
    }

    @SuppressWarnings("unchecked")
    private void putAndLoad(AbstractSetting<?> setting) {
        synchronized (container) {
            container.put((Class<? extends AbstractSetting<?>>) setting.getClass(), setting);
            setting.loadOrCreateConfig();
        }
    }

    private void loadSettings(AbstractSetting<?>... settings) {
        Arrays.stream(settings).forEach(this::putAndLoad);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractSetting<T>> Optional<T> getOptional(Class<T> clazz) {
        return Optional.ofNullable((T) container.get(clazz));
    }
}
