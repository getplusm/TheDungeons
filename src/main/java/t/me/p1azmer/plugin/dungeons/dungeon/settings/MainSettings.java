package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MainSettings extends AbstractSettings implements IPlaceholderMap {
    private boolean enabled;
    private boolean clickTimer;
    private boolean letPlayersWhenClose;

    private int minimalOnline;
    private Map<String, Integer> mobMap;

    private final PlaceholderMap placeholderMap;

    public MainSettings(@NotNull Dungeon dungeon,
                        boolean enabled, boolean clickTimer, boolean letPlayersWhenClose,
                        int minimalOnline,
                        @NotNull Map<String, Integer> mobMap) {
        super(dungeon);
        this.dungeon = dungeon;
        this.enabled = enabled;
        this.clickTimer = clickTimer;
        this.minimalOnline = minimalOnline;
        this.letPlayersWhenClose = letPlayersWhenClose;
        this.mobMap = mobMap;


        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.DUNGEON_SETTINGS_CLICK_TIMER, () -> LangManager.getBoolean(this.isClickTimer()))
                .add(Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE, () -> String.valueOf(this.getMinimalOnline()))
                .add(Placeholders.DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE, () -> LangManager.getBoolean(this.isLetPlayersWhenClose()))
                .add(Placeholders.DUNGEON_SETTINGS_MOBS, () -> this.getMobMap().entrySet().stream()
                        .map(entry -> Colorizer.apply(Colors.LIGHT_YELLOW + entry.getKey() + ": " + entry.getValue())).collect(Collectors.joining("\n")))
        ;
    }

    @NotNull
    public static MainSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled", true); //
        boolean clickTimer = cfg.getBoolean(path + "Settings.Click_Timer");
        boolean letPlayersWhenClose = cfg.getBoolean(path + "Settings.Let_Players_When_Close", true);

        int minimalOnline = cfg.getInt(path + "Settings.Minimal_Online");

        Map<String, Integer> mobs = new HashMap<>();
        for (String mobId : cfg.getSection(path + "Mobs")) {
            int amount = cfg.getInt(path + "Mobs." + mobId + ".Amount");
            mobs.put(mobId, amount);
        }

        return new MainSettings(dungeon, enabled, clickTimer, letPlayersWhenClose, minimalOnline, mobs);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + "Enabled", this.isEnabled());
        cfg.set(path + "Settings.Minimal_Online", this.getMinimalOnline());
        cfg.set(path + "Settings.Click_Timer", this.isClickTimer());
        cfg.set(path + "Settings.Let_Players_When_Close", this.isLetPlayersWhenClose());

        cfg.set(path + "Mobs", null);
        this.getMobMap().forEach((mobId, amount) -> cfg.set(path + "Mobs." + mobId + ".Amount", amount));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isClickTimer() {
        return clickTimer;
    }

    public int getMinimalOnline() {
        return minimalOnline;
    }

    public boolean isLetPlayersWhenClose() {
        return letPlayersWhenClose;
    }

    @NotNull
    public Map<String, Integer> getMobMap() {
        return mobMap;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setMobMap(@NotNull Map<String, Integer> mobMap) {
        this.mobMap = mobMap;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setClickTimer(boolean clickTimer) {
        this.clickTimer = clickTimer;
    }

    public void setMinimalOnline(int minimalOnline) {
        this.minimalOnline = minimalOnline;
    }

    public void setLetPlayersWhenClose(boolean letPlayersWhenClose) {
        this.letPlayersWhenClose = letPlayersWhenClose;
    }
}
