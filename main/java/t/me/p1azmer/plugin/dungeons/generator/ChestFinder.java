package t.me.p1azmer.plugin.dungeons.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.Placeholders;

import java.util.Objects;

public class ChestFinder {
    private static final int SEARCH_RADIUS = 5;

    public static Block findNearestChest(Location location, Material material) {
        int radiusSquared = SEARCH_RADIUS * SEARCH_RADIUS;
        int currentRadius = 1;

        while (currentRadius <= SEARCH_RADIUS) {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dy = -currentRadius; dy <= currentRadius; dy++) {
                    for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                        if (dx * dx + dy * dy + dz * dz <= radiusSquared) {
                            Block block = Objects.requireNonNull(location.getWorld(), "World at location '"+Placeholders.LOCATION.replacer(location)+"' not found!").getBlockAt(x + dx, y + dy, z + dz);
                            if (block.getType().equals(material)) {
                                return block;
                            }
                        }
                    }
                }
            }

            currentRadius++;
        }

        DungeonAPI.PLUGIN.error("Cannot find the chest block at location="+ Placeholders.LOCATION.replacer(location).apply("%location_x%, %location_y%, %location_z%, %location_world%"));
        return null;
    }
}