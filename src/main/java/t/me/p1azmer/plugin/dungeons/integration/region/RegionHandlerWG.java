package t.me.p1azmer.plugin.dungeons.integration.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Region;

import java.util.Comparator;
import java.util.Objects;

public class RegionHandlerWG implements RegionHandler {

    private WorldGuard worldGuard;
    private WorldGuardPlatform platform;

    @Override
    public void setup() {
        this.worldGuard = WorldGuard.getInstance();
        this.platform = this.worldGuard.getPlatform();
    }

    @Override
    public void shutdown() {
        if (this.worldGuard != null) {
            this.worldGuard = null;
        }
        if (this.platform != null) {
            this.platform = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        Region region = dungeon.getDungeonRegion();
        if (!region.isEnabled()) return;

        double regionRadius = region.getRadius();
        Location location = dungeon.getLocation();
        if (location == null) return;
        org.bukkit.World world = dungeon.getWorld();
        if (world == null) return;
        String regionName = region.getNameRaw();

        ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName, convertToSk89qBV(location.clone().add(-regionRadius, regionRadius, regionRadius)), convertToSk89qBV(location.clone().add(regionRadius, -regionRadius, -regionRadius)));
        Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world))).addRegion(protectedRegion);
        for (String flag : region.getFlags()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region flag -w " + world.getName() + " " + regionName + " " + flag);
        }
        region.setCreated(true);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Region region = dungeon.getDungeonRegion();
        if (!region.isCreated()) return;

        World world = BukkitAdapter.adapt(dungeon.getWorld());
        RegionManager regionManager = this.platform.getRegionContainer().get(world);
        if (regionManager == null) return;

        ProtectedRegion protectedRegion = regionManager.getRegion(region.getNameRaw());
        if (protectedRegion == null) return;

        regionManager.removeRegion(protectedRegion.getId());
    }

    public BlockVector3 convertToSk89qBV(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        org.bukkit.World world = location.getWorld();
        if (world == null) return false;

        com.sk89q.worldedit.world.World sworld = BukkitAdapter.adapt(world);
        BlockVector3 vector3 = BukkitAdapter.adapt(location).toVector().toBlockPoint();
        RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(sworld);
        if (regionManager == null) return false;

        ApplicableRegionSet set = regionManager.getApplicableRegions(vector3);
        return set.getRegions().stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null) == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull Region region) {
        org.bukkit.World world = location.getWorld();
        if (world == null) return false;

        com.sk89q.worldedit.world.World sworld = BukkitAdapter.adapt(world);
        BlockVector3 vector3 = BukkitAdapter.adapt(location).toVector().toBlockPoint();
        RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(sworld);
        if (regionManager == null) return false;
        ApplicableRegionSet set = regionManager.getApplicableRegions(vector3);
        ProtectedRegion protectedRegion = set.getRegions().stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null);
        if (protectedRegion == null) return false;

       return protectedRegion.getId().equals(region.getNameRaw().toLowerCase());
    }
}
