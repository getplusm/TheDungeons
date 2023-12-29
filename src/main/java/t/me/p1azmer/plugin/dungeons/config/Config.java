package t.me.p1azmer.plugin.dungeons.config;

import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.Colorizer;

public class Config {
    public static final String DIR_DUNGEONS = "/dungeons/";
    public static final String DIR_KEYS = "/keys/";
    public static final String DIR_SCHEMATICS = "/schematics/";

    public static final JOption<Boolean> DUNGEON_HOLD_KEY_TO_OPEN = JOption.create("Settings.Dungeon.Hold_Key_To_Open", false, "Sets whether players need to hold the key in their main hand to open the dungeon.");

    public static final JOption<String> EDITOR_TITLE_DUNGEON = JOption.create("Editor.Title.Dungeon", "Dungeon Settings", "Title of the dungeon editor menu")
            .mapReader(Colorizer::apply);
    public static final JOption<String> EDITOR_TITLE_MOB = JOption.create("Editor.Title.Mob", "Mobs Settings", "Title of the mobs editor menu")
            .mapReader(Colorizer::apply);

    public static final JOption<String> EDITOR_TITLE_KEY = JOption.create("Editor.Title.Key", "Key Settings", "Title of the key editor menu")
            .mapReader(Colorizer::apply);
    public static final JOption<Boolean> DEBUG = JOption.create("Settings.Debug.Other", false, "Enables plugin debugging");
    public static final JOption<Boolean> DEBUG_TICK = JOption.create("Settings.Debug.Tick", false, "Enables dungeon tick debugging");
    public static final JOption<Boolean> DEBUG_TICK_CHEST = JOption.create("Settings.Debug.Tick_Chest", false, "Enables dungeon chest tick debugging");
}