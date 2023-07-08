package t.me.p1azmer.plugin.dungeons.config;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.plugin.dungeons.Placeholders;

public class Config {
    public static final String DIR_DUNGEONS = "/dungeons/";
    public static final String DIR_KEYS = "/keys/";
    public static final String DIR_SCHEMATICS = "/schematics/";


    //public static JOption<Integer> NOTIFY_TIME = JOption.create("Settings.Notify.Time", 60, "How many seconds before notifying players", "After this number, players will receive notifications in chat, boss bar, or action bar");
    public static JOption<Long> IDLE_BREAK = JOption.create("Settings.Idle.Break", 120L, "After how many seconds to break the dungeon if no one opened it");
    public static final JOption<Boolean> DUNGEON_HOLD_KEY_TO_OPEN = JOption.create("Settings.Dungeon.Hold_Key_To_Open", false, "Sets whether players need to hold the key in their main hand to open the dungeon.");

    public static JOption<String> BOSSBAR_TITLE = JOption.create("Settings.Notify.BossBar.Title", "Time until dungeon spawns: " + Placeholders.DUNGEON_WAIT_IN + " sec.", "Boss bar text");
    public static JOption<BarColor> BOSSBAR_COLOR = JOption.create("Settings.Notify.BossBar.Color", BarColor.class, BarColor.BLUE, "Boss bar color, available types:", String.join("\n", CollectionsUtil.getEnumsList(BarColor.class)));
    public static JOption<BarStyle> BOSSBAR_STYLE = JOption.create("Settings.Notify.BossBar.Style", BarStyle.class, BarStyle.SEGMENTED_12, "Boss bar style, available types:", String.join("\n- ", CollectionsUtil.getEnumsList(BarStyle.class)));

    public static final JOption<String> EDITOR_TITLE_CRATE = JOption.create("Editor.Title.Dungeon", "Dungeon Settings", "Title of the dungeon editor menu").mapReader(Colorizer::apply);

    public static final JOption<String> EDITOR_TITLE_KEY = JOption.create("Editor.Title.Key", "Key Settings", "Title of the key editor menu").mapReader(Colorizer::apply);
}