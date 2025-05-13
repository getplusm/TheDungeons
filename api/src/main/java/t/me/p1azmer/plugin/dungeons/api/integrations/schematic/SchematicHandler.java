package t.me.p1azmer.plugin.dungeons.api.integrations.schematic;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;

import java.io.File;

public interface SchematicHandler {

    boolean paste(Dungeon dungeon, @NotNull File schematicFile);

    boolean undo(Dungeon dungeon);

    void shutdown();

    int getChestBlockCount(Dungeon dungeon, @NotNull File schematicFile);

    default boolean containsChestBlock(Dungeon dungeon, @NotNull File schematicFile) {
        return getChestBlockCount(dungeon, schematicFile) > 0;
    }
}