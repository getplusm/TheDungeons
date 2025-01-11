package t.me.p1azmer.plugin.dungeons.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Cuboid {

    Location min;
    Location max;

    public Cuboid(@NotNull Location loc1, @NotNull Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.min = new Location(loc1.getWorld(), minX, minY, minZ);
        this.max = new Location(loc1.getWorld(), maxX, maxY, maxZ);
    }

    public boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null || !world.equals(this.min.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        int minY = this.min.getBlockY();
        int maxY = this.max.getBlockY();
        return x >= min.getBlockX() && x <= max.getBlockX() &&
                y >= minY && y <= maxY &&
                z >= min.getBlockZ() && z <= max.getBlockZ();
    }

    @NotNull
    public Set<Block> getBlocks() {
        Set<Block> blockSet = new HashSet<>(this.getSize());
        World world = this.getMax().getWorld();
        if (world == null) return blockSet;

        for (int x = this.min.getBlockX(); x <= this.max.getBlockX(); ++x) {
            for (int y = this.min.getBlockY(); y <= this.max.getBlockY(); ++y) {
                for (int z = this.min.getBlockZ(); z <= this.max.getBlockZ(); ++z) {
                    blockSet.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blockSet;
    }

    public int getSize() {
        int dx = max.getBlockX() - min.getBlockX() + 1;
        int dy = max.getBlockY() - min.getBlockY() + 1;
        int dz = max.getBlockZ() - min.getBlockZ() + 1;
        return dx * dy * dz;
    }
}