package t.me.p1azmer.plugin.dungeons.api.integrations.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.region.Region;

public interface RegionHandler {

    void create(@NotNull Dungeon dungeon);

    void delete(@NotNull Dungeon dungeon);

    boolean isValidLocation(@NotNull Location location);

    boolean isDungeonRegion(@NotNull Location location, @NotNull Region region);

    default void createOrThrow(@NotNull Dungeon dungeon) {
        try {
            create(dungeon);
        } catch (Exception exception) {
            throw new RuntimeException("Got an exception while creating dungeon region", exception);
        }
    }

    default boolean isValidLocationOrThrow(@NotNull Location location) {
        try {
            return isValidLocation(location);
        } catch (Exception exception) {
            throw new RuntimeException("Got an exception while validating location", exception);
        }
    }
}