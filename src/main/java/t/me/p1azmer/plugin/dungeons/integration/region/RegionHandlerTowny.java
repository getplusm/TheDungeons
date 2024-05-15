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
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegionHandlerTowny implements RegionHandler {

    private TownyAPI townyAPI;
    private final UUID ownerId = UUID.fromString("1492a9a4-4277-4eb6-897a-b346d76bc1e0");
    private final DungeonPlugin plugin;
    private final Map<Dungeon, Town> claimMap = new ConcurrentHashMap<>();

    public RegionHandlerTowny(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.townyAPI = TownyAPI.getInstance();
    }

    @Override
    public void shutdown() {
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
    }

    @Override
    public void create(@NotNull Dungeon dungeon) {
        Region region = dungeon.getRegion();
        if (!region.isEnabled()) return;

        dungeon.getLocation().ifPresent(location -> {
            Town town = new Town(region.getName(), ownerId);

            dungeon.getDungeonCuboid().ifPresent(cuboid ->
                    cuboid.getBlocks().forEach(block -> {
                        TownBlock townBlock = new TownBlock(WorldCoord.parseWorldCoord(block));
                        townBlock.setTown(town, false);
                    })
            );
            this.claimMap.put(dungeon, town);
        });
        //result.claim.setPermission(this.ownerId, ClaimPermission.Build);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Region region = dungeon.getRegion();
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
    public boolean isDungeonRegion(@NotNull Location location, @NotNull Region region) {
        World world = location.getWorld();
        if (world == null) return false;
        TownBlock townBlock = this.townyAPI.getTownBlock(location);
        if (townBlock == null || townBlock.getTownOrNull() == null) return false;
        return townBlock.getTownOrNull().getUUID().equals(this.ownerId);
    }
}
