package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

@Getter
@Setter
public class PartySettings extends AbstractSettings {
    private boolean enabled;
    private int size;

    public PartySettings(
            @NotNull Dungeon dungeon,
            boolean enabled,
            int size
    ) {
        super(dungeon);
        this.enabled = enabled;
        this.size = size;
        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.PARTY_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.PARTY_SIZE, () -> String.valueOf(this.getSize()))
        ;
    }

    @NotNull
    public static PartySettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled");
        int size = cfg.getInt(path + ".Size");
        return new PartySettings(dungeon, enabled, size);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        cfg.set(path + ".Size", this.getSize());
    }
}
