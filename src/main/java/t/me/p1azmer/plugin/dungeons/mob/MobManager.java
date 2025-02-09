package t.me.p1azmer.plugin.dungeons.mob;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.PDCUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.mob.config.MobConfig;
import t.me.p1azmer.plugin.dungeons.mob.config.MobsConfig;
import t.me.p1azmer.plugin.dungeons.mob.kill.MobKillReward;
import t.me.p1azmer.plugin.dungeons.scheduler.ThreadSync;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobManager extends AbstractManager<DungeonPlugin> {
    Map<String, MobConfig> mobConfigMap;
    MobList mobList;
    final ThreadSync threadSync;

    public MobManager(@NotNull DungeonPlugin plugin, @NotNull ThreadSync threadSync) {
        super(plugin);
        this.threadSync = threadSync;
    }

    @Override
    public void onLoad() {
        this.mobConfigMap = new HashMap<>();
        String DIR_MOBS = "/mobs/";
        this.plugin.getConfigManager().extractResources(DIR_MOBS);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + DIR_MOBS, false)) {
            MobConfig mob = new MobConfig(plugin, cfg);
            if (mob.load()) {
                this.mobConfigMap.put(mob.getId().toLowerCase(), mob);
            } else this.plugin.warn("Mob not loaded: '" + cfg.getFile().getName() + "'!");
        }
        plugin.info("Mobs Loaded: " + mobConfigMap.size());
        plugin.getConfig().initializeOptions(MobsConfig.class);
        this.mobList = new MobList();
        this.addListener(new MobListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.mobConfigMap != null) {
            this.mobConfigMap.values().forEach(MobConfig::clear);
            this.mobConfigMap.clear();
        }
        this.killMobs();
    }

    @NotNull
    public MobList getMobs() {
        this.mobList.getEnemies().removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }

    public void killMobs() {
        for (MobFaction faction : MobFaction.values()) {
            this.killMobs(faction);
        }
    }

    public void killMobs(@NotNull MobFaction faction) {
        this.getMobs().removeAll(faction);
    }

    public void killMob(@NotNull LivingEntity entity) {
        entity.remove();
        this.getMobs().removeInvalid();
    }

    public boolean createMobConfig(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getMobConfigById(id) != null) return false;

        JYML cfg = new JYML(this.plugin.getDataFolder() + "/mobs/", id + ".yml");
        MobConfig mobConfig = new MobConfig(plugin, cfg);

        mobConfig.setEntityType(EntityType.ZOMBIE);
        mobConfig.setName(StringUtil.capitalizeUnderscored(mobConfig.getEntityType().name().toLowerCase()));
        mobConfig.setNameVisible(true);

        Attribute healthAttribute;
        if (Version.isBehind(Version.MC_1_21_3)) {
            healthAttribute = Attribute.GENERIC_MAX_HEALTH;
        } else {
            healthAttribute = Attribute.valueOf("MAX_HEALTH");
        }

        mobConfig.getAttributes().put(healthAttribute, 20D);

        mobConfig.save();
        mobConfig.load();
        this.getMobConfigMap().put(mobConfig.getId(), mobConfig);
        return true;
    }

    @NotNull
    public List<String> getMobIds() {
        return new ArrayList<>(this.mobConfigMap.keySet());
    }

    @NotNull
    public Map<String, MobConfig> getMobConfigMap() {
        return this.mobConfigMap;
    }

    @NotNull
    public Collection<MobConfig> getMobConfigs() {
        return this.mobConfigMap.values();
    }

    @Nullable
    public MobConfig getMobConfigById(@NotNull String id) {
        return this.mobConfigMap.get(id.toLowerCase());
    }

    public void spawnCustomMob(@NotNull Dungeon dungeon, @NotNull String mobId, @NotNull MobList mobList, @NotNull Location location) {
        MobConfig customMob = this.getMobConfigById(mobId);
        if (customMob == null) {
            return;
        }

        EntityType type = customMob.getEntityType();
        LivingEntity entity = this.summonLivingEntity(type, location);
        if (entity == null) {
            return;
        }

        String riderId = customMob.getRiderId();
        if (riderId != null && !riderId.isEmpty()) {
            MobConfig rider = this.getMobConfigById(riderId);
            if (rider != null) {
                EntityType riderType = rider.getEntityType();
                LivingEntity riderEntity = this.summonLivingEntity(riderType, location);
                if (riderEntity != null) {
                    entity.addPassenger(riderEntity);

                    rider.applySettings(riderEntity);
                    rider.applyAttributes(riderEntity);
                    rider.applyPotionEffects(riderEntity);
                    this.setMobConfig(riderEntity, rider);
                    this.setMobDungeon(riderEntity, dungeon);
                    mobList.getEnemies().add(riderEntity);
                }
            }
        }

        customMob.applySettings(entity);
        customMob.applyAttributes(entity);
        customMob.applyPotionEffects(entity);
        this.setMobConfig(entity, customMob);
        this.setMobDungeon(entity, dungeon);
        mobList.getEnemies().add(entity);
    }

    private @Nullable LivingEntity summonLivingEntity(@NotNull EntityType type, @NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        Entity entity;
        if (!Bukkit.isPrimaryThread()) {
            entity = threadSync.syncApply(() -> location.getWorld().spawnEntity(location, type)).join();
        } else {
            entity = location.getWorld().spawnEntity(location, type);
        }

        if (!(entity instanceof LivingEntity bukkitEntity)) {
            return null;
        }
        return bukkitEntity;
    }

    private void setMobConfig(@NotNull LivingEntity entity, @NotNull MobConfig customMob) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_ID, customMob.getId());
    }

    private void setMobDungeon(@NotNull LivingEntity entity, @NotNull Dungeon dungeon) {
        PDCUtil.set(entity, Keys.DUNGEON_KEY_ID, dungeon.getId());
    }

    public static void setLevel(@NotNull LivingEntity entity, int level) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_LEVEL, level);
    }

    @Nullable
    public static String getMobDungeonId(@NotNull LivingEntity entity) {
        return PDCUtil.getString(entity, Keys.ENTITY_MOB_DUNGEON_ID).orElse(null);
    }

    @NotNull
    public static String getMobIdProvider(@NotNull LivingEntity entity) {
        return PDCUtil.getString(entity, Keys.ENTITY_MOB_ID).orElse("");
    }

    @NotNull
    public static String getMobId(@NotNull LivingEntity entity) {
        String[] split = getMobIdProvider(entity).split(":");
        return split.length == 2 ? split[1] : "";
    }

    @NotNull
    public static String getMobProvider(@NotNull LivingEntity entity) {
        String[] split = getMobIdProvider(entity).split(":");
        return split[0];
    }

    @Nullable
    public static MobKillReward getMobKillReward(@NotNull LivingEntity entity) {
        if (!MobsConfig.KILL_REWARD_ENABLED.get()) return null;

        var map = MobsConfig.KILL_REWARD_VALUES.get();
        return map.getOrDefault(getMobIdProvider(entity).toLowerCase(), map.get(Placeholders.DEFAULT));
    }

    public boolean isCustomMob(@NotNull Entity entity) {
        return this.getCustomEntity(entity);
    }

    public boolean getCustomEntity(@NotNull Entity entity) {
        String id = PDCUtil.getString(entity, Keys.ENTITY_MOB_ID).orElse(null);
        return id != null;
    }

    @Nullable
    public MobConfig getEntityMobConfig(@NotNull LivingEntity entity) {
        return this.getMobConfigById(getMobId(entity));
    }

    public static int getEntityLevel(@NotNull LivingEntity entity) {
        return PDCUtil.getInt(entity, Keys.ENTITY_MOB_LEVEL).orElse(0);
    }
}