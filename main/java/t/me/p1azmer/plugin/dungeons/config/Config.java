package t.me.p1azmer.plugin.dungeons.config;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Config {
    public static final String DIR_DUNGEONS = "/dungeons/";
    public static final String DIR_KEYS = "/keys/";
    public static final String DIR_SCHEMATICS = "/schematics/";

    public static JOption<Long> IDLE_BREAK = JOption.create("Settings.Idle.Break", 120L, "After how many seconds to break the dungeon if no one opened it");
    public static final JOption<Boolean> DUNGEON_HOLD_KEY_TO_OPEN = JOption.create("Settings.Dungeon.Hold_Key_To_Open", false, "Sets whether players need to hold the key in their main hand to open the dungeon.");

    public static JOption<Boolean> BOSSBAR_ENABLED = JOption.create("Settings.Notify.BossBar.Enabled", true, "Sets whether the BossBar will be used as a notification about the appearance of a dungeon");
    public static JOption<String> BOSSBAR_TITLE = JOption.create("Settings.Notify.BossBar.Title", "#faebf0Time until dungeon spawns: " + Placeholders.DUNGEON_NAME + " #ffe4e1" + Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME + " #faebf0sec.", "Boss bar text");
    public static JOption<BarColor> BOSSBAR_COLOR = new JOption<>("Settings.Notify.BossBar.Color",
            (cfg, path, def) -> StringUtil.getEnum(cfg.getString(path, null), BarColor.class).orElse(BarColor.BLUE),
            BarColor.BLUE,
            "BossBar Color",
            "Allowed values: " + Arrays.stream(BarColor.values()).map(Enum::name).collect(Collectors.joining(", "))
    ).setWriter((cfg, path, set) -> cfg.set(path, set.name()));
    public static JOption<BarStyle> BOSSBAR_STYLE = new JOption<>("Settings.Notify.BossBar.Style",
            (cfg, path, def) -> StringUtil.getEnum(cfg.getString(path, null), BarStyle.class).orElse(BarStyle.SEGMENTED_12),
            BarStyle.SEGMENTED_12,
            "BossBar Style",
            "Allowed values: " + Arrays.stream(BarStyle.values()).map(Enum::name).collect(Collectors.joining(", "))
    ).setWriter((cfg, path, set) -> cfg.set(path, set.name()));

    public static final JOption<String> EDITOR_TITLE_DUNGEON = JOption.create("Editor.Title.Dungeon", "Dungeon Settings", "Title of the dungeon editor menu").mapReader(Colorizer::apply);
    public static final JOption<String> EDITOR_TITLE_MOB = JOption.create("Editor.Title.Mob", "Mobs Settings", "Title of the mobs editor menu").mapReader(Colorizer::apply);

    public static final JOption<String> EDITOR_TITLE_KEY = JOption.create("Editor.Title.Key", "Key Settings", "Title of the key editor menu").mapReader(Colorizer::apply);
    public static final JOption<Boolean> DEBUG = JOption.create("Settings.Debug.Other", false, "Enables plugin debugging");
    public static final JOption<Boolean> DEBUG_TICK = JOption.create("Settings.Debug.Tick", false, "Enables dungeon tick debugging");
    public static final JOption<Boolean> DEBUG_TICK_CHEST = JOption.create("Settings.Debug.Tick_Chest", false, "Enables dungeon chest tick debugging");
}