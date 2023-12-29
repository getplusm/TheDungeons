package t.me.p1azmer.plugin.dungeons.api.hologram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

public interface HologramHandler extends Loadable {

    void create(@NotNull Dungeon dungeon, @Nullable ChestModule module);

    void delete(@NotNull Dungeon dungeon);

    void update(@NotNull DungeonChestBlock dungeonChestBlock);
}