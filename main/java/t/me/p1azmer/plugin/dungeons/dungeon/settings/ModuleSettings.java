package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;

import java.util.HashMap;
import java.util.Map;

public class ModuleSettings implements IPlaceholderMap {

    private final Map<String, Boolean> moduleSettingsMap;
    private final PlaceholderMap placeholderMap;

    public ModuleSettings(@NonNull Map<String, Boolean> moduleSettingsMap) {
        this.moduleSettingsMap = moduleSettingsMap;
        this.placeholderMap = new PlaceholderMap();
    }

    public static ModuleSettings read(@NonNull JYML cfg, @NonNull String path) {
        Map<String, Boolean> settingMap = new HashMap<>();
        for (String moduleId : cfg.getSection(path + ".Map")) {
            settingMap.put(moduleId, cfg.getBoolean(path + ".Map." + moduleId, true));
        }
        return new ModuleSettings(settingMap);
    }

    public void write(@NonNull JYML cfg, @NonNull String path) {
        for (Map.Entry<String, Boolean> entry : this.getModuleSettingsMap().entrySet()) {
            cfg.set(path + ".Map." + entry.getKey(), entry.getValue());
        }
    }

    @NonNull
    public Map<String, Boolean> getModuleSettingsMap() {
        return moduleSettingsMap;
    }

    public boolean isEnabled(@NonNull String moduleId) {
        return this.getModuleSettingsMap().getOrDefault(moduleId, true);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
