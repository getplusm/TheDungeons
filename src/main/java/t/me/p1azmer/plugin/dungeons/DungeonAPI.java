package t.me.p1azmer.plugin.dungeons;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;

import java.util.Optional;

public class DungeonAPI {
    public static final DungeonPlugin PLUGIN = DungeonPlugin.getPlugin(DungeonPlugin.class);


    @NotNull
    public static DungeonManager getDungeonManager(){
        return PLUGIN.getDungeonManager();
    }

    @NotNull
    public static ModuleManager getModuleManager(@NotNull Dungeon dungeon){
        return dungeon.getModuleManager();
    }

    public static Optional<AbstractModule> getModule(@NotNull Dungeon dungeon, @NotNull String name){
        ModuleManager moduleManager = getModuleManager(dungeon);
        return Optional.ofNullable(moduleManager.getModule(name));
    }

    public static <C extends AbstractModule> Optional<C> getModule(@NotNull Dungeon dungeon, @NotNull Class<C> clazz){
        ModuleManager moduleManager = getModuleManager(dungeon);
        return moduleManager.getModule(clazz);
    }
}
