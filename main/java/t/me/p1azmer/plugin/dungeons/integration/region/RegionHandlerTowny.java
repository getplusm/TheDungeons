package t.me.p1azmer.plugin.dungeons.integration.region;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegionHandlerTowny implements RegionHandler {

    private TownyAPI townyAPI;
    private final UUID ownerId = UUID.fromString("1492a9a4-4277-4eb6-897a-b346d76bc1e0");
    private DungeonPlugin plugin;
    private Map<Dungeon, Town> claimMap;

    public RegionHandlerTowny(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.townyAPI = TownyAPI.getInstance();
        this.claimMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.claimMap != null) {
            this.claimMap.entrySet()
                    .stream().filter(Objects::nonNull)
                    .forEach(entry -> {
                        this.townyAPI.getDataSource().deleteTown(entry.getValue());
                        try {
                            TownyUniverse.getInstance().unregisterTown(entry.getValue());
                        } catch (NotRegisteredException ignored) {
                        }
                    });
            this.claimMap.clear();
            this.claimMap = null;
        }
        if (this.plugin != null) {
            this.plugin = null;
        }
        if (this.townyAPI != null) {
            this.townyAPI = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        DungeonRegion region = dungeon.getDungeonRegion();
        if (!region.isEnabled()) return;

        Location location = dungeon.getLocation();
        if (location == null) return;

        Town town = new Town(region.getName(), ownerId);

        if (dungeon.getDungeonCuboid() != null)
            dungeon.getDungeonCuboid().getBlocks().forEach(block -> {
                TownBlock townBlock = new TownBlock(WorldCoord.parseWorldCoord(block));
                townBlock.setTown(town, false);
            });

        this.claimMap.put(dungeon, town);
        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        DungeonRegion region = dungeon.getDungeonRegion();
        if (!region.isCreated()) return;

        Town town = this.claimMap.get(dungeon);
        if (town == null) return;
        this.townyAPI.getDataSource().deleteTown(town);
        try {
            TownyUniverse.getInstance().unregisterTown(town);
        } catch (NotRegisteredException ignored) {
        }

        this.claimMap.remove(dungeon, town);
    }

    @Override
    public boolean isValidLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        return this.townyAPI.getTownBlock(location) == null;
    }

    @Override
    public boolean isDungeonRegion(@NotNull Location location, @NotNull DungeonRegion dungeonRegion) {
        World world = location.getWorld();
        if (world == null) return false;
        TownBlock townBlock = this.townyAPI.getTownBlock(location);
        if (townBlock == null || townBlock.getTownOrNull() == null) return false;
        return townBlock.getTownOrNull().getUUID().equals(this.ownerId);
    }
}
