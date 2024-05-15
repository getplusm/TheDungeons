package t.me.p1azmer.plugin.dungeons.integration.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.data.KingdomsDataCenter;
import org.kingdoms.data.managers.LandManager;
import org.kingdoms.events.lands.ClaimLandEvent;
import org.kingdoms.events.lands.UnclaimLandEvent;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegionHandlerKingdoms implements RegionHandler {

    private KingdomPlayer kingdomPlayer;
    private LandManager landManager;
    private final UUID ownerId = UUID.fromString("1492a9a4-4277-4eb6-897a-b346d76bc1e0");
    private final UUID kdPlayer = UUID.fromString("1492a9a5-4277-4eb6-897b-b346d76bc1e0");
    private final DungeonPlugin plugin;
    private final Map<Dungeon, Land> claimMap = new ConcurrentHashMap<>();

    public RegionHandlerKingdoms(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.kingdomPlayer = KingdomsDataCenter.get().getKingdomPlayerManager().getData(kdPlayer);
        if (this.kingdomPlayer == null)
            this.kingdomPlayer = new KingdomPlayer(kdPlayer, ownerId, System.currentTimeMillis());
        if (this.kingdomPlayer.getKingdom() == null) {
            this.kingdomPlayer.joinKingdom(new Kingdom(kdPlayer, "PLAZMER_DUNGEON_KINGDOM"));
        }
        this.landManager = KingdomsDataCenter.get().getLandManager();
    }

    @Override
    public void shutdown() {
        this.claimMap.entrySet()
                .stream().filter(Objects::nonNull)
                .filter(entry -> entry.getKey().getLocation().isPresent() && this.landManager.isLoaded(SimpleChunkLocation.of(entry.getKey().getLocation().get())))
                .forEach(f -> {
                    this.landManager.delete(SimpleChunkLocation.of(f.getKey().getLocation().get()));
                });
        this.claimMap.clear();
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        Region region = dungeon.getRegion();
        Kingdom kingdom = kingdomPlayer.getKingdom();
        if (kingdom == null || kingdomPlayer == null || !region.isEnabled()) return;

        dungeon.getLocation().ifPresent(location -> {
            kingdom.claim(SimpleChunkLocation.of(location), kingdomPlayer, ClaimLandEvent.Reason.ADMIN);
            this.claimMap.put(dungeon, this.landManager.getData(SimpleChunkLocation.of(location)));
        });
        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Region region = dungeon.getRegion();
        if (!region.isCreated()) return;

        Land land = this.claimMap.get(dungeon);
        if (land == null) return;
        land.unclaim(kingdomPlayer, UnclaimLandEvent.Reason.ADMIN, false);

        this.claimMap.remove(dungeon, land);
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        Land land = this.landManager.getData(SimpleChunkLocation.of(location));
        return land == null || !land.isClaimed();
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull Region region) {
        World world = location.getWorld();
        if (world == null) return false;
        Land land = this.landManager.getData(SimpleChunkLocation.of(location));
        if (land == null || !land.isClaimed() || land.getKingdomId() == null) return false;
        return land.getKingdomId().equals(this.kingdomPlayer.getKingdomId());
    }
}
