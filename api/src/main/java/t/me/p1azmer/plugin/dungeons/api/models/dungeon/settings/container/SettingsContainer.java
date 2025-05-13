package t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.container;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;

import java.util.Optional;

public interface SettingsContainer {

    void reloadAll();

    <T extends AbstractSetting<T>> Optional<T> getOptional(Class<T> clazz);

    default @NotNull <T extends AbstractSetting<T>> T getOrThrow(Class<T> clazz) {
        return getOptional(clazz).orElseThrow(() -> new RuntimeException("Not found settings with " + clazz.getSimpleName() + " class"));
    }
}
