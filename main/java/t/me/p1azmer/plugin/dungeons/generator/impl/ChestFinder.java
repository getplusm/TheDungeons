package t.me.p1azmer.plugin.dungeons.generator.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/*
    Code refactoring by @b3cksgold
 */
public class ChestFinder {

    @NotNull
    public static CompletableFuture<List<Block>> findNearestChest(@NotNull Dungeon dungeon, @NotNull Location location, @NotNull World world, int blockAmount) {
        Material material = dungeon.getSettings().getChestMaterial();
        List<Block> blocks = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            if (dungeon.getSettings().getChestMaterial().isAir()) {
                throw new IllegalArgumentException("In the dungeon '" + dungeon.getId() + "' chest-block is set as air, change the settings!");
            }

            int searchRadius = dungeon.getSettings().getChestBlockSearchRadius();
            int radiusSquared = searchRadius * searchRadius;

            int currentRadius = 0;
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            for (int step = 1; step <= blockAmount; step++) {
                while (currentRadius <= searchRadius) {
                    for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                        for (int dy = -currentRadius; dy <= currentRadius; dy++) {
                            for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                                if (dx * dx + dy * dy + dz * dz <= radiusSquared) {
                                    if (blocks.size() == dungeon.getSettings().getChestBlockLimit() || blocks.size() == blockAmount) {
                                        break;
                                    }

                                    Block block = world.getBlockAt(x + dx, y + dy, z + dz);
                                    dungeon.plugin().info(block.getType().name() + " location block");
                                    if (!blocks.contains(block) && block.getType().equals(material)) {
                                        blocks.add(block);
                                    }
                                }
                                if (dz == -currentRadius) {
                                    dz = currentRadius;
                                } else {
                                    break;
                                }
                            }
                            if (dy == -currentRadius) {
                                dy = currentRadius;
                            } else {
                                break;
                            }
                        }
                        if (dx == -currentRadius) {
                            dx = currentRadius;
                        } else {
                            break;
                        }
                    }
                    currentRadius++;
                }
            }
            return blocks;
        });
    }
}