package t.me.p1azmer.plugin.dungeons.integration.region;

import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.protectionblocks.ProtectionAPI;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.utils.Cuboid;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegionHandlerPB implements RegionHandler {

    private RegionManager regionManager;
    private DungeonPlugin plugin;
    private Map<Dungeon, Region> claimMap;

    public RegionHandlerPB(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.regionManager = ProtectionAPI.PLUGIN.getRegionManager();
        this.claimMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.claimMap != null) {
            this.claimMap.values()
                    .stream().filter(Objects::nonNull)
                    .forEach(f -> {
                        this.regionManager.deleteRegion(f, false);
                    });
            this.claimMap.clear();
            this.claimMap = null;
        }
        if (this.plugin != null) {
            this.plugin = null;
        }
        if (this.regionManager != null) {
            this.regionManager = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        DungeonRegion dungeonRegion = dungeon.getDungeonRegion();
        if (!dungeonRegion.isEnabled()) return;

        double regionRadius = dungeonRegion.getRadius();
        Location location = dungeon.getLocation();
        if (location == null) return;
        World world = dungeon.getWorld();

        String regionId = "Dungeons_" + UUID.randomUUID();
        if (!this.regionManager.create(regionId)) return;
        Region region = this.regionManager.getRegions().stream().filter(f -> f.getId().equals(regionId)).findFirst().orElse(null);
        if (region == null) return;

        Location first = location.clone().add(-regionRadius, regionRadius, regionRadius);
        Location second = location.clone().add(regionRadius, -regionRadius, -regionRadius);
        region.setCuboid(new Cuboid(first, second));
        // TODO set the region block (rewrite in ProtectionBlocks)
        this.claimMap.put(dungeon, region);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        DungeonRegion region = dungeon.getDungeonRegion();
        if (!region.isCreated()) return;

        Region claim = this.claimMap.get(dungeon);
        if (claim == null) return;

        this.regionManager.deleteRegion(claim, false);
        this.claimMap.remove(dungeon, claim);
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        Region region = this.regionManager.getRegionByLocation(location);
        return region == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull DungeonRegion dungeonRegion) {
        return this.regionManager.getRegionByLocation(location) != null;
    }
}
