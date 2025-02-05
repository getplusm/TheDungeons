package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobModule extends AbstractModule {
    static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    MobList mobList;
    MobManager manager;

    public MobModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.mobList = new MobList();
        this.manager = plugin().getMobManager();

        return aBoolean -> {
            DungeonStage stage = getDungeon().getStage();
            Optional<ChestModule> module = this.getManager().getModule(ChestModule.class);
            ChestModule chestModule = module.orElse(null);

            return stage.isOpened() && chestModule != null && !chestModule.getChests().isEmpty();
        };
    }

    @Override
    protected void onShutdown() {
        if (this.mobList != null) {
            this.killMobs();
            this.mobList = null;
        }
    }

    @Override
    public boolean onActivate(boolean force) {
        Map<String, Integer> mobMap = getDungeon().getMobsSettings().getMobMap();
        MobManager mobManager = plugin().getMobManager();
        if (mobMap.isEmpty()) {
            debug("No mobs to spawn");
        }

        Location location = getDungeon().getLocation().orElseThrow(() -> new IllegalStateException("Dungeon location is not set"));
        for (val entry : mobMap.entrySet()) {
            String mobId = entry.getKey();
            Integer amount = entry.getValue();
            try {
                for (int i = 0; i < amount; i++) {
                    Location spawnLocation = findSafeLocation(location, getDungeon().getRegion().getRadius());
                    if (spawnLocation == null) continue;

                    mobManager.summonLivingEntity(getDungeon(), mobId, this.mobList, spawnLocation);
                }
            } catch (RuntimeException exception) {
                DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while spawning '" + mobId + "' mob", exception);
            }
        }
        return true;
    }

    @Override
    public void update() {
        super.update();

        this.getMobs().getAll().forEach(entity -> {
            Location location = this.getDungeon().getLocation().orElse(null);
            if (location == null) {
                this.manager.killMob(entity);
                return;
            }

            Region dungeonRegion = getDungeon().getRegion();
            Location entityLocation = entity.getLocation();

            if (entityLocation.distance(location) > dungeonRegion.getRadius()) {
                Location safeLocation = findSafeLocation(location, dungeonRegion.getRadius());
                if (safeLocation != null) {
                    getDungeon().getThreadSync().sync(() -> entity.teleport(safeLocation)).exceptionally(throwable -> {
                        DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while teleporting mob", throwable);
                        return null;
                    });
                } else {
                    DungeonPlugin.getLog().log(Level.WARNING, "Could not find a safe location to teleport mob");
                }
            }
        });
    }

    @Override
    public boolean onDeactivate(boolean force) {
        getDungeon().getThreadSync().sync(this::killMobs).exceptionally(throwable -> {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while killing mobs", throwable);
            return null;
        });
        return true;
    }

    public void killMobs() {
        for (MobFaction faction : MobFaction.values()) {
            this.killMobs(faction);
        }
    }

    public void killMobs(@NotNull MobFaction faction) {
        this.getMobs().removeAll(faction);
    }

    @NotNull
    public MobList getMobs() {
        if (this.mobList == null)
            this.mobList = new MobList();

        this.mobList.getEnemies().removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }

    private static @Nullable Location findSafeLocation(@NotNull Location center, int radius) {
        int attempts = 10;

        for (int i = 0; i < attempts; i++) {
            double angle = RANDOM.nextDouble() * 2 * Math.PI;
            double distance = RANDOM.nextDouble() * radius;
            double x = center.getX() + Math.cos(angle) * distance;
            double z = center.getZ() + Math.sin(angle) * distance;

            double y = center.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;

            Location potentialLocation = new Location(center.getWorld(), x, y, z);

            if (isLocationSafe(potentialLocation)) {
                return potentialLocation;
            }
        }

        return null;
    }

    private static boolean isLocationSafe(@NotNull Location location) {
        Material blockBelow = location.getBlock().getRelative(BlockFace.DOWN).getType();
        return blockBelow.isSolid() && !blockBelow.toString().contains("WATER") && !blockBelow.toString().contains("LAVA");
    }
}
