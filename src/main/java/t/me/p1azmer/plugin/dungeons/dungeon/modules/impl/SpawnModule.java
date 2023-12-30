package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonDeleteEvent;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonSpawnEvent;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.generator.RangeInfo;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;
import t.me.p1azmer.plugin.dungeons.utils.DungeonCuboid;

import java.util.function.Predicate;

/*
    Thank you very much for the code idea:
    discord: b3cksgold
 */
public class SpawnModule extends AbstractModule {

    private boolean spawned;

    public SpawnModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.spawned = false;
        return aBoolean -> dungeon().getStage().isCheck() && !isSpawned();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(dungeon().getWorld().getName());
        if (rangeInfo == null) {
            this.error("Unable to start dungeon spawn '" + dungeon().getId() + "' because the location generator for this '" + dungeon().getWorld().getName() + "' world is not set!");
            return false;
        }

        World world = this.dungeon().getWorld();
        int originX = rangeInfo.getStartX();
        int originY = this.dungeon().getSchematicSettings().isUnderground() ? world.getMinHeight() : world.getMaxHeight();
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
        if (this.dungeon().getSchematicSettings().isUnderground())
            modifiedY += Rnd.get(Version.isAbove(Version.V1_18_R2) ? 30 : 10);

        Location result = this.dungeon().getSchematicSettings().isUnderground() ? new Location(world, randomX, modifiedY, randomZ) : LocationUtil.getFirstGroundBlock(new Location(world, randomX, modifiedY, randomZ));
        Block block = result.getBlock();
        Biome biome = block.getBiome();

        RegionHandler handler = plugin().getRegionHandler();
        if (handler != null){
            if (!handler.isValidLocation(result)){
                return false;
            }
        }
        if (rangeInfo.isBiomesAsBlack()) {
            if (rangeInfo.getBiomes().contains(biome)) {
                this.debug("Biomes contains biome " + biome.name());
                return false;
            }
        } else if (!rangeInfo.getBiomes().contains(biome)) {
            this.debug("Biomes not contains biome " + biome.name());
            return false;
        }
        if (rangeInfo.isMaterialsAsBlack()) {
            if (rangeInfo.getMaterials().contains(block.getType())) {
                this.debug("Materials contains block " + block.getType().name());
                return false;
            }
        } else if (!rangeInfo.getMaterials().contains(block.getType())) {
            this.debug("Materials not contains block " + block.getType().name());
            return false;
        }
        return this.spawn(result);
    }

    @Override
    public boolean onDeactivate() {
        if (dungeon().getModuleManager().getModule(SchematicModule.class).isPresent() && !dungeon().getModuleManager().getModule(SchematicModule.class).get().onDeactivate()) return false;

        DungeonDeleteEvent event = new DungeonDeleteEvent(this.dungeon());
        plugin().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.debug("Unable to deactivate the '"+this.getId()+"' module due to an Event");
            return false;
        }

        this.spawned = false;
        this.dungeon().setLocation(null);
        return true;
    }

    public boolean spawn(@NotNull Location result){
        DungeonSpawnEvent event = new DungeonSpawnEvent(this.dungeon(), result);
        plugin().getPluginManager().callEvent(event);
        if (event.isCancelled()){
            this.error("Cancelled by Event");
            return false;
        }
        result = event.getLocation();
        this.dungeon().setLocation(result);

        Location lowerLocation = new Location(result.getWorld(), result.getBlockX(), result.getBlockY(), result.getBlockZ());
        Location upperLocation = new Location(result.getWorld(), result.getBlockX(), result.getBlockY(), result.getBlockZ());
        int size = dungeon().getDungeonRegion().getRadius();

        lowerLocation.subtract(size, size, size);
        upperLocation.add(size, size, size);

        if (lowerLocation.getY() > upperLocation.getY()) {
            double temp = lowerLocation.getY();
            lowerLocation.setY(upperLocation.getY());
            upperLocation.setY(temp);
        }
        DungeonCuboid cuboid = new DungeonCuboid(lowerLocation, upperLocation);
        this.dungeon().setDungeonCuboid(cuboid);
        this.spawned = true;
        return true;
    }

    public boolean isSpawned() {
        return spawned;
    }
}
