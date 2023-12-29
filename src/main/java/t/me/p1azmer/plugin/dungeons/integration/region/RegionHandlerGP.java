package t.me.p1azmer.plugin.dungeons.integration.region;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RegionHandlerGP implements RegionHandler {
    private GriefPrevention griefPrevention;
    private DungeonPlugin plugin;
    private Map<Dungeon, Claim> claimMap;

    public RegionHandlerGP(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.griefPrevention = GriefPrevention.instance;
        this.claimMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.claimMap != null) {
            this.claimMap.values().forEach(f -> this.griefPrevention.dataStore.deleteClaim(f));
            this.claimMap.clear();
            this.claimMap = null;
        }
        if (this.plugin != null) {
            this.plugin = null;
        }
        if (this.griefPrevention != null) {
            this.griefPrevention = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        DungeonRegion region = dungeon.getDungeonRegion();
        if (!region.isEnabled()) return;

        double regionRadius = region.getRadius();
        Location location = dungeon.getLocation();
        if (location == null) return;
        org.bukkit.World world = dungeon.getWorld();

        Location first = location.clone().add(-regionRadius, regionRadius, regionRadius);
        Location second = location.clone().add(regionRadius, -regionRadius, -regionRadius);
        CreateClaimResult result = this.griefPrevention.dataStore.createClaim(world, first.getBlockX(), first.getBlockX(), first.getBlockY(), second.getBlockY(), second.getBlockZ(), second.getBlockZ(), null, null, (long) (new HashSet<>(this.griefPrevention.dataStore.getClaims()).size() + 1), null);
        if (!result.succeeded) {
            plugin.error("Plugin " + this.griefPrevention.getName() + " did not create a region on the location");
        } else {
            this.claimMap.put(dungeon, result.claim);

        }
        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        DungeonRegion region = dungeon.getDungeonRegion();
        if (!region.isCreated()) return;

        Claim claim = this.claimMap.get(dungeon);
        if (claim == null) return;

        this.griefPrevention.dataStore.deleteClaim(claim);
        this.claimMap.remove(dungeon, claim);
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        long ms = System.currentTimeMillis();
        Claim claim = this.griefPrevention.dataStore.getClaimAt(location, false, false, null);
        plugin.error("Valid took " + (System.currentTimeMillis()-ms) + "ms");
        return claim == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull DungeonRegion dungeonRegion) {
        Claim claim = this.griefPrevention.dataStore.getClaimAt(location, false, false, null);
        if (claim == null) return false;
        return claim.isAdminClaim();
    }
}
