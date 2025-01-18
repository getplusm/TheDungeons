package t.me.p1azmer.plugin.dungeons.dungeon;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.listener.DungeonListener;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.SpawnModule;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.SchematicSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;
import t.me.p1azmer.plugin.dungeons.integration.region.RegionHandlerWG;
import t.me.p1azmer.plugin.dungeons.scheduler.ThreadSync;
import t.me.p1azmer.plugin.dungeons.task.DungeonTickTask;
import t.me.p1azmer.plugin.dungeons.utils.Cuboid;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DungeonManager extends AbstractManager<DungeonPlugin> {
    Map<String, Dungeon> dungeonMap = new ConcurrentHashMap<>();
    LocationGenerator locationGenerator;
    ThreadSync threadSync;
    @NonFinal
    DungeonTickTask dungeonTickTask;

    public DungeonManager(@NotNull DungeonPlugin plugin,
                          @NotNull LocationGenerator locationGenerator, @NotNull ThreadSync threadSync) {
        super(plugin);
        this.locationGenerator = locationGenerator;
        this.threadSync = threadSync;
    }

    @Override
    protected void onLoad() {
        RegionHandler regionHandler = plugin.getRegionHandler();

        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            this.plugin.getConfig().initializeOptions(GeneratorConfig.class);
            this.plugin.getConfigManager().extractResources(Config.DIR_DUNGEONS);

            for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_DUNGEONS, true)) {
                Dungeon dungeon = new Dungeon(this, cfg, locationGenerator, threadSync);
                if (dungeon.load()) {
                    this.dungeonMap.put(dungeon.getId(), dungeon);
                    if (regionHandler != null && regionHandler.getClass().equals(RegionHandlerWG.class)) {
                        SchematicSettings schematicSettings = dungeon.getSchematicSettings();
                        Region dungeonRegion = dungeon.getRegion();

                        if (schematicSettings.isUnderground() && dungeonRegion.isEnabled() && !dungeonRegion.getFlags().contains("build")) {
                            plugin.error("Please note that the dungeon '" + dungeon.getId() + "' is set to be underground, but its region does not have building rights!");
                        }
                    }
                    dungeon.getModuleManager().setup();
                } else this.plugin.error("Dungeon not loaded: '" + cfg.getFile().getName() + "'.");
            }
            this.plugin.info("Loaded " + this.getDungeonMap().size() + " dungeons.");
        });

        this.addListener(new DungeonListener(this));
        this.dungeonTickTask = new DungeonTickTask(this);
    }

    @Override
    protected void onShutdown() {
        try {
            this.dungeonMap.values().forEach(dungeon -> {
                dungeon.clear();
                ModuleManager moduleManager = dungeon.getModuleManager();
                moduleManager.shutdown();
                dungeon.setModuleManager(null);
            });
            this.dungeonMap.clear();
            if (this.dungeonTickTask != null) {
                this.dungeonTickTask.shutdown();
                this.dungeonTickTask = null;
            }
        } catch (RuntimeException exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while shutting down the dungeon manager");
        }
    }

    public boolean create(@NotNull String id) {
        try {
            id = StringUtil.lowerCaseUnderscore(id);
            if (this.getDungeonById(id) != null) {
                return false;
            }

            JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_DUNGEONS, id + ".yml");
            Dungeon dungeon = new Dungeon(this, cfg, locationGenerator, threadSync);
            dungeon.setName("&a&l" + StringUtil.capitalizeUnderscored(dungeon.getId()) + " Dungeon");
            dungeon.setWorld(plugin.getServer().getWorlds()
                    .stream()
                    .filter(f -> f.getEnvironment().equals(World.Environment.NORMAL))
                    .findFirst()
                    .orElseThrow());
            dungeon.save();
            dungeon.load();

            this.getDungeonMap().put(dungeon.getId(), dungeon);
            return true;
        } catch (RuntimeException exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while creating a new dungeon", exception);
            return false;
        }
    }

    public void delete(@NotNull Dungeon dungeon) {
        try {
            if (dungeon.getFile().delete()) {
                dungeon.clear();
                this.getDungeonMap().remove(dungeon.getId());
            }
        } catch (RuntimeException exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while deleting a dungeon", exception);
        }
    }

    @NotNull
    public List<String> getDungeonIds(boolean keyOnly) {
        return this.getDungeons()
                .stream()
                .filter(crate -> !crate.getKeyIds().isEmpty() || !keyOnly)
                .map(Dungeon::getId)
                .toList();
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
    public Dungeon getDungeonById(@NotNull String id) {
        return this.getDungeonMap().get(id.toLowerCase());
    }

    @Nullable
    public Dungeon getDungeonByBlock(@NotNull Block block) {
        return this.getDungeonByLocation(block.getLocation(), block);
    }

    @Nullable
    public Dungeon getDungeonByLocation(@NotNull Location location, @NotNull Block block) {
        try {
            return this.getDungeons().stream().filter(dungeon -> {
                ModuleManager moduleManager = dungeon.getModuleManager();
                ChestModule module = moduleManager.getModule(ChestModule.class).orElse(null);
                Block dungeonBlock = module != null ? module.getBlock(location).orElse(null) : null;

                Cuboid dungeonCuboid = dungeon.getDungeonCuboid().orElse(null);
                RegionHandler regionHandler = plugin.getRegionHandler();
                Region dungeonRegion = dungeon.getRegion();

                return (dungeonCuboid != null && dungeonCuboid.contains(location))
                        || (dungeonBlock != null
                        && (dungeonBlock.hasMetadata(dungeon.getId())
                        || dungeonBlock.equals(block)
                        || dungeonBlock.getLocation().equals(location)
                        || dungeonBlock.getLocation().distance(location) <= 1D))
                        || (regionHandler != null && dungeonRegion.isEnabled()
                        && regionHandler.isDungeonRegion(location, dungeonRegion));
            }).findFirst().orElse(null);
        } catch (RuntimeException exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while trying find a dungeon by location", exception);
            return null;
        }
    }

    @NotNull
    public List<Dungeon> getActiveDungeons() {
        return this.getDungeons()
                .stream()
                .filter(dungeon -> {
                    Optional<SpawnModule> spawnModule = dungeon.getModuleManager().getModule(SpawnModule.class);
                    DungeonStage dungeonStage = dungeon.getStage();
                    return spawnModule.isPresent() && spawnModule.get().isSpawned() && !dungeonStage.isCancelled() || !dungeonStage.isRebooted() || !dungeonStage.isFreeze();
                })
                .collect(Collectors.toList());
    }

    @Nullable
    public Dungeon getNearestDungeon() {
        return this.getDungeonMap().values().stream()
                .filter(f -> !f.getStage().isFreeze() && !f.getStage().isCancelled() && !f.getStage().isClosed())
                .min(Comparator.comparingInt(Dungeon::getNextStageTime)).orElse(null);
    }

    public CompletableFuture<Boolean> spawnDungeon(@NotNull Dungeon dungeon, @NotNull Location location) {
        ModuleManager moduleManager = dungeon.getModuleManager();
        SpawnModule spawnModule = moduleManager.getModule(SpawnModule.class).orElse(null);
        return CompletableFuture.supplyAsync(() -> {
            if (spawnModule == null) {
                plugin.error("It is impossible to spawn the '" + dungeon.getId() +
                        "' dungeon, as its `SpawnModule` has not been found. Try to find the errors above");
                return false;
            }

            dungeon.cancel(false);
            dungeon.setLocation(location);
            spawnModule.spawn(location);
            DungeonStage.call(dungeon, DungeonStage.OPENING, "Dungeon Manager via command");
            dungeonTickTask.tryActivateDungeonModules(dungeon);
            return true;
        }).exceptionally(throwable -> {
            DungeonPlugin.getLog().log(Level.SEVERE, "Error spawning dungeon '" + dungeon.getId() + "' via command", throwable);
            return false;
        });
    }

    public void interactDungeon(@NotNull Player player, @NotNull Dungeon dungeon, @NotNull Block block) {
        if (!block.hasMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey())) return;
        this.openDungeonChest(dungeon, block, player);
    }

    public void openDungeonChest(@NotNull Dungeon dungeon, @NotNull Block block, @NotNull Player player) {
        ModuleManager moduleManager = dungeon.getModuleManager();
        moduleManager.getModule(ChestModule.class)
                .flatMap(module -> module.getChestByBlock(block))
                .ifPresent(chest -> chest.click(player));
    }
}
