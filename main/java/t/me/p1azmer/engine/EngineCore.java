package t.me.p1azmer.engine;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;

public class EngineCore {


    @NotNull
    public static DungeonPlugin get() {
        return DungeonAPI.PLUGIN;
    }

}