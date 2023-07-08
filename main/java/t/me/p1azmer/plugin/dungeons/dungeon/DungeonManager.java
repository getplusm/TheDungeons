package t.me.p1azmer.plugin.dungeons.dungeon;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.hooks.external.WorldGuardHook;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonManager extends AbstractManager<DungeonPlugin> {
    private final Map<String, Dungeon> dungeonMap;

    public DungeonManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.dungeonMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getConfigManager().extractResources(Config.DIR_DUNGEONS);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_DUNGEONS, true)) {
            Dungeon dungeon = new Dungeon(plugin, cfg);
            if (dungeon.load()) {
                this.dungeonMap.put(dungeon.getId(), dungeon);
            } else this.plugin.error("Dungeon not loaded: '" + cfg.getFile().getName() + "'.");
        }
        this.plugin.info("Loaded " + this.getDungeonMap().size() + " dungeons.");

        this.addListener(new DungeonListener(this));
    }

    @Override
    protected void onShutdown() {
        this.dungeonMap.values().forEach(Dungeon::clear);
        this.dungeonMap.clear();
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getDungeonByKey(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_DUNGEONS, id + ".yml");
        Dungeon dungeon = new Dungeon(this.plugin, cfg);
        dungeon.setName("&a&l" + StringUtil.capitalizeUnderscored(dungeon.getId()) + " Данж");

        ItemStack item = new ItemStack(Material.SKULL_ITEM);
        ItemUtil.setSkullTexture(item, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZkN2ZkYjUwZjE0YzczMWM3MjdiMGUwZDE4OWI2YTg3NDMxOWZjMGQ3OWM4YTA5OWFjZmM3N2M3YjJkOTE5NiJ9fX0=");
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(dungeon.getName());
        });
        dungeon.addOpenMessage("&6Open in: " + Placeholders.DUNGEON_OPEN_IN);
        dungeon.addCloseMessage("&6Close in: " + Placeholders.DUNGEON_CLOSE_IN);
        dungeon.save();
        dungeon.load();

        this.getDungeonMap().put(dungeon.getId(), dungeon);
        return true;
    }

    public boolean delete(@NotNull Dungeon dungeon) {
        if (dungeon.getFile().delete()) {
            dungeon.clear();
            this.getDungeonMap().remove(dungeon.getId());
            return true;
        }
        return false;
    }

    @NotNull
    public List<String> getDungeonIds(boolean keyOnly) {
        return this.getDungeons().stream().filter(crate -> !crate.getKeyIds().isEmpty() || !keyOnly).map(Dungeon::getId).collect(Collectors.toList());
    }

    @NotNull
    public Map<String, Dungeon> getDungeonMap() {
        return this.dungeonMap;
    }

    @NotNull
    public Collection<Dungeon> getDungeons() {
        return this.getDungeonMap().values();
    }

    @Nullable
    public Dungeon getDungeonByKey(@NotNull String id) {
        return this.getDungeonMap().get(id.toLowerCase());
    }

    @Nullable
    public Dungeon getDungeonByBlock(@NotNull Block block) {
        return this.getDungeonByLocation(block.getLocation(), block);
    }

    @Nullable
    public Dungeon getDungeonByLocation(@NotNull Location loc, @NotNull Block block) {
        return this.getDungeons().stream().filter(dungeon -> {
            if (dungeon.getBlock() != null) {
                Block dungeonBlock = dungeon.getBlock();
                ProtectedRegion region = WorldGuardHook.getProtectedRegion(loc);
                if (region == null) {
                    return false;
                }
                if (!region.getId().equalsIgnoreCase("Dungeons_" + dungeon.getId() + "_" + plugin.getName())) {
                    return false;
                }

                return dungeonBlock.hasMetadata(dungeon.getId()) || dungeonBlock.equals(block) || dungeonBlock.getLocation().equals(loc) || dungeonBlock.getLocation().distance(loc) <= 1D;
            }
            return false;
        }).findFirst().orElse(null);
    }

    public boolean spawnDungeon(@NotNull Dungeon dungeon, @NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        if (dungeon.isSpawn()) return false;
        dungeon.spawn(location, true);
        return true;
    }

    public void interactDungeon(@NotNull Player player, @NotNull Dungeon dungeon, @NotNull Block block) {
        if (!block.hasMetadata(Keys.CHEST_BLOCK.getKey())) {
            return;
        }
        dungeon.open(player);
    }
}
