package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.SchematicModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.SchematicSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchematicsSettingsEditor extends EditorMenu<DungeonPlugin, SchematicSettings> {

    public SchematicsSettingsEditor(@NotNull SchematicSettings settings) {
        super(settings.dungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 9);
        Dungeon dungeon = settings.dungeon();

        this.addReturn(8).setClick((viewer, event) -> {
            dungeon.getEditor().openNextTick(viewer.getPlayer(), 1);
        });


        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.SCHEMATICS_IGNORE_AIR, 3).setClick((viewer, event) -> {
            settings.setIgnoreAirBlocks(!settings.isIgnoreAirBlocks());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isIgnoreAirBlocks() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.SCHEMATICS_IGNORE_AIR).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_UNDERGROUND, 2).setClick((viewer, event) -> {
            this.object.setUnderground(!settings.isUnderground());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isUnderground() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_UNDERGROUND).writeMeta();
        });
        this.addItem(Material.FLOWER_BANNER_PATTERN, EditorLocales.SCHEMATICS_LIST, 5).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    settings.setSchematics(new ArrayList<>());
                    this.save(viewer);
                    return;
                }
            }
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_ENTER_SCHEMATIC, wrapper -> {
                    String schematicName = wrapper.getText().replace(".schem", "");
                    AtomicBoolean result = new AtomicBoolean(true);
                    settings.dungeon().getModuleManager().getModule(SchematicModule.class).ifPresent(module -> {
                        File schematicFile = module.getFileByName(schematicName);
                        if (!this.plugin.getSchematicHandler().containsChestBlock(dungeon, schematicFile)) {
                            EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_VALID).getLocalized());
                            result.set(false);
                            return;
                        }
                        int chestBlocks = this.plugin.getSchematicHandler().getAmountOfChestBlocks(dungeon, schematicFile);
                        if (chestBlocks <= 0) {
                            EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_CONTAINS_CHEST).getLocalized());
                            result.set(false);
                        }
                    });
                    if (!result.get()) return false;
                    List<String> list = new ArrayList<>(settings.getSchematics());
                    list.add(schematicName);
                    settings.setSchematics(list);
                    dungeon.save();
                    return true;
                });
            }
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemReplacer.replace(item, settings.replacePlaceholders());
                    ItemReplacer.replace(item, settings.dungeon().replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
        this.openNextTick(viewer.getPlayer(), viewer.getPage());
    }
}