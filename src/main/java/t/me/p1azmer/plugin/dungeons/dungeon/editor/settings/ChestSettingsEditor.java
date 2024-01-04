package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.ChestSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule.OpenType.CLICK;
import static t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule.OpenType.TIMER;

public class ChestSettingsEditor extends EditorMenu<DungeonPlugin, ChestSettings> implements AutoPaged<DungeonChestState> {

    public ChestSettingsEditor(@NotNull ChestSettings settings) {
        super(settings.dungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 36);

        this.addReturn(31).setClick((viewer, event) -> {
            this.plugin.runTask(task -> settings.dungeon().getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(32);
        this.addPreviousPage(30);

        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_USE_ONE_KEY_TO_OPEN_CHEST, 11).setClick((viewer, event) -> {
            settings.setUseOneKeyForMenu(!settings.isUseOneKeyForMenu());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isUseOneKeyForMenu() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_USE_ONE_KEY_TO_OPEN_CHEST).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_RANDOM_SLOTS, 12).setClick((viewer, event) -> {
            settings.setRandomSlots(!settings.isRandomSlots());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isRandomSlots() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_RANDOM_SLOTS).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_BIG_CHEST, 13).setClick((viewer, event) -> {
            settings.setBigMenu(!settings.isBigMenu());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isBigMenu() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_BIG_CHEST).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_OPEN_TYPE, 14).setClick((viewer, event) -> {
            settings.setOpenType(settings.getOpenType().equals(CLICK) ? TIMER : CLICK);
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.getOpenType().isClick() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUyZGNhNjhjOGY4YWY1MzNmYjczN2ZhZWVhY2JlNzE3Yjk2ODc2N2ZjMTg4MjRkYzJkMzdhYzc4OWZjNzcifX19") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI4Mzk2NTkyM2MwZTNlYWQwZmU4NzYwZTA4Y2JhODk4MmUzM2E2YWNkYjg5MWVjYzZmNmRmZTZkNWQxODBiYyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_OPEN_TYPE).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_SEPARATE_GUI, 15).setClick((viewer, event) -> {
            settings.setSeparateMenu(!settings.isSeparateMenu());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isSeparateMenu() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_SEPARATE_GUI).writeMeta();
        });

        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3ZDY1YTM5NjZhODJkYzk2OTk1NGFjNjg2MGI1NWRhNzdiZGE1MDMyZThjYzFmMzhlY2UwNGFhOTQwYWFlZCJ9fX0="),
                EditorLocales.DUNGEON_SETTINGS_CHEST_LIMIT, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_POSITIVE_VALUE, wrapper -> {
                int value = wrapper.asInt(0);
                if (value <= 0) {
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT).getLocalized());
                    return false;
                }
                settings.setBlockLimit(value);
                this.save(viewer);
                return true;
            });
        });
        this.addItem(settings.getMaterial(), EditorLocales.DUNGEON_SETTINGS_CHEST_MATERIAL, 22).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                settings.setMaterial(cursor.getType());
                event.getView().setCursor(null);
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), Arrays.stream(Material.values()).filter(Material::isSolid).map(Enum::name).collect(Collectors.toList()), false);
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_CHEST_BLOCK_MATERIAL, wrapper -> {
                String materialRaw = wrapper.getTextRaw();
                Material material = StringUtil.getEnum(materialRaw, Material.class).orElse(null);
                if (material == null) {
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_MATERIAL_NOT_FOUND).getLocalized());
                    return false;
                }
                settings.setMaterial(material);
                this.save(viewer);
                return true;
            });
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, settings.replacePlaceholders())));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(2, 7).toArray();
    }

    @Override
    @NotNull
    public List<DungeonChestState> getObjects(@NotNull Player player) {
        return new ArrayList<>(Arrays.stream(DungeonChestState.values()).toList());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull DungeonChestState state) {
        ItemStack item = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNiOGYwNjg4NWQxZGFhZmQyNmNkOTViMzQ4MmNiNTI1ZDg4MWE2N2UwZDI0NzE2MWI5MDhkOTNkNTZkMTE0ZiJ9fX0=");
        ItemReplacer.create(item)
                .readLocale(EditorLocales.CHEST_BLOCK_STATE_OBJECT)
                .trimmed()
                .hideFlags()
                .replace(s -> s.replace(Placeholders.EDITOR_STATE_TIME, String.valueOf(this.object.getTime(state))))
                .replace(this.object.replacePlaceholders())
                .replace(state.replacePlaceholders())
                .replace(Colorizer::apply)
                .writeMeta();

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull DungeonChestState state) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.getClick().equals(ClickType.LEFT)) {
                EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_DUNGEON_WRITE_VALUE).getLocalized());
                EditorManager.startEdit(player, wrapper -> {
                    int time = wrapper.asInt(1);
                    this.object.setStateTime(state, time);
                    this.save(viewer);
                    return true;
                });
                plugin.runTask(task -> player.closeInventory());
            }

        };
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}