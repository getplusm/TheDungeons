package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public class PartySettings extends AbstractSettings implements IPlaceholderMap {
    private boolean enabled;
    private int size;

    private final PlaceholderMap placeholderMap;

    public PartySettings(@NotNull Dungeon dungeon, boolean enabled, int size) {
        super(dungeon);
        this.dungeon = dungeon;
        this.enabled = enabled;
        this.size = size;
        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.PARTY_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.PARTY_SIZE, () -> String.valueOf(this.getSize()))
        ;
    }

    public static PartySettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled");
        int size = cfg.getInt(path + ".Size");
        return new PartySettings(dungeon, enabled, size);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        cfg.set(path + ".Size", this.getSize());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSize() {
        return size;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
