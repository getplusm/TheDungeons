package t.me.p1azmer.plugin.dungeons.api.schematic;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.io.File;

public interface SchematicHandler extends Loadable {
    boolean paste(@NotNull Dungeon dungeon, @NotNull File schematicFile);

    boolean undo(@NotNull Dungeon dungeon);

    boolean containsChestBlock(@NotNull Dungeon dungeon, @NotNull File schematicFile);

    int getAmountOfChestBlocks(@NotNull Dungeon dungeon, @NotNull File schematicFile);
}