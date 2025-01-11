package t.me.p1azmer.plugin.dungeons.generator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationGenerator {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    BlockingQueue<Location> undergroundLocations = new LinkedBlockingQueue<>();
    BlockingQueue<Location> highLocations = new LinkedBlockingQueue<>();
    RegionHandler regionHandler;

    public LocationGenerator(@NotNull RegionHandler regionHandler) {
        this.regionHandler = regionHandler;

        generateStartupLocations();
    }

    private void generateStartupLocations() {
        GeneratorConfig.LOCATION_SEARCH_RANGES.get().values().forEach(range -> {
            World world = range.getWorld().orElseThrow(() -> {
                return new RuntimeException("World " + range.getWorldRaw() + " in range info not found");
            });
            generateTripleUndergroundLocations(world);
            generateTripleHighLocations(world);
        });
    }

    public @NotNull Location getRandomUndergroundLocation(@NotNull World world) {
        if (undergroundLocations.isEmpty()) {
            throw new RuntimeException("Underground locations queue is empty");
        }
        Location location = undergroundLocations.poll();
        if (undergroundLocations.isEmpty()) {
            generateTripleUndergroundLocations(world);
        }
        return location;
    }

    public @NotNull Location getRandomHighLocation(@NotNull World world) {
        if (highLocations.isEmpty()) {
            throw new RuntimeException("Highest locations queue is empty");
        }
        Location location = highLocations.poll();
        if (highLocations.isEmpty()) {
            generateTripleHighLocations(world);
        }
        return location;
    }

    private void generateTripleUndergroundLocations(@NotNull World world) {
        DungeonPlugin.getLog().info("Starting generation of underground locations for world `" + world.getName() + "`");
        for (int i = 0; i < 3; i++) {
            scheduler.execute(() -> {
                findRandomLocation(true, undergroundLocations, world, regionHandler);
            });
        }
    }

    private void generateTripleHighLocations(@NotNull World world) {
        DungeonPlugin.getLog().info("Starting generation of highest locations for world `" + world.getName() + "`");
        for (int i = 0; i < 3; i++) {
            scheduler.execute(() -> {
                findRandomLocation(false, highLocations, world, regionHandler);
            });
        }
    }

    private static void findRandomLocation(boolean underground, @NotNull BlockingQueue<Location> cache,
                                           @NotNull World world, @Nullable RegionHandler regionHandler) {
        RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(world.getName());
        if (rangeInfo == null) {
            throw new RuntimeException("No range info for world " + world.getName());
        }

        boolean generated = false;
        int attempts = 0;
        while (!generated && attempts < 10) {
            attempts++;
            boolean onlyGeneratedChunks = rangeInfo.isOnlyGeneratedChunks();

            int originX = rangeInfo.getStartX();
            int originY = underground ? world.getMinHeight() : world.getMaxHeight();
            int originZ = rangeInfo.getStartZ();

            int minOffset = -rangeInfo.getDistanceMin();
            int maxOffset = rangeInfo.getDistanceMax();

            // #####################################################
            // ############   [X and Z Randomization]   ############
            int direction = Rnd.get(0, 2);
            int randomX;

            // decide if positive or negative
            if (direction == 0) {
                randomX = Rnd.get(originX + minOffset, originX + maxOffset + 1);
            } else {
                randomX = Rnd.get(originX - maxOffset, originX - minOffset + 1);
            }

            direction = Rnd.get(0, 2);
            int randomZ;

            if (direction == 0) {
                randomZ = Rnd.get(originZ + minOffset, originZ + maxOffset + 1);
            } else {
                randomZ = Rnd.get(originZ - maxOffset, originZ - minOffset + 1);
            }

            // ############   [X and Z Randomization]   ############
            // #####################################################

            // X and Z are randomized, now just an example for handling Y

            int modifiedY = originY;
            if (underground)
                modifiedY += Rnd.get(Version.isAbove(Version.V1_18_R2) ? 30 : 10);

            Location result = new Location(world, randomX, modifiedY, randomZ);

            if (!underground) result = world.getHighestBlockAt(result).getLocation();

            Block block = result.getBlock();
            Biome biome = block.getBiome();

            String returnError = "Spawn returned result false, because: ";
            if (regionHandler != null) {
                if (!regionHandler.isValidLocation(result)) {
                    continue;
                }
            }
            if (rangeInfo.isBiomesAsBlack()) {
                if (rangeInfo.getBiomes().contains(biome)) {
                    DungeonPlugin.getLog().severe(returnError + "Biomes contains biome " + biome.translationKey());
                    continue;
                }
            } else if (!rangeInfo.getBiomes().contains(biome)) {
                DungeonPlugin.getLog().severe(returnError + "Biomes not contains biome " + biome.translationKey());
                continue;
            }
            if (!result.getChunk().isLoaded()) {
                if (onlyGeneratedChunks) {
                    DungeonPlugin.getLog().severe(returnError + "Chunk not loaded");
                    continue;
                }
            }
            if (rangeInfo.isMaterialsAsBlack()) {
                if (rangeInfo.getMaterials().contains(block.getType())) {
                    DungeonPlugin.getLog().severe(returnError + "Materials contains block " + block.getType().name());
                    continue;
                }
            } else if (!rangeInfo.getMaterials().contains(block.getType())) {
                DungeonPlugin.getLog().severe(returnError + "Materials not contains block " + block.getType().name());
                continue;
            }

            generated = cache.add(result);
        }
    }
}
