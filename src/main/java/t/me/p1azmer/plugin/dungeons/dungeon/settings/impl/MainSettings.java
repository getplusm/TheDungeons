package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders;

@Getter
@Setter
public class MainSettings extends AbstractSettings {
    private boolean enabled, clickTimer, letPlayersWhenClose;
    private int minimalOnline;

    public MainSettings(
            @NotNull Dungeon dungeon,
            boolean enabled,
            boolean clickTimer,
            boolean letPlayersWhenClose,
            int minimalOnline
    ) {
        super(dungeon);
        this.enabled = enabled;
        this.clickTimer = clickTimer;
        this.minimalOnline = minimalOnline;
        this.letPlayersWhenClose = letPlayersWhenClose;


        this.placeholders = new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.DUNGEON_SETTINGS_CLICK_TIMER, () -> LangManager.getBoolean(this.isClickTimer()))
                .add(Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE, () -> String.valueOf(this.getMinimalOnline()))
                .add(Placeholders.DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE, () -> LangManager.getBoolean(this.isLetPlayersWhenClose()))
        ;
    }

    @NotNull
    public static MainSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled", true); //
        boolean clickTimer = cfg.getBoolean(path + ".Click_Timer");
        boolean letPlayersWhenClose = cfg.getBoolean(path + ".Let_Players_When_Close", true);

        int minimalOnline = cfg.getInt(path + ".Minimal_Online");

        return new MainSettings(dungeon, enabled, clickTimer, letPlayersWhenClose, minimalOnline);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        cfg.set(path + ".Minimal_Online", this.getMinimalOnline());
        cfg.set(path + ".Click_Timer", this.isClickTimer());
        cfg.set(path + ".Let_Players_When_Close", this.isLetPlayersWhenClose());

        cfg.set(path + ".Mobs", null);
    }
}
