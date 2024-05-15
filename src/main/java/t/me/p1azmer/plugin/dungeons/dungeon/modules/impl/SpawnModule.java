package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonDespawnEvent;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonSpawnEvent;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.generator.RangeInfo;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/*
    Thank you very much for the code idea:
    discord: b3cksgold
 */
@Getter
public class SpawnModule extends AbstractModule {

  private boolean spawned;

  public SpawnModule(
    @NotNull Dungeon dungeon,
    @NotNull String id
  ) {
    super(dungeon, id, false, true);
  }

  @Override
  protected Predicate<Boolean> onLoad() {
    this.spawned = false;
    return aBoolean -> {
      DungeonStage stage = getDungeon().getStage();
      GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
      GenerationType generationType = generationSettings.getGenerationType();
      Optional<Location> spawnLocation = generationSettings.getSpawnLocation();
      boolean allowedWithGeneration = !generationType.isDynamic() && spawnLocation.isPresent();

      return stage.isCheck() && !isSpawned() || allowedWithGeneration;
    };
  }

  @Override
  protected void onShutdown() {
    this.spawned = false;
    GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
    GenerationType generationType = generationSettings.getGenerationType();
    if (generationType.isDynamic()) this.getDungeon().setLocation(null);
  }

  @Override
  public CompletableFuture<Boolean> onActivate(boolean force) {
    if (isSpawned()) {
      this.debug("Dungeon is already spawned on world");
      return CompletableFuture.completedFuture(force);
    }

    GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
    GenerationType generationType = generationSettings.getGenerationType();
    Optional<Location> spawnLocation = generationSettings.getSpawnLocation();

    if (!generationType.isDynamic()) {
      if (spawnLocation.isPresent()) {
        return CompletableFuture.completedFuture(this.spawn(spawnLocation.get()));
      }

      this.error("The location of the dungeon is not set in the generation settings");
      return CompletableFuture.completedFuture(false);
    }

    World world = this.getDungeon().getWorld();
    return CompletableFuture.supplyAsync(() -> {
      boolean result;
      int attempts = 0;
      System.currentTimeMillis();
      long ms;
      do {
        ms = System.currentTimeMillis();
        result = foundedRandomLocation(world, force);
        this.debug("Attempt: " + attempts + " | Result: " + result + " | Took: " + (System.currentTimeMillis() - ms) + "ms");
      } while (!result && attempts++ < 10);

      this.debug("Found location: Attempts: " + attempts + " | Result: " + result + " | Took: " + (System.currentTimeMillis() - ms) + "ms");
      return result;
    });
  }

  @Override
  public boolean onDeactivate(boolean force) {
    GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
    GenerationType generationType = generationSettings.getGenerationType();
    Optional<Location> spawnLocation = generationSettings.getSpawnLocation();
    Optional<SchematicModule> schematicModule = this.getManager().getModule(SchematicModule.class);

    if (!generationType.isDynamic() && spawnLocation.isPresent()) return false;
    if (schematicModule.isPresent() && !schematicModule.get().tryDeactivate(ActionType.FORCE)) return false;

    AsyncDungeonDespawnEvent event = new AsyncDungeonDespawnEvent(this.getDungeon());
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      this.debug("Unable to deactivate the '" + this.getId() + "' module due to an Event");
      return false;
    }

    this.spawned = false;
    return true;
  }

  public boolean spawn(@NotNull Location result) {
    AsyncDungeonSpawnEvent event = new AsyncDungeonSpawnEvent(this.getDungeon(), result);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      this.debug("Unable to spawn the '" + this.getId() + "' module due to an Event");
      return false;
    }
    return this.spawned = true;
  }

  // TODO: rewrite with chunks for optimization
  private boolean foundedRandomLocation(@NotNull World world, boolean force) {
    RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(getDungeon().getWorld().getName());
    if (rangeInfo == null) {
      this.error("Unable to start dungeon spawn '" + getDungeon().getId() + "' because the location generator for this '" + getDungeon().getWorld().getName() + "' world is not set!");
      return false;
    }

    boolean underground = this.getDungeon().getSchematicSettings().isUnderground();
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

    if (!force) {
      String returnError = "Spawn returned result false, because: ";
      RegionHandler handler = plugin().getRegionHandler();
      if (handler != null) {
        if (!handler.isValidLocation(result)) {
          return false;
        }
      }
      if (rangeInfo.isBiomesAsBlack()) {
        if (rangeInfo.getBiomes().contains(biome)) {
          this.debug(returnError + "Biomes contains biome " + biome.name());
          return false;
        }
      } else if (!rangeInfo.getBiomes().contains(biome)) {
        this.debug(returnError + "Biomes not contains biome " + biome.name());
        return false;
      }
      if (!result.getChunk().isLoaded()){
        if (onlyGeneratedChunks) {
          this.debug(returnError + "Chunk not loaded");
          return false;
        }
        world.loadChunk(result.getChunk());
      }
      if (rangeInfo.isMaterialsAsBlack()) {
        if (rangeInfo.getMaterials().contains(block.getType())) {
          this.debug(returnError + "Materials contains block " + block.getType().name());
          return false;
        }
      } else if (!rangeInfo.getMaterials().contains(block.getType())) {
        this.debug(returnError + "Materials not contains block " + block.getType().name());
        return false;
      }
    }

    return this.spawn(result);
  }
}