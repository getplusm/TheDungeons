package t.me.p1azmer.plugin.dungeons.dungeon;

import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.ChestState;

import java.util.function.Function;

public class Placeholders extends t.me.p1azmer.plugin.dungeons.Placeholders {

    public static final String DUNGEON_NAME = "%dungeon_name%";
    public static final String DUNGEON_ID = "%dungeon_id%";
    public static final String DUNGEON_WORLD = "%dungeon_world%";
    public static final String DUNGEON_KEY_IDS = "%dungeon_key_ids%";
    public static final String DUNGEON_KEY_NAMES = "%dungeon_key_names%";
    public static final String DUNGEON_NEXT_STAGE_IN = "%dungeon_next_stage_in%";
    public static final String EDITOR_HOLOGRAM_TEXT = "%dungeon_editor_hologram_text%";
    public static final Function<ChestState, String> DUNGEON_HOLOGRAM_MESSAGES = var -> "%dungeon_hologram_" + var + "_messages%";
    public static final String DUNGEON_HOLOGRAM_CHEST_OFFSET_Y = "%dungeon_hologram_chest_offset_y%";

    // party
    public static final String PARTY_ENABLED = "%party_enabled%";
    public static final String PARTY_SIZE = "%party_size%";

    // schematics
    public static final String SCHEMATICS_LIST = "%schematics_list%";
    public static final String SCHEMATICS_IGNORE_AIR = "%schematics_ignore_air%";
    public static final String SCHEMATICS_UNDERGROUND = "%schematics_underground%";
}
