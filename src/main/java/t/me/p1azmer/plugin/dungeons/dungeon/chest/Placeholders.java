package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import t.me.p1azmer.plugin.dungeons.dungeon.chest.state.ChestState;

import java.util.function.Function;

public class Placeholders extends t.me.p1azmer.plugin.dungeons.dungeon.Placeholders {
    public static final Function<ChestState, String> DUNGEON_CHEST_STATE_TIME = var -> "%dungeon_chest_state_" + var + "_time%";
    public static final String DUNGEON_CHEST_STATE_NAME = "%dungeon_state_name%";
    public static final String DUNGEON_CHEST_NEXT_STATE_IN = "%dungeon_chest_next_state_in%";
    public static final String EDITOR_STATE_TIME = "%dungeon_editor_state_time%";
}
