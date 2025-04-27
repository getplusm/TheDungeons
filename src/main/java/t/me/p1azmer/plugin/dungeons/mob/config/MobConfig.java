package t.me.p1azmer.plugin.dungeons.mob.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.mob.Placeholders;
import t.me.p1azmer.plugin.dungeons.mob.editor.MobMainEditor;
import t.me.p1azmer.plugin.dungeons.mob.style.MobStyleType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobConfig extends AbstractConfigHolder<DungeonPlugin> implements Placeholder {
    private MobMainEditor editor;
    private String name;
    @Setter
    @Getter
    private boolean nameVisible;
    private EntityType entityType;
    private String riderId;
    @Setter
    @Getter
    private double spawnChance;
    @Setter
    @Getter
    private boolean enabledSpawn;
    @Setter
    @Getter
    private boolean silent;
    private final Map<MobStyleType, String> styles;

    private final Map<EquipmentSlot, ItemStack> equipment;
    private final Map<Attribute, Double> attributes; // [0] Base, [1] Per Level
    private final Map<PotionEffectType, int[]> potionEffects; // [0] Duration, [1] Value
    private final PlaceholderMap placeholderMap;

    public MobConfig(@NotNull DungeonPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.styles = new HashMap<>();
        this.attributes = new HashMap<>();
        this.equipment = new HashMap<>();
        this.potionEffects = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.MOB_ID, this::getId)
                .add(Placeholders.MOB_NAME, this::getName)
                .add(Placeholders.MOB_NAME_VISIBLE, () -> LangManager.getBoolean(this.isNameVisible()))
                .add(Placeholders.MOB_ENTITY_TYPE, () -> LangManager.getEntityType(this.getEntityType()))
                .add(Placeholders.MOB_RIDER_ID, this::getRiderId)
                .add(Placeholders.MOB_SPAWN_CHANCE, () -> String.valueOf(this.getSpawnChance()))
                .add(Placeholders.MOB_ENABLED_SPAWN, () -> LangManager.getBoolean(this.isEnabledSpawn()))
                .add(Placeholders.MOB_SILENT, () -> LangManager.getBoolean(this.isSilent()))
                .add(Placeholders.MOB_ATTRIBUTES_BASE, () -> this.getAttributes().entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n")))
                .add(Placeholders.MOB_POTION_EFFECT_DURATION, () -> this.getPotionEffects().entrySet().stream()
                        .map(entry -> entry.getKey().getName() + ": " + entry.getValue()[0]).collect(Collectors.joining("\n")))
                .add(Placeholders.MOB_POTION_EFFECT_VALUE, () -> this.getPotionEffects().entrySet().stream()
                        .map(entry -> entry.getKey().getName() + ": " + entry.getValue()[1]).collect(Collectors.joining("\n")))

        ;
    }

    @Override
    public boolean load() {
        try {
            this.setName(cfg.getString("Name", this.getId()));
            this.setNameVisible(cfg.getBoolean("Name_Visible"));
            EntityType type = cfg.getEnum("Entity_Type", EntityType.class);
            if (type == null) {
                throw new IllegalStateException("Invalid entity type for '" + getId() + "' mob!");
            }
            this.setEntityType(type);

            this.setRiderId(cfg.getString("Rider.Id", null));
            this.setEnabledSpawn(cfg.getBoolean("Spawn.Enabled", true));
            this.setSpawnChance(cfg.getDouble("Spawn.Chance", 10));
            this.setSilent(cfg.getBoolean("Silent"));

            for (String sId : cfg.getSection("Styles")) {
                MobStyleType styleType = StringUtil.getEnum(sId, MobStyleType.class).orElse(null);
                if (styleType == null) continue;

                String value = cfg.getString("Styles." + sId, "");
                this.addStyle(styleType, value);
            }

            // Potion Effects
            String path = "Potion_Effects.";
            for (PotionEffectType effectType : PotionEffectType.values()) {
                int duration = cfg.getInt(path + "Duration." + effectType.getName());
                int value = cfg.getInt(path + "Value." + effectType.getName());
                if (duration > 0 || value > 0) {
                    this.potionEffects.put(effectType, new int[]{duration, value});
                }
            }

            Stream.of(EquipmentSlot.values()).forEach(slot -> this.setEquipment(slot, cfg.getItemEncoded("Equipment." + slot.name())));

            // Attributes
            path = "Attributes.";
            if (Version.isAtLeast(Version.MC_1_21_2)) {
                for (Attribute attribute : Registry.ATTRIBUTE) {
                    double valueBase = cfg.getDouble(path + attribute.translationKey());
                    if (valueBase > 0) {
                        this.attributes.put(attribute, valueBase);
                    }
                }
            } else {
                for (Attribute attribute : Attribute.values()) {
                    double valueBase = cfg.getDouble(path + attribute.translationKey());
                    if (valueBase > 0) {
                        this.attributes.put(attribute, valueBase);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Name_Visible", this.isNameVisible());
        cfg.set("Entity_Type", this.getEntityType().name());

        cfg.set("Rider.Id", this.getRiderId());
        cfg.set("Spawn.Chance", this.getSpawnChance());
        cfg.set("Spawn.Enabled", this.isEnabledSpawn());
        cfg.set("Silent", this.isSilent());

        cfg.set("Styles", null);
        this.getStyles().forEach((styleType, value) -> cfg.set("Styles." + styleType.name(), value));

        cfg.set("Equipment", null);
        Stream.of(EquipmentSlot.values()).forEach(slot -> cfg.setItemEncoded("Equipment." + slot.name(), this.getEquipment(slot)));

        cfg.set("Potion_Effects", null);
        this.getPotionEffects().forEach((att, values) -> {
            String name = att.getName();
            cfg.set("Potion_Effects.Duration." + name, values[0]);
            cfg.set("Potion_Effects.Value." + name, values[1]);
        });

        cfg.set("Attributes", null);
        this.getAttributes().forEach((att, values) -> {
            String name = att.translationKey();
            cfg.set("Attributes." + name, values);
        });
    }

    @NotNull
    public MobMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new MobMainEditor(this);
        }
        return editor;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(@NotNull EntityType type) {
        this.entityType = type;
    }

    @NotNull
    public Map<MobStyleType, String> getStyles() {
        return styles;
    }

    @NotNull
    public String getStyle(@NotNull MobStyleType styleType) {
        return this.getStyles().getOrDefault(styleType, "");
    }

    public void addStyle(@NotNull MobStyleType styleType, @NotNull String value) {
        if (value.isEmpty() || !styleType.isApplicable(this.getEntityType())) return;

        this.getStyles().put(styleType, value);
    }

    public void removeStyle(@NotNull MobStyleType styleType) {
        this.getStyles().remove(styleType);
    }

    @NotNull
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    @NotNull
    public ItemStack getEquipment(@NotNull EquipmentSlot slot) {
        return this.equipment.getOrDefault(slot, new ItemStack(Material.AIR));
    }

    public void setEquipment(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        this.getEquipment().put(slot, item);
    }

    /**
     * @return Mob attribute values array, where [0] is base value, [1] is per level increase.
     */
    @NotNull
    public Map<Attribute, Double> getAttributes() {
        return this.attributes;
    }

    @NotNull
    public Map<PotionEffectType, int[]> getPotionEffects() {
        return potionEffects;
    }

    @Nullable
    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(@Nullable String riderId) {
        this.riderId = riderId;
    }

    public void applySettings(@NotNull LivingEntity entity) {
        entity.setCustomName(this.replacePlaceholders().apply(MobsConfig.NAME_FORMAT.get()));
        entity.setCustomNameVisible(this.isNameVisible());
        entity.setSilent(this.isSilent());

        EntityEquipment armor = entity.getEquipment();
        if (armor != null) {
            this.getEquipment().forEach(armor::setItem);
        }

        if (entity instanceof Ageable ageable) {
            ageable.setAdult();
        }
        if (entity instanceof PiglinAbstract piglin) {
            piglin.setImmuneToZombification(true);
        } else if (entity instanceof Hoglin hoglin) {
            hoglin.setImmuneToZombification(true);
        } else if (entity instanceof Zombie zombie) {
            if (zombie instanceof ZombieVillager zombieVillager) {
                zombieVillager.setConversionTime(-1);
            } else if (zombie instanceof PigZombie pigZombie) {
                pigZombie.setAngry(true);
            } else if (zombie instanceof Husk husk) {
                husk.setConversionTime(-1);
            } else {
                zombie.setConversionTime(-1);
            }
        }

        this.getStyles().forEach(((styleType, value) -> styleType.getWrapper().apply(entity, value)));
    }

    public void applyAttributes(@NotNull LivingEntity entity) {

        this.getAttributes().forEach((attribute, value) -> {
            AttributeInstance aInstance = entity.getAttribute(attribute);
            if (aInstance == null) return;

            aInstance.setBaseValue(value);

            Attribute healthAttribute;
            if (Version.isBehind(Version.MC_1_21_3)) {
                healthAttribute = Attribute.GENERIC_MAX_HEALTH;
            } else {
                healthAttribute = Attribute.valueOf("MAX_HEALTH");
            }
            if (attribute == healthAttribute) {
                entity.setHealth(value);
            }
        });
    }

    public void applyPotionEffects(@NotNull LivingEntity entity) {
        this.getPotionEffects().forEach((effectType, values) -> {
            // Fix for cases where default value is not present to use the vanilla one.
            if (values[0] <= 0) values[0] = 1;
            if (values[1] <= 0) values[1] = 1;

            int duration = values[0];
            int value = values[1];

            entity.addPotionEffect(effectType.createEffect(duration, value));
        });
    }
}