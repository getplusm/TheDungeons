package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.module.AbstractModule;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ModuleSettings extends AbstractSettings {
    private final Map<String, Boolean> moduleSettingsMap;

    public ModuleSettings(
            @NotNull Dungeon dungeon,
            @NonNull Map<String, Boolean> moduleSettingsMap
    ) {
        super(dungeon);
        this.moduleSettingsMap = moduleSettingsMap;
        this.placeholders = new PlaceholderMap();
    }

    @NotNull
    public static ModuleSettings read(@NotNull Dungeon dungeon, @NonNull JYML cfg, @NonNull String path) {
        Map<String, Boolean> settingMap = new HashMap<>();
        for (String moduleId : cfg.getSection(path + ".Map")) {
            settingMap.put(moduleId, cfg.getBoolean(path + ".Map." + moduleId, true));
        }
        return new ModuleSettings(dungeon, settingMap);
    }

    public void write(@NonNull JYML cfg, @NonNull String path) {
        for (Map.Entry<String, Boolean> entry : this.getModuleSettingsMap().entrySet()) {
            cfg.set(path + ".Map." + entry.getKey(), entry.getValue());
        }
    }

    public void setEnabled(@NotNull String moduleId, boolean value) {
        this.getModuleSettingsMap().put(moduleId, value);
    }

    public boolean isEnabled(@NonNull String moduleId) {
        return this.getModuleSettingsMap().getOrDefault(moduleId, true);
    }

    public boolean isEnabled(@NonNull AbstractModule module) {
        return this.isEnabled(module.getId());
    }
}
