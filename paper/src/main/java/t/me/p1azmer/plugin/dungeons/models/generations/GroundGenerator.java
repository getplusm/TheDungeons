package t.me.p1azmer.plugin.dungeons.models.generations;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.types.GenerationType;
import t.me.p1azmer.plugin.dungeons.api.models.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.config.ConfigContainer;
import t.me.p1azmer.plugin.dungeons.config.configs.Config;
import t.me.p1azmer.plugin.dungeons.api.models.generator.WorldGenerationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroundGenerator implements LocationGenerator {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    ConfigContainer configContainer;
    RegionHandler regionHandler;
    ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("TheDungeons Ground Generator Worker #%s")
            .setUncaughtExceptionHandler((t, e) -> DungeonPlugin.getLog().log(Level.SEVERE,
                    "Got an exception while generated location", e))
            .setDaemon(true)
            .build());

    @Getter List<Location> locations = new ArrayList<>();

    public GroundGenerator(ConfigContainer configContainer, RegionHandler regionHandler) {
        this.configContainer = configContainer;
        this.regionHandler = regionHandler;
        generateStartupLocations();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void generateStartupLocations() {
        Config config = configContainer.getConfig();
        for (WorldGenerationInfo generationInfo : config.getWorldGenerationInfoList()) {
            World world = generationInfo.getWorldOrThrow();

            for (int i = 0; i < 3; i++) {
                executor.execute(() -> generateLocation(world, generationInfo));
            }
        }
    }

    @Override
    public @NotNull Location getRandomLocation(@NotNull World world) {
        if (locations.isEmpty()) {
            generateTripleUndergroundLocations(world);
            DungeonPlugin.getLog().severe("It seems that the list of generated locations is empty and the request was" +
                    " received before they were generated. Don't worry, new locations are already being created.");
        }
        Location location = getFirstPointFromList(locations);
        if (locations.size() < 2) generateTripleUndergroundLocations(world);

        return location;
    }

    private void generateTripleUndergroundLocations(@NotNull World world) {
        DungeonPlugin.getLog().info("Starting generation of ground locations for world `" + world.getName() + "`");
        Config config = configContainer.getConfig();
        List<WorldGenerationInfo> infoByWorld = config.getWorldGenerationInfoByWorld(world);
        for (int i = 0; i < 3; i++) {
            WorldGenerationInfo generationInfo = infoByWorld.get(RANDOM.nextInt(0, infoByWorld.size()));
            executor.execute(() -> generateLocation(world, generationInfo));
        }
    }

    private void generateLocation(World world, WorldGenerationInfo generationInfo) {
        boolean generated = false;
        int attempts = 3;
        while (!generated && attempts > 0) {
            attempts--;
            long ms = System.currentTimeMillis();
            Location result = findRandomLocation(world, generationInfo, GenerationType.GROUND, regionHandler);
            if (result == null) continue;

            generated = locations.add(result);
            DungeonPlugin.getLog().info("Generated new location " + result + " took " + (System.currentTimeMillis() - ms) + "ms");
        }
    }

    private static @NotNull Location getFirstPointFromList(@NotNull List<Location> list) {
        return list.remove(0);
    }
}
