package t.me.p1azmer.plugin.dungeons.api.integrations.hologram;


import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;

public interface HologramHandler {

    void create(@NotNull Dungeon dungeon);

    void delete(@NotNull Dungeon dungeon);

    void update(@NotNull ChestBlock chestBlock);

    default void createOrThrow(@NotNull Dungeon dungeon) {
        try {
            create(dungeon);
        } catch (Exception exception) {
            throw new RuntimeException("Got an exception while creating hologram for "
                    + dungeon.getId() + " dungeon", exception);
        }
    }

    default void deleteOrThrow(@NotNull Dungeon dungeon) {
        try {
            delete(dungeon);
        } catch (Exception exception) {
            throw new RuntimeException("Got an exception while deleting hologram for "
                    + dungeon.getId() + " dungeon", exception);
        }
    }
}