package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;
import t.me.p1azmer.plugin.dungeons.scheduler.ThreadSync;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobModule extends AbstractModule {
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
        ThreadSync threadSync = getDungeon().getThreadSync();
        if (mobMap.isEmpty()) {
            debug("No mobs to spawn");
        }

        mobMap.forEach((mobId, amount) -> {
            for (int i = 0; i < amount; i++) {
                threadSync.sync(() -> {
                    mobManager.spawnMob(getDungeon(), MobFaction.ENEMY, mobId, this.mobList);
                }).exceptionally(throwable -> {
                    DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while spawning mob: " + mobId, throwable);
                    return null;
                });
            }
        });
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

            if (entityLocation.distance(location) > dungeonRegion.getRadius()) { // TODO: write safe damage with radius and target
                Location groundBlock = LocationUtil.getFirstGroundBlock(location);
                getDungeon().getThreadSync().sync(() -> entity.teleport(groundBlock)).exceptionally(throwable -> {
                    DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while teleporting mob", throwable);
                    return null;
                });
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
}
