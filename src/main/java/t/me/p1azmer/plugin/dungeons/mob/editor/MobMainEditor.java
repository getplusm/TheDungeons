package t.me.p1azmer.plugin.dungeons.mob.editor;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.Menu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.mob.config.MobConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MobMainEditor extends EditorMenu<DungeonPlugin, MobConfig> {

    private MobStylesEditor mobStylesEditor;

    public MobMainEditor(@NotNull MobConfig mob) {
        super(mob.plugin(), mob, Config.EDITOR_TITLE_MOB.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> plugin.getEditor().getMobEditor().openNextTick(viewer, 1));

        this.addItem(Material.NAME_TAG, EditorLocales.MOB_NAME, 10).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                mob.setNameVisible(!mob.isNameVisible());
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                mob.setName(wrapper.getText());
                mob.save();
                return true;
            });
        });

        this.addItem(Material.CREEPER_SPAWN_EGG, EditorLocales.MOB_ENTITY_TYPE, 11).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), Stream.of(EntityType.values())
                    .filter(EntityType::isSpawnable).filter(EntityType::isAlive).map(Enum::name).toList(), true);

            this.handleInput(viewer, Lang.Editor_Mob_Enter_Type, wrapper -> {
                EntityType entityType = StringUtil.getEnum(wrapper.getTextRaw(), EntityType.class).orElse(null);
                if (entityType == null || !entityType.isSpawnable() || !entityType.isAlive()) {
                    return false;
                }
                mob.setEntityType(entityType);
                mob.save();
                return true;
            });
        });

        this.addItem(Material.SADDLE, EditorLocales.MOB_RIDER, 12).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                mob.setRiderId("");
                mob.save();
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), this.plugin().getMobManager().getMobIds(), true);

            this.handleInput(viewer, Lang.Editor_Mob_Enter_Create, wrapper -> {
                String id = wrapper.getTextRaw();
                MobConfig config = this.plugin().getMobManager().getMobConfigById(id);
                if (config == null) {
                    return false;
                }
                mob.setRiderId(id);
                mob.save();
                return true;
            });
        });

        this.addItem(Material.SPLASH_POTION, EditorLocales.MOB_POTIONS, 13).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    mob.getPotionEffects().clear();
                    this.save(viewer);
                }
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), Stream.of(PotionEffectType.values()).map(PotionEffectType::getName).toList(), false);
            this.handleInput(viewer, Lang.Editor_Mob_Enter_Potion, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length != 2) return false;

                PotionEffectType attribute = PotionEffectType.getByName(split[0]);
                if (attribute == null) return false;

                int value = NumberUtil.getInteger(split[1], 0);
                int[] valuesHas = mob.getPotionEffects().computeIfAbsent(attribute, k -> new int[2]);
                int index = event.isLeftClick() ? 0 : 1;
                valuesHas[index] = value;
                mob.getPotionEffects().put(attribute, valuesHas);
                mob.save();
                return true;
            });
        });

        this.addItem(Material.APPLE, EditorLocales.MOB_ATTRIBUTES, 14).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    mob.getAttributes().clear();
                    this.save(viewer);
                }
                return;
            }
            List<String> attributes = new ArrayList<>();
            if (Version.isBehind(Version.V1_20_R1)) {
                attributes = Stream.of(Attribute.values()).map(Attribute::translationKey).toList();
            } else if (Version.isAbove(Version.V1_20_R1)) {
                attributes = Registry.ATTRIBUTE.stream().map(Attribute::translationKey).toList();
            } else if (Version.isAbove(Version.MC_1_21)) {
                attributes = Registry.ATTRIBUTE.stream().map(Attribute::translationKey).toList();
            }

            EditorManager.suggestValues(viewer.getPlayer(), attributes, false);
            this.handleInput(viewer, Lang.Editor_Mob_Enter_Attribute, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length != 2) return false;

                Attribute attribute;
                if (Version.isAbove(Version.MC_1_21)) {
                    attribute = Registry.ATTRIBUTE.stream().filter(f -> f.translationKey().equals(split[0])).findFirst().orElse(null);
                } else {
                    attribute = Attribute.valueOf(split[0]);
                }

                if (attribute == null) return false;

                double value = NumberUtil.getDouble(split[1], 0D);
                mob.getAttributes().put(attribute, value);
                mob.save();
                return true;
            });
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.MOB_EQUIPMENT, 15).setClick((viewer, event) -> new EquipmentEditor(mob).openNextTick(viewer, 1));

        this.addItem(Material.ORANGE_DYE, EditorLocales.MOB_STYLES, 16).setClick((viewer, event) -> this.getEditorMobStyles().openNextTick(viewer, 1));

        this.addItem(Material.DISC_FRAGMENT_5, EditorLocales.MOB_SILENT, 28).setClick((viewer, event) -> {
            mob.setSilent(!mob.isSilent());
            mob.save();
            this.save(viewer);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions()
                .addDisplayModifier((viewer, item) -> ItemReplacer.replace(item, mob.replacePlaceholders())));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @NotNull
    public MobStylesEditor getEditorMobStyles() {
        if (this.mobStylesEditor == null) {
            this.mobStylesEditor = new MobStylesEditor(plugin, this.object);
        }
        return mobStylesEditor;
    }

    private static class EquipmentEditor extends Menu<DungeonPlugin> {

        private final MobConfig mob;
        private final Map<EquipmentSlot, Integer> equipmentSlots;

        public EquipmentEditor(@NotNull MobConfig mob) {
            super(mob.plugin(), Config.EDITOR_TITLE_MOB.get(), 9);
            this.mob = mob;
            this.equipmentSlots = new HashMap<>();
            this.equipmentSlots.put(EquipmentSlot.FEET, 0);
            this.equipmentSlots.put(EquipmentSlot.LEGS, 1);
            this.equipmentSlots.put(EquipmentSlot.CHEST, 2);
            this.equipmentSlots.put(EquipmentSlot.HEAD, 3);
            this.equipmentSlots.put(EquipmentSlot.HAND, 4);
            this.equipmentSlots.put(EquipmentSlot.OFF_HAND, 5);
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Nullable
        private EquipmentSlot getTypeBySlot(int slot) {
            return this.equipmentSlots.entrySet().stream().filter(entry -> entry.getValue() == slot).findFirst()
                    .map(Map.Entry::getKey).orElse(null);
        }

        private void saveEquipment(@NotNull Player player, @NotNull Inventory inventory) {
            this.equipmentSlots.forEach((equipmentSlot, slot) -> this.mob.setEquipment(equipmentSlot, inventory.getItem(slot)));
            this.mob.save();
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                inventory.setItem(this.equipmentSlots.getOrDefault(equipmentSlot, 0), this.mob.getEquipment(equipmentSlot));
            }
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(slotType != SlotType.PLAYER && slotType != SlotType.PLAYER_EMPTY && this.getTypeBySlot(event.getRawSlot()) == null);
        }

        @Override
        public void onDrag(@NotNull MenuViewer viewer, @NotNull InventoryDragEvent event) {
            super.onDrag(viewer, event);
            event.setCancelled(event.getRawSlots().stream().anyMatch(slot -> this.getTypeBySlot(slot) == null));
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            this.saveEquipment(viewer.getPlayer(), event.getInventory());
            this.mob.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }
    }
}