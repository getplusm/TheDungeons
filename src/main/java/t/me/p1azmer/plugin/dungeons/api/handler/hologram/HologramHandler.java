package t.me.p1azmer.plugin.dungeons.api.handler.hologram;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

public interface HologramHandler extends Loadable {

    void create(@NotNull Dungeon dungeon, @NotNull ChestModule module);

    void delete(@NotNull Dungeon dungeon);

    void update(@NotNull ChestBlock chestBlock);
}