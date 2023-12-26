package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;
import t.me.p1azmer.plugin.dungeons.generator.RangeInfo;
import t.me.p1azmer.plugin.dungeons.utils.DungeonCuboid;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class SpawnModule extends AbstractModule {

    private boolean spawned;
    private List<Block> blocks;

    public SpawnModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.spawned = false;
        this.blocks = new LinkedList<>();
        return aBoolean -> dungeon().getStage().isCheck() && !isSpawned();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(dungeon().getWorld().getName());
        if (rangeInfo == null) {
            plugin().error("Unable to start dungeon spawn '" + dungeon().getId() + "' because the location generator for this '" + dungeon().getWorld().getName() + "' world is not set!");
            return false;
        }

        World world = this.dungeon().getWorld();
        int originX = rangeInfo.getStartX();
        int originY = this.dungeon().getSettings().isUnderground() ? world.getMinHeight() : world.getMaxHeight();
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
        if (this.dungeon().getSettings().isUnderground())
            modifiedY += Rnd.get(Version.isAbove(Version.V1_18_R2) ? 30 : 10);

        Location result = this.dungeon().getSettings().isUnderground() ? new Location(world, randomX, modifiedY, randomZ) : LocationUtil.getFirstGroundBlock(new Location(world, randomX, modifiedY, randomZ));
        Block block = result.getBlock();
        Biome biome = block.getBiome();

        if (rangeInfo.isBiomesAsBlack()) {
            if (rangeInfo.getBiomes().contains(biome)) {
                plugin().error("biomes not contains biome " + biome.name());
                return false;
            }
        } else if (!rangeInfo.getBiomes().contains(biome)) {
            plugin().error("biomes not contains biome " + biome.name());
            return false;
        }
        if (rangeInfo.isMaterialsAsBlack()) {
            if (rangeInfo.getMaterials().contains(block.getType())) {
                plugin().error("materials contains block " + block.getType().name());
                return false;
            }
        } else if (!rangeInfo.getMaterials().contains(block.getType())) {
            plugin().error("materials not contains block " + block.getType().name());
            return false;
        }
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

    @Override
    public boolean onDeactivate() {
        this.spawned = false;
        return true;
    }

    public boolean isSpawned() {
        return spawned;
    }
}
