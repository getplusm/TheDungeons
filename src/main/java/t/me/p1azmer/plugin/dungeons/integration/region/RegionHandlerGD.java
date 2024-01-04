package t.me.p1azmer.plugin.dungeons.integration.region;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.claim.ClaimResult;
import com.griefdefender.api.claim.ClaimTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegionHandlerGD implements RegionHandler {

    private Core griefDefender;
    private final UUID ownerId = UUID.fromString("1492a9a4-4277-4eb6-897a-b346d76bc1e0");
    private DungeonPlugin plugin;
    private Map<Dungeon, Claim> claimMap;

    public RegionHandlerGD(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.griefDefender = GriefDefender.getCore();
        this.claimMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.claimMap != null) {
            this.claimMap.values()
                    .stream().filter(Objects::nonNull)
                    .filter(Claim::isAdminClaim)
                    .forEach(f -> {
                        ClaimManager claimManager = this.griefDefender.getClaimManager(f.getWorldUniqueId());
                        if (claimManager != null)
                            claimManager.deleteClaim(f);
                    });
            this.claimMap.clear();
            this.claimMap = null;
        }
        if (this.plugin != null) {
            this.plugin = null;
        }
        if (this.griefDefender != null) {
            this.griefDefender = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        Region region = dungeon.getDungeonRegion();
        if (!region.isEnabled()) return;

        double regionRadius = region.getRadius();
        Location location = dungeon.getLocation();
        if (location == null) return;
        World world = dungeon.getWorld();

        ClaimManager claimManager = this.griefDefender.getClaimManager(world.getUID());
        if (claimManager == null) {
            DungeonStage.call(dungeon, DungeonStage.CANCELLED, "GriefDefender not found the claim manager at this world");
            return;
        }
        Location first = location.clone().add(-regionRadius, regionRadius, regionRadius);
        Location second = location.clone().add(regionRadius, -regionRadius, -regionRadius);
        ClaimResult result = Claim.builder()
                .world(world.getUID())
                .bounds(first.getBlockX(), first.getBlockX(), first.getBlockY(), second.getBlockY(), second.getBlockZ(), second.getBlockZ())
                .type(ClaimTypes.ADMIN)
                .expire(false)
                .denyMessages(false)
                .inherit(false)
                .build();
        if (!result.successful()) {
            plugin.error("Plugin GriefDefender did not create a region on the location");
        } else {
            this.claimMap.put(dungeon, result.getClaim());
        }
        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Region region = dungeon.getDungeonRegion();
        if (!region.isCreated()) return;

        Claim claim = this.claimMap.get(dungeon);
        if (claim == null) return;

        claim.getClaimManager().deleteClaim(claim);
        this.claimMap.remove(dungeon, claim);
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        ClaimManager claimManager = this.griefDefender.getClaimManager(location.getWorld().getUID());
        if (claimManager == null) return false;
        return claimManager.getClaimAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull Region region) {
        World world = location.getWorld();
        if (world == null) return false;
        ClaimManager claimManager = this.griefDefender.getClaimManager(location.getWorld().getUID());
        if (claimManager == null) return false;
        Claim claim = claimManager.getClaimAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (claim == null) return false;
        return claim.isAdminClaim();
    }
}
