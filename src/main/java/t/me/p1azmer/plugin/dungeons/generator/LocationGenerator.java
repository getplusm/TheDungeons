package t.me.p1azmer.plugin.dungeons.generator;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationGenerator {
    static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("TheDungeons Location Generator Worker #%s")
            .setUncaughtExceptionHandler((t, e) -> DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while generated location", e))
            .setDaemon(true)
            .build());
    List<Location> undergroundLocations = new ArrayList<>();
    List<Location> highLocations = new ArrayList<>();
    @Nullable RegionHandler regionHandler;

    public LocationGenerator(@Nullable RegionHandler regionHandler) {
        this.regionHandler = regionHandler;

        generateStartupLocations();
    }

    private void generateStartupLocations() {
        GeneratorConfig.LOCATION_SEARCH_RANGES.get().values().forEach(range -> {
            World world = range.getWorld().orElseThrow(() -> {
                return new IllegalArgumentException("World " + range.getWorldRaw() + " in range info not found");
            });
            generateTripleUndergroundLocations(world);
            generateTripleHighLocations(world);
        });
    }

    public @Nullable Location getRandomUndergroundLocation(@NotNull World world) {
        if (undergroundLocations.isEmpty()) {
            generateTripleUndergroundLocations(world);
            DungeonPlugin.getLog().severe("It seems that the list of generated locations is empty and the request was" +
                    " received before they were generated. Don't worry, new locations are already being created.");
        }
        Location location = getWithRemoveLastLocationFromList(undergroundLocations);
        if (undergroundLocations.size() < 2) {
            generateTripleUndergroundLocations(world);
        }
        return location;
    }

    public @Nullable Location getRandomHighLocation(@NotNull World world) {
        if (highLocations.isEmpty()) {
            generateTripleHighLocations(world);
            DungeonPlugin.getLog().severe("It seems that the list of generated locations is empty and the request was" +
                    " received before they were generated. Don't worry, new locations are already being created.");
            return null;
        }
        Location location = getWithRemoveLastLocationFromList(highLocations);
        if (highLocations.size() < 2) {
            generateTripleHighLocations(world);
        }
        return location;
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }

    private void generateTripleUndergroundLocations(@NotNull World world) {
        DungeonPlugin.getLog().info("Starting generation of underground locations for world `" + world.getName() + "`");
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> findRandomLocation(true, undergroundLocations, world, regionHandler));
        }
    }

    private void generateTripleHighLocations(@NotNull World world) {
        DungeonPlugin.getLog().info("Starting generation of highest locations for world `" + world.getName() + "`");
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> findRandomLocation(false, highLocations, world, regionHandler));
        }
    }

    private static void findRandomLocation(boolean underground, @NotNull List<Location> locations,
                                           @NotNull World world, @Nullable RegionHandler regionHandler) {
        RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(world.getName());
        if (rangeInfo == null) {
            throw new RuntimeException("No range info for world " + world.getName());
        }

        boolean generated = false;
        int attempts = 3;
        while (!generated && attempts > 0) {
            attempts--;
            long ms = System.currentTimeMillis();
            boolean onlyGeneratedChunks = rangeInfo.isOnlyGeneratedChunks();

            int originX = rangeInfo.getStartX();
            int originY = underground ? world.getMinHeight() : world.getMaxHeight();
            int originZ = rangeInfo.getStartZ();

            int minOffset = -rangeInfo.getDistanceMin();
            int maxOffset = rangeInfo.getDistanceMax();

            int direction = RANDOM.nextInt(0, 2);
            int randomX;

            if (direction == 0) {
                randomX = RANDOM.nextInt(originX + minOffset, originX + maxOffset + 1);
            } else {
                randomX = RANDOM.nextInt(originX - maxOffset, originX - minOffset + 1);
            }

            direction = RANDOM.nextInt(0, 2);
            int randomZ;
            if (direction == 0) {
                randomZ = RANDOM.nextInt(originZ + minOffset, originZ + maxOffset + 1);
            } else {
                randomZ = RANDOM.nextInt(originZ - maxOffset, originZ - minOffset + 1);
            }

            int modifiedY = originY;
            if (underground) {
                modifiedY += RANDOM.nextInt(Version.isAbove(Version.V1_18_R2) ? 30 : 10);
            }

            Location result = new Location(world, randomX, modifiedY, randomZ);
            if (!underground) result = world.getHighestBlockAt(result).getLocation();

            Block block = result.getBlock();
            Biome biome = block.getBiome();

            if (regionHandler != null) {
                if (!regionHandler.isValidLocationOrThrow(result)) continue;
            }

            if (!rangeInfo.getBiomes().isEmpty()) {
                if (rangeInfo.isBiomesAsBlack()) {
                    if (rangeInfo.getBiomes().contains(biome)) continue;
                } else if (!rangeInfo.getBiomes().contains(biome)) {
                    continue;
                }
            }
            if (!result.getChunk().isLoaded()) {
                if (onlyGeneratedChunks) continue;
            }
            if (rangeInfo.isMaterialsAsBlack()) {
                if (rangeInfo.getMaterials().contains(block.getType())) continue;
            } else if (!rangeInfo.getMaterials().contains(block.getType())) {
                continue;
            }

            generated = locations.add(result);
            DungeonPlugin.getLog().info("Generated new location " + result + " took " + (System.currentTimeMillis() - ms) + "ms");
        }
    }

    private static @NotNull Location getWithRemoveLastLocationFromList(@NotNull List<Location> list) {
        return list.remove(0);
    }
}
