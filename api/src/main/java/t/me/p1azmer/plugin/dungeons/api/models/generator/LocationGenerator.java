package t.me.p1azmer.plugin.dungeons.api.models.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.types.GenerationType;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public interface LocationGenerator {
    ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @NotNull Location getRandomLocation(World world);

    default @Nullable Location findRandomLocation(World world, WorldGenerationInfo generationInfo,
                                                  GenerationType generationType, RegionHandler regionHandler) {
        if (generationType.equals(GenerationType.STATIC)) {
            throw new IllegalArgumentException("Static generation type not supported here!");
        }

        boolean onlyGeneratedChunks = generationInfo.isOnlyGeneratedChunks();
        boolean underground = generationType.equals(GenerationType.UNDERGROUND);

        int originX = generationInfo.getStartX();
        int originY = underground ? world.getMinHeight() : world.getMaxHeight();
        int originZ = generationInfo.getStartZ();

        int minOffset = -generationInfo.getDistanceMin();
        int maxOffset = generationInfo.getDistanceMax();

        int randomX = generateRandomCoordinate(originX, minOffset, maxOffset);
        int randomZ = generateRandomCoordinate(originZ, minOffset, maxOffset);

        int modifiedY = originY;
        if (underground) {
            modifiedY += RANDOM.nextInt(15);
        }

        Location result = new Location(world, randomX, modifiedY, randomZ);
        if (!underground) result = world.getHighestBlockAt(result).getLocation();

        Block block = result.getBlock();
        Material blockType = block.getType();
        Biome biome = block.getBiome();

        if (regionHandler != null) {
            if (!regionHandler.isValidLocationOrThrow(result)) return null;
        }

        if (!generationInfo.getBiomes().isEmpty()) {
            if (generationInfo.isBiomesAsBlack()) {
                if (generationInfo.getBiomes().contains(biome.getKey().getKey())) return null;
            } else if (!generationInfo.getBiomes().contains(biome.getKey().getKey())) {
                return null;
            }
        }
        if (!result.getChunk().isLoaded()) {
            if (onlyGeneratedChunks) return null;

        }
        if (generationInfo.isMaterialsAsBlack()) {
            if (generationInfo.getMaterials().contains(blockType)) return null;
        } else if (!generationInfo.getMaterials().contains(blockType)) {
            return null;
        }

        return result;
    }

    private static int generateRandomCoordinate(int origin, int minOffset, int maxOffset) {
        int range = maxOffset - minOffset;
        return origin + minOffset + RANDOM.nextInt(range + 1);
    }
}
