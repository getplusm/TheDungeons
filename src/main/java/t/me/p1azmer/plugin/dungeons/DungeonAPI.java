package t.me.p1azmer.plugin.dungeons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;

public class DungeonAPI {

    public static final DungeonPlugin PLUGIN = DungeonPlugin.getPlugin(DungeonPlugin.class);

    @NotNull
    public static DungeonManager getDungeonManager(){
        return PLUGIN.getDungeonManager();
    }

    @NotNull
    public static ModuleManager getDungeonModule(@NotNull Dungeon dungeon){
        return dungeon.getModuleManager();
    }
}
