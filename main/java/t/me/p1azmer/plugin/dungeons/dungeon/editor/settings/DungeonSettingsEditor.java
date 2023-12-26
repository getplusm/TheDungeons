package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.MainSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DungeonSettingsEditor extends EditorMenu<DungeonPlugin, MainSettings> {

    public DungeonSettingsEditor(@NotNull MainSettings region) {
        super(region.getDungeon().plugin(), region, Config.EDITOR_TITLE_DUNGEON.get(), 45);
        Dungeon dungeon = region.getDungeon();
        ItemStack clockHead1 = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNiOGYwNjg4NWQxZGFhZmQyNmNkOTViMzQ4MmNiNTI1ZDg4MWE2N2UwZDI0NzE2MWI5MDhkOTNkNTZkMTE0ZiJ9fX0=");
        ItemStack clockHead2 = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg2YjlkNThiY2QxYTU1NWY5M2U3ZDg2NTkxNTljZmQyNWI4ZGQ2ZTliY2UxZTk3MzgyMjgyNDI5MTg2MiJ9fX0=");
        ItemStack mobHead = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5NDlhMTZjYmFlNTRmNTRhMGFmMTA1ZjRjZGExNzEyZWI1YzM5YTc3Y2NhOWE5ZWQ1NTI4ZTAzYjczYWMwIn19fQ==");
        String MINIMAL_ONLINE_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZjYmFlNzI0NmNjMmM2ZTg4ODU4NzE5OGM3OTU5OTc5NjY2YjRmNWE0MDg4ZjI0ZTI2ZTA3NWYxNDBhZTZjMyJ9fX0=";
        ItemStack minimalOnlineHead = ItemUtil.createCustomHead(MINIMAL_ONLINE_HEAD_TEXTURE);

        String BLOCKS_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UyZWI0NzUxZTNjNTBkNTBmZjE2MzUyNTc2NjYzZDhmZWRmZTNlMDRiMmYwYjhhMmFhODAzYjQxOTM2M2NhMSJ9fX0=";
        ItemStack blocksHead = ItemUtil.createCustomHead(BLOCKS_HEAD_TEXTURE);
        String CHEST_BLOCK_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdmZDgxMThmMDc4MjhjZjdkOTM5MWM1YTAyMTRhYTI1YWYyNGM5OGU0NTAzZTBiMmZjYzhlZmRkMzE4OWJiMCJ9fX0=";
        ItemStack chestBlockHead = ItemUtil.createCustomHead(CHEST_BLOCK_HEAD_TEXTURE);

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.OAK_BUTTON, EditorLocales.DUNGEON_SETTINGS_ENABLE, 0).setClick((viewer, event) -> {
            this.object.setEnabled(!this.getObject().isEnabled());
            this.save(viewer);
        });
        this.addItem(chestBlockHead, EditorLocales.DUNGEON_SETTINGS_CHEST_LIMIT, 2).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(0);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setChestBlockLimit(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(blocksHead, EditorLocales.DUNGEON_SETTINGS_CHEST_BLOCK_SEARCH_RADIUS, 3).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setChestBlockSearchRadius(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(minimalOnlineHead, EditorLocales.DUNGEON_SETTINGS_MINIMAL_ONLINE, 4).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_VALUE, wrapper -> {
                this.object.setMinimalOnline(wrapper.asAnyInt(0));
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead1, EditorLocales.DUNGEON_SETTINGS_CHEST_CLOSE_TIME, 5).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setChestCloseTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead1, EditorLocales.DUNGEON_SETTINGS_CHEST_OPEN_TIME, 6).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setChestOpenTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead1, EditorLocales.DUNGEON_SETTINGS_CHEST_WAIT_TIME, 7).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setChestWaitTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead1, EditorLocales.DUNGEON_SETTINGS_TIMER_REFRESH, 8).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setRefreshTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(Material.HOPPER, EditorLocales.DUNGEON_SETTINGS_RANDOM_SLOTS, 10).setClick((viewer, event) -> {
            this.object.setRandomSlots(!this.getObject().isRandomSlots());
            this.save(viewer);
        });
        this.addItem(Material.CHEST, EditorLocales.DUNGEON_SETTINGS_BIG_CHEST, 11).setClick((viewer, event) -> {
            this.object.setBigChest(!this.getObject().isBigChest());
            this.save(viewer);
        });
        this.addItem(Material.DIRT, EditorLocales.DUNGEON_SETTINGS_UNDERGROUND, 12).setClick((viewer, event) -> {
            this.object.setUnderground(!this.getObject().isUnderground());
            this.save(viewer);
            return;
        });
        this.addItem(Material.BIRCH_BUTTON, EditorLocales.DUNGEON_SETTINGS_CLICK_TIMER, 13).setClick((viewer, event) -> {
            this.object.setClickTimer(!this.getObject().isClickTimer());
            this.save(viewer);
        });
        this.addItem(Material.CHEST, EditorLocales.DUNGEON_SETTINGS_OPEN_TYPE, 14).setClick((viewer, event) -> {
            this.object.setChestOpenType(this.getObject().getChestOpenType().equals(Dungeon.OpenType.CLICK) ? Dungeon.OpenType.TIMER : Dungeon.OpenType.CLICK);
            this.save(viewer);
        });
        this.addItem(Material.COMPASS, EditorLocales.DUNGEON_SETTINGS_SEPARATE_GUI, 15).setClick((viewer, event) -> {
            this.object.setSeparateChestBlockGui(!this.getObject().isSeparateChestBlockGui());
            this.save(viewer);
        });
        this.addItem(Material.BARREL, EditorLocales.DUNGEON_SETTINGS_CHEST_MATERIAL, 16).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                this.object.setChestMaterial(cursor.getType());
                event.getView().setCursor(null);
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), Arrays.stream(Material.values()).filter(Material::isSolid).map(Enum::name).collect(Collectors.toList()), false);
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_CHEST_BLOCK_MATERIAL, wrapper -> {
                String materialRaw = wrapper.getTextRaw();
                Material material = StringUtil.getEnum(materialRaw, Material.class).orElse(null);
                if (material == null){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_MATERIAL_NOT_FOUND).getLocalized());
                    return false;
                }
                this.object.setChestMaterial(material);
                dungeon.save();
                return true;
            });
        });
        this.addItem(Material.BARRIER, EditorLocales.DUNGEON_SETTINGS_LET_PLAYER_WHEN_CLOSE, 17).setClick((viewer, event) -> {
            this.object.setLetPlayersWhenClose(!this.getObject().isLetPlayersWhenClose());
            this.save(viewer);
        });
        this.addItem(Material.TRIPWIRE_HOOK, EditorLocales.DUNGEON_SETTINGS_USE_ONE_KEY_TO_OPEN_CHEST, 18).setClick((viewer, event) -> {
            this.object.setUseOneKeyForChest(!this.getObject().isUseOneKeyForChest());
            this.save(viewer);
        });
        this.addItem(clockHead2, EditorLocales.DUNGEON_SETTINGS_REGION_CLOSE_TIME, 19).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setRegionCloseTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead2, EditorLocales.DUNGEON_SETTINGS_REGION_WAIT_TIME, 20).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setRegionWaitTime(value);
                dungeon.save();
                return true;
            });
        });
        this.addItem(clockHead2, EditorLocales.DUNGEON_SETTINGS_REGION_OPEN_TIME, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(1);
                if (value <= 0){
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                this.object.setRegionOpenTime(value);
                dungeon.save();
                return true;
            });
        });

        this.addItem(Material.ACACIA_FENCE_GATE, EditorLocales.DUNGEON_SETTINGS_COMMAND_OPEN, 23).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.getOpenCommands().clear();
                    this.save(viewer);
                }
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                this.object.getOpenCommands().add(wrapper.getTextRaw());
                dungeon.save();
                return true;
            });
        });
        this.addItem(Material.ACACIA_FENCE, EditorLocales.DUNGEON_SETTINGS_COMMAND_CLOSE, 24).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.getCloseCommands().clear();
                    this.save(viewer);
                }
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                this.object.getCloseCommands().add(wrapper.getTextRaw());
                dungeon.save();
                return true;
            });
        });
        this.addItem(mobHead, EditorLocales.DUNGEON_SETTINGS_MOBS, 26).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.getMobMap().clear();
                    this.save(viewer);
                }
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), plugin().getMobManager().getMobIds(), false);
            this.handleInput(viewer, Lang.Editor_Mob_Enter_Id, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length != 2) return false;

                String mobId = split[0];

                int value = StringUtil.getInteger(split[1], 1);
                this.object.getMobMap().put(mobId, value);
                dungeon.save();
                return true;
            });
        });
        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemUtil.replace(item, dungeon.replacePlaceholders());
                    ItemUtil.replace(item, dungeon.getDungeonRegion().replacePlaceholders());
                    ItemUtil.replace(item, dungeon.getSettings().replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getDungeon().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}