package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessSettings extends AbstractSettings {
    boolean enabled;
    @Nullable String notAccessMessage;
    Set<String> proSkillAPIAccessClasses;

    public AccessSettings(@NotNull Dungeon dungeon, boolean enabled,
                          @NotNull Set<String> proSkillAPIAccessClasses, @Nullable String notAccessMessage) {
        super(dungeon);
        this.enabled = enabled;
        this.proSkillAPIAccessClasses = proSkillAPIAccessClasses;
        this.notAccessMessage = notAccessMessage;

        this.placeholders = new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_ACCESS_PSAPI_CLASSES, () -> String.join("\n", getProSkillAPIAccessClasses()))
                .add(Placeholders.DUNGEON_SETTINGS_ACCESS_ENABLED, () -> LangManager.getBoolean(isEnabled()))
                .add(Placeholders.DUNGEON_SETTINGS_ACCESS_NOT_ACCESS_MESSAGE, () -> Optional.ofNullable(getNotAccessMessage()).orElse(Colors2.RED + "Empty"))
        ;
    }

    @NotNull
    public static AccessSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".PSAPI.Enabled");
        Set<String> proSkillAPIAccessClasses = cfg.getStringSet(path + ".PSAPI.Access_Classes");
        String notAccessMessage = cfg.getString(path + ".PSAPI.Not_Access_Message");

        return new AccessSettings(dungeon, enabled, proSkillAPIAccessClasses, notAccessMessage);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".PSAPI.Enabled", isEnabled());
        cfg.set(path + ".PSAPI.Access_Classes", getProSkillAPIAccessClasses());
        cfg.set(path + ".PSAPI.Not_Access_Message", getNotAccessMessage());
    }
}
