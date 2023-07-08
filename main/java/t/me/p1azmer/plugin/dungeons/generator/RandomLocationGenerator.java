package t.me.p1azmer.plugin.dungeons.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.hooks.external.WorldGuardHook;
import t.me.p1azmer.engine.utils.LocationUtil;

import java.util.Random;

public class RandomLocationGenerator {
    private static final int MAX_TRIES = 10;

    @Nullable
    public static Location getRandomLocation(World world) {
        int RADIUS = (int) world.getWorldBorder().getSize();
        Random random = new Random();
        int centerX = random.nextInt(RADIUS * 2 + 1) - RADIUS;
        int centerZ = random.nextInt(RADIUS * 2 + 1) - RADIUS;

        int tries = 0;
        while (tries < MAX_TRIES) {
            int offsetX = random.nextInt(RADIUS * 2 + 1) - RADIUS;
            int offsetZ = random.nextInt(RADIUS * 2 + 1) - RADIUS;

            int x = centerX + offsetX;
            int z = centerZ + offsetZ;
            int y = world.getHighestBlockYAt(x, z);

            Location location = LocationUtil.getFirstGroundBlock(new Location(world, x, y, z));
            if (isValidLocation(location)) {
                return location;
            }

            tries++;
        }
        return null;
    }

    private static boolean isValidLocation(Location location) {
        Block block = location.getBlock();
        if (WorldGuardHook.getProtectedRegion(location) != null)
            return false;
        return block.getType() == Material.AIR && block.getRelative(0, -1, 0).getType() != Material.WATER && block.getRelative(0, -1, 0).getType() != Material.LAVA;
    }
}