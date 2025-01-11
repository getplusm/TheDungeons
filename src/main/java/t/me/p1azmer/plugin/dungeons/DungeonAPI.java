package t.me.p1azmer.plugin.dungeons;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;

import java.util.Optional;

@UtilityClass
public class DungeonAPI {
    public final DungeonPlugin PLUGIN = DungeonPlugin.getPlugin(DungeonPlugin.class);

    @NotNull
    public DungeonManager getDungeonManager() {
        return PLUGIN.getDungeonManager();
    }

    @NotNull
    public MobManager getMobManager() {
        return PLUGIN.getMobManager();
    }

    @NotNull
    public ModuleManager getModuleManager(@NotNull Dungeon dungeon) {
        return dungeon.getModuleManager();
    }

    public Optional<AbstractModule> getModule(@NotNull Dungeon dungeon, @NotNull String name) {
        ModuleManager moduleManager = getModuleManager(dungeon);
        return Optional.ofNullable(moduleManager.getModule(name));
    }

    public <C extends AbstractModule> Optional<C> getModule(@NotNull Dungeon dungeon, @NotNull Class<C> clazz) {
        ModuleManager moduleManager = getModuleManager(dungeon);
        return moduleManager.getModule(clazz);
    }
}
