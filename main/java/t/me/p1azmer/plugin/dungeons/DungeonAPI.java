package t.me.p1azmer.plugin.dungeons;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;

public class DungeonAPI {

    public static final DungeonPlugin PLUGIN = DungeonPlugin.getPlugin(DungeonPlugin.class);

    @NotNull
    public static DungeonManager getDungeonManager(){
        return PLUGIN.getDungeonManager();
    }
}
