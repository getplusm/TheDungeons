package t.me.p1azmer.plugin.dungeons.integration.impl.region;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.region.Region;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.core.common.pair.Pair;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.settings.RegionSettings;

import java.util.Comparator;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorldGuardRegion implements RegionHandler {

    WorldGuard worldGuard = WorldGuard.getInstance();
    WorldGuardPlatform platform = worldGuard.getPlatform();

    Map<org.bukkit.World, World> adaptedWorlds = Maps.newHashMap();
    Map<Dungeon, Pair<ProtectedRegion, com.sk89q.worldedit.world.World>> protectedRegions = Maps.newConcurrentMap();

    @Override
    public void create(@NotNull Dungeon dungeon) {
        RegionSettings settings = dungeon.getSettingsContainer().getOptional(RegionSettings.class).orElse(null);
        if (settings == null) return;

        double regionRadius = settings.getRadius();
        Location location = dungeon.getLocation();
        if (location == null) return;

        org.bukkit.World world = location.getWorld();
        String regionName = settings.getRegionIdPrefix() + dungeon.getId();
        ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName,
                convertToSk89qBV(location.clone().add(-regionRadius, regionRadius, regionRadius)),
                convertToSk89qBV(location.clone().add(regionRadius, -regionRadius, -regionRadius)));

        com.sk89q.worldedit.world.World adaptedWorld = adaptedWorlds.computeIfAbsent(world, v -> BukkitAdapter.adapt(world));
        RegionManager regionManager = platform.getRegionContainer().get(adaptedWorld);
        if (regionManager == null) {
            throw new RuntimeException("Unable to get region manager for world " + world.getName());
        }

        regionManager.addRegion(protectedRegion);
        for (String flag : settings.getFlags()) {
            String command = "region flag -w " + world.getName() + " " + regionName + " " + flag;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        protectedRegions.put(dungeon, Pair.of(protectedRegion, adaptedWorld));
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Pair<ProtectedRegion, World> pair = protectedRegions.remove(dungeon);
        if (pair == null) return;

        ProtectedRegion protectedRegion = pair.getFirst();
        World world = pair.getSecond();

        RegionManager regionManager = platform.getRegionContainer().get(world);
        if (regionManager == null) return;

        regionManager.removeRegion(protectedRegion.getId());
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        org.bukkit.World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) return false;

        World world = adaptedWorlds.computeIfAbsent(bukkitWorld, v -> BukkitAdapter.adapt(bukkitWorld));
        BlockVector3 vector3 = BukkitAdapter.adapt(location).toVector().toBlockPoint();
        RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(world);
        if (regionManager == null) return false;

        ApplicableRegionSet set = regionManager.getApplicableRegions(vector3);
        // todo auto removal cache with caffeine
        return set.getRegions().stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null) == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull Region region) {
        return false;
    }

    private static BlockVector3 convertToSk89qBV(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }
}
