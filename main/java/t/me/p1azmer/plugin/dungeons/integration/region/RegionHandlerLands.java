package t.me.p1azmer.plugin.dungeons.integration.region;

//import me.angeschossen.lands.api.LandsIntegration;
//import me.angeschossen.lands.api.land.Land;
//import me.ryanhamshire.GriefPrevention.Claim;
//import me.ryanhamshire.GriefPrevention.CreateClaimResult;
//import me.ryanhamshire.GriefPrevention.GriefPrevention;
//import org.bukkit.Location;
//import org.jetbrains.annotations.NotNull;
//import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
//import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
//import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
//import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//
//public class RegionHandlerLands implements RegionHandler {
//
//    private LandsIntegration landsIntegration;
//    private DungeonPlugin plugin;
//    private Map<Dungeon, Land> claimMap;
//
//    public RegionHandlerLands(@NotNull DungeonPlugin plugin) {
//        this.plugin = plugin;
//    }
//
//    @Override
//    public void setup() {
//        this.landsIntegration = LandsIntegration.of(this.plugin);
//        this.claimMap = new HashMap<>();
//    }
//
//    @Override
//    public void shutdown() {
//        if (this.claimMap != null) {
//            this.claimMap.values().forEach(f -> f.);
//            this.claimMap.clear();
//            this.claimMap = null;
//        }
//        if (this.plugin != null) {
//            this.plugin = null;
//        }
//        if (this.landsIntegration != null) {
//            this.landsIntegration = null;
//        }
//    }
//
//    @Override
//    public void create(@NotNull Dungeon dungeon) {
//        DungeonRegion region = dungeon.getDungeonRegion();
//        if (!region.isEnabled()) return;
//
//        double regionRadius = region.getRadius();
//        Location location = dungeon.getLocation();
//        if (location == null) return;
//        org.bukkit.World world = dungeon.getWorld();
//        if (world == null) return;
//
//
//        Location first = location.clone().add(-regionRadius, regionRadius, regionRadius);
//        Location second = location.clone().add(regionRadius, -regionRadius, -regionRadius);
//        Land land = this.landsIntegration.getWorld(world).getArea(0,0,0).
//        CreateClaimResult result = this.griefPrevention.dataStore.createClaim(world, first.getBlockX(), first.getBlockX(), first.getBlockY(), second.getBlockY(), second.getBlockZ(), second.getBlockZ(), null, null, (long) (new HashSet<>(this.griefPrevention.dataStore.getClaims()).size() + 1), null);
//        if (!result.succeeded) {
//            plugin.error("Plugin " + this.griefPrevention.getName() + " did not create a region on the location");
//        } else {
//            this.claimMap.put(dungeon, result.claim);
//
//        }
//        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
//    }
//
//    @Override
//    public void delete(@NotNull Dungeon dungeon) {
//        DungeonRegion region = dungeon.getDungeonRegion();
//        if (!region.isCreated()) return;
//        Claim claim = this.claimMap.get(dungeon);
//        if (claim == null) return;
//
//        this.griefPrevention.dataStore.deleteClaim(claim);
//        this.claimMap.remove(dungeon, claim);
//    }
//}
