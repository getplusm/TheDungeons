package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class MobModule extends AbstractModule {
    private MobList mobList;
    private MobManager manager;

    public MobModule(
            @NotNull Dungeon dungeon,
            @NotNull String id
    ) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.mobList = new MobList();
        this.manager = plugin().getMobManager();

        return aBoolean -> {
            DungeonStage stage = getDungeon().getStage();
            return stage.isOpened();
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
    protected CompletableFuture<Boolean> onActivate(boolean force) {
        Map<String, Integer> mobMap = getDungeon().getSettings().getMobMap();
        MobManager mobManager = plugin().getMobManager();
        if (!mobMap.isEmpty()) {
            mobMap.forEach((mobId, amount) -> {
                for (int i = 0; i < amount; i++) {
                    plugin().runTask(sync -> mobManager.spawnMob(getDungeon(), MobFaction.ENEMY, mobId, this.mobList));
                }
            });
            this.debug("Mob spawned");
        } else this.debug("No mobs to spawn");
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void update() {
        this.getMobs()
                .getAll()
                .forEach(entity -> {
                    Location location = this.getDungeon().getLocation().orElse(null);
                    if (location == null) {
                        this.manager.killMob(entity);
                        return;
                    }
                    Region dungeonRegion = getDungeon().getRegion();
                    Location entityLocation = entity.getLocation();

                    if (entityLocation.distance(location) > dungeonRegion.getRadius()) { // TODO: write safe damage with radius and target
                        Location groundBlock = LocationUtil.getFirstGroundBlock(location);
                        entity.teleport(groundBlock);
                    }
                });
    }

    @Override
    protected boolean onDeactivate(boolean force) {
        this.killMobs();
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

        this.mobList.getEnemies()
                .removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }
}
