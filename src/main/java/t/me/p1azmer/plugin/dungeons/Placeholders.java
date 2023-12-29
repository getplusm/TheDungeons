package t.me.p1azmer.plugin.dungeons;

import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;

import java.util.function.Function;

public class Placeholders extends t.me.p1azmer.engine.utils.Placeholders {

    public static final String GENERIC_AMOUNT = "%amount%";
    public static final String GENERIC_TIME = "%time%";
    public static final String GENERIC_HANDLER = "%handler%";

    public static final String DUNGEON_NAME = "%dungeon_name%";
    public static final String DUNGEON_ID = "%dungeon_id%";
    public static final String DUNGEON_WORLD = "%dungeon_world%";
    public static final String DUNGEON_KEY_IDS = "%dungeon_key_ids%";
    public static final String DUNGEON_KEY_NAMES = "%dungeon_key_names%";
    public static final String DUNGEON_NEXT_STAGE_IN = "%dungeon_next_stage_in%";
    public static final String EDITOR_HOLOGRAM_TEXT = "%dungeon_editor_hologram_text%";
    public static final Function<DungeonChestState, String> DUNGEON_HOLOGRAM_MESSAGES = var -> "%dungeon_hologram_" + var + "_messages%";
    public static final String DUNGEON_HOLOGRAM_CHEST_OFFSET_Y = "%dungeon_hologram_chest_offset_y%";
    // schematics
    public static final String SCHEMATICS_LIST = "%schematics_list%";
    public static final String SCHEMATICS_IGNORE_AIR = "%schematics_ignore_air%";

    // settings
    public static final String DUNGEON_SETTINGS_ENABLED = "%dungeon_enabled%";
    public static final String DUNGEON_SETTINGS_MINIMAL_ONLINE = "%dungeon_minimal_online%";
    public static final String DUNGEON_SETTINGS_REFRESH = "%dungeon_refresh_time%";
    public static final String DUNGEON_SETTINGS_CLICK_TIMER = "%dungeon_click_timer%";
    public static final String DUNGEON_SETTINGS_BIG_CHEST = "%dungeon_big_chest%";
    public static final String DUNGEON_SETTINGS_CHEST_BLOCK_LIMIT = "%dungeon_chest_block_limit%";
    public static final String DUNGEON_SETTINGS_SEPARATE_CHEST_BLOCK = "%dungeon_separate_chest_block_gui%";
    public static final String DUNGEON_SETTINGS_RANDOM_SLOTS = "%dungeon_random_slots%";
    public static final String DUNGEON_SETTINGS_UNDERGROUND = "%dungeon_underground%";
    public static final String DUNGEON_SETTINGS_OPEN_TYPE = "%dungeon_open_type%";
    public static final String DUNGEON_SETTINGS_CHEST_MATERIAL = "%dungeon_chest_block%";
    public static final String DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE = "%dungeon_let_players_when_close%";
    public static final String DUNGEON_SETTINGS_USE_ONE_KEY_FOR_CHEST = "%dungeon_use_one_key_for_chest%";
    public static final String DUNGEON_SETTINGS_MOBS = "%dungeon_settings_mobs%";

    // chest states
    public static final Function<DungeonChestState, String> DUNGEON_CHEST_STATE_TIME = var -> "%dungeon_chest_state_" + var + "_time%";
    public static final String DUNGEON_CHEST_STATE_NAME = "%dungeon_state_name%";
    public static final String DUNGEON_CHEST_NEXT_STATE_IN = "%dungeon_chest_next_state_in%";
    public static final String EDITOR_STATE_TIME = "%dungeon_editor_state_time%";

    // modules
    public static final String MODULE_ID = "%module_id%";
    public static final String EDITOR_MODULE_ENABLED = "%editor_module_enabled%";

    // stages
    public static final String EDITOR_STAGE_COMMANDS = "%dungeon_editor_stage_commands%";
    public static final String EDITOR_STAGE_ANNOUNCE_MESSAGE = "%dungeon_editor_stage_announce_message%";
    public static final String EDITOR_STAGE_ANNOUNCE_TIMES = "%dungeon_editor_stage_announce_times";
    public static final String EDITOR_STAGE_TIME = "%dungeon_editor_stage_time";
    public static final String EDITOR_STAGE_NAME = "%dungeon_editor_stage_name";
    // announce
    public static final String ANNOUNCE_GLOBAL = "%dungeon_announce_global%";

    // key
    public static final String KEY_ID = "%key_id%";
    public static final String KEY_NAME = "%key_name%";
    public static final String KEY_ITEM_NAME = "%key_item_name%";


    // reward
    public static final String REWARD_ID = "%reward_id%";
    public static final String REWARD_NAME = "%reward_name%";
    public static final String REWARD_CHANCE = "%reward_chance%";
    public static final String REWARD_ITEM_NAME = "%reward_preview_name%";
    public static final String REWARD_ITEM_LORE = "%reward_preview_lore%";
    public static final String REWARD_MAX_AMOUNT = "%reward_win_limit_amount%";
    public static final String REWARD_MIN_AMOUNT = "%reward_win_limit_cooldown%";


    // region
    public static final String REGION_ENABLED = "%region_enabled%";
    public static final String REGION_IGNORE_AIR_BLOCKS = "%region_ignore_air_blocks%";
    public static final String REGION_NAME = "%region_name%";
    public static final String REGION_RADIUS = "%region_radius%";
    public static final String REGION_FLAGS = "%region_flags%";


    // effect
    public static final String EFFECT_NAME = "%effect_name%";
    public static final String EFFECT_DURATION = "%effect_duration%";
    public static final String EFFECT_AMPLIFIER = "%effect_amplifier%";

    // mobs
    public static final String MOB_ID = "%mob_id%";
    public static final String MOB_NAME = "%mob_name%";
    public static final String MOB_NAME_VISIBLE = "%mob_name_visible%";
    public static final String MOB_ENTITY_TYPE = "%mob_entity_type%";
    public static final String MOB_ATTRIBUTES_BASE = "%mob_attributes_base%";
    public static final String MOB_SPAWN_CHANCE = "%mob_spawn_chance%";
    public static final String MOB_ENABLED_SPAWN = "%mob_spawn_enabled%";
    public static final String MOB_SILENT = "%mob_silent%";
    public static final String MOB_RIDER_ID = "%mob_rider_id%";

    public static final String MOB_POTION_EFFECT_VALUE = "%mob_potion_effect_value%";
    public static final String MOB_POTION_EFFECT_DURATION = "%mob_potion_effect_duration%";
    public static final String MOB_STYLE_TYPE = "%mob_style_type%";
    public static final String MOB_STYLE_VALUE = "%mob_style_value%";

    // party
    public static final String PARTY_ENABLED = "%party_enabled%";
    public static final String PARTY_SIZE = "%party_size%";

    

}

/*


В будущем, добавить лицо данжа и голограмму на вход с эффектом. Допустим, чтобы если данж закрыт, мы писали об этом в голограмме и подсвечивали локацию необходимую для игроков
 */