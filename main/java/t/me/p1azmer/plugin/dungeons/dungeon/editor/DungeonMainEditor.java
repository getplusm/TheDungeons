package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.EventListener;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.effect.DungeonEffectListEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.region.DungeonRegionMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.reward.DungeonRewardListEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.settings.DungeonSettingsEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.settings.HologramSettingsEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.settings.PartySettingsEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.SchematicModule;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DungeonMainEditor extends EditorMenu<DungeonPlugin, Dungeon> implements EventListener {

    private HologramSettingsEditor hologramSettingsEditor;
    private DungeonRewardListEditor editorRewards;
    private DungeonEffectListEditor effectEditor;
    private DungeonRegionMainEditor regionMainEditor;
    private DungeonSettingsEditor settingsEditor;
    private PartySettingsEditor partySettingsEditor;

    private final AutoRemovalCollection<Dungeon> rebootCache = AutoRemovalCollection.newHashSet(1, TimeUnit.MINUTES);

    public DungeonMainEditor(@NotNull Dungeon dungeon) {
        super(dungeon.plugin(), dungeon, Config.EDITOR_TITLE_DUNGEON.get(), 54);

        String REGION_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIyODM5ZDVjN2ZjMDY3ODA2MmYxYzZjOGYyN2IzMzIwOTQzODRlM2JiNWM0YjVlYmQxNjc2YjI3OWIwNmJmIn19fQ==";
        ItemStack regionHead = ItemUtil.createCustomHead(REGION_HEAD_TEXTURE);
        String SETTINGS_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMxOGVlNTI3OGUwOGQ5ZTZmZTkxNjNlYzQyNjdjNzkxZjUyNDhhMzU3ZjVmNzgwZDYzNDY4MTJjNzA0ZWI4ZiJ9fX0=";
        ItemStack settingsHead = ItemUtil.createCustomHead(SETTINGS_HEAD_TEXTURE);

        String KEY_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVlZmE0Y2QyYTU1OGU0YTgxMmUyZWE3NTQxZTYyNzUwYjk2YmExZDgyYzFkYTlmZDVmMmUzZmI5MzA4YzYzNSJ9fX0=";
        ItemStack keyHead = ItemUtil.createCustomHead(KEY_HEAD_TEXTURE);

        String BARRIER_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";
        ItemStack barrierHead = ItemUtil.createCustomHead(BARRIER_HEAD_TEXTURE);
        ItemStack worldHead = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjliMjg4OTAyMDU4MzU2NWY4OGQ1MDUzNzg3MGM1OWFhZDgwMjU5NGZhYmQ4MzdlMWQxNGY1YTA2YWUzNDUwOSJ9fX0=");

        this.addReturn(49).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().getDungeonEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.NAME_TAG, EditorLocales.DUNGEON_NAME, 4).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
                dungeon.setName(wrapper.getText());
                dungeon.save();
                return true;
            });
        });
        dungeon.getModuleManager().getModule(SchematicModule.class).ifPresent(module -> {
            this.addItem(Material.WOODEN_AXE, EditorLocales.DUNGEON_SCHEMATIC, 27).setClick((viewer, event) -> {
                if (event.isShiftClick()) {
                    if (event.isRightClick()) {
                        module.setSchematics(new ArrayList<>());
                        this.save(viewer);
                        return;
                    }
                }
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_ENTER_SCHEMATIC, wrapper -> {
                        String schematicName = wrapper.getText();
                        File schematicFile = module.getFileByName(schematicName);
                        if (!this.plugin.getSchematicHandler().containsChestBlock(dungeon, schematicFile)) {
                            EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_VALID).getLocalized());
                            return false;
                        }
                        int chestBlocks = this.plugin.getSchematicHandler().getAmountOfChestBlocks(dungeon, schematicFile);
                        if (chestBlocks <= 0) {
                            EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_CONTAINS_CHEST).getLocalized());
                            return false;
                        }
                        List<String> list = module.getSchematics();
                        list.add(schematicName);
                        module.setSchematics(list);
                        dungeon.save();
                        return true;
                    });
                }
            });
        });
        this.addItem(keyHead, EditorLocales.DUNGEON_KEYS, 28).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_KEY_ID, wrapper -> {
                    dungeon.getKeyIds().add(wrapper.getTextRaw());
                    dungeon.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKeyManager().getKeyIds(), false);
            } else if (event.isRightClick()) {
                dungeon.getKeyIds().clear();
                this.save(viewer);
            }
        });

        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQzMWFlN2RjZDFlMmRkMzZjMzNhMGM5YTExNmI1NmUxNGFjZGFmMGRhZmIyYTA0OTg2ZDY1YWVhMGUzNTMxNCJ9fX0="),
                EditorLocales.HOLOGRAM_SETTINGS, 9).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getHologramEditor().open(viewer.getPlayer(), 1));
        });

        // other editors
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJjZGUwNjhlOTlhNGY5OGMzMWY4N2I0Y2MwNmJlMTRiMjI5YWNhNGY3MjgxYTQxNmM3ZTJmNTUzMjIzZGI3NCJ9fX0="),
                EditorLocales.DUNGEON_REWARDS, 11).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getEditorRewards().open(viewer.getPlayer(), 1));
        });
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhjZWI4NjMxYWRkN2NiYjU2NWRjYjcwNWYxMjEyMzQ5Y2NjZDc1NTk2NWM0NmE5MjI4NTJjOWZkOTQ4YjRiYiJ9fX0="),
                EditorLocales.DUNGEON_EFFECTS, 13).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getEffectEditor().open(viewer.getPlayer(), 1));
        });
        this.addItem(regionHead, EditorLocales.DUNGEON_REGION, 15).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getRegionMainEditor().open(viewer.getPlayer(), 1));
        });
        this.addItem(settingsHead, EditorLocales.DUNGEON_SETTINGS, 17).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getSettingsEditor().open(viewer.getPlayer(), 1));
        });
        this.addItem(barrierHead, EditorLocales.DUNGEON_PARTICLE, 26).setClick((viewer, event) -> {
            viewer.getPlayer().sendMessage(Colorizer.apply("&cThis page will be available later\nFollow the news in our discord: &lhttps://discord.gg/ajnPb3fdKq"));
        });
        if (plugin().getPartyHandler() != null) {
            this.addItem(Material.GOLDEN_HORSE_ARMOR, EditorLocales.DUNGEON_PARTY, 25).setClick((viewer, event) -> {
                this.getPartySettingsEditor().openNextTick(viewer.getPlayer(), 1);
            });
        }
        this.addItem(worldHead, EditorLocales.DUNGEON_WORLD, 31).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.worldNames(), true);
            this.handleInput(viewer, Lang.Editor_Dungeon_Enter_World, wrapper -> {
                String worldName = wrapper.getText();
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Dungeon_World_Not_Found).getLocalized());
                    return false;
                }
                dungeon.setWorld(world);
                dungeon.save();
                return true;
            });
        });
        this.addItem(Material.SOUL_TORCH, EditorLocales.DUNGEON_REBOOT, 45).setClick((viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                if (this.rebootCache.add(dungeon)) {
                    this.getObject().reboot();
                    viewer.getPlayer().sendMessage(Colorizer.apply("&aReboot the '" + dungeon.getId() + "' dungeon!"));
                } else {
                    viewer.getPlayer().sendMessage(Colorizer.apply("&cYou can't restart the dungeon so often! Please wait one minute for reboot!"));
                }
            }
        });


        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    dungeon.getModuleManager().getModules().forEach(module -> ItemReplacer.replace(item, module.replacePlaceholders()));
                    ItemReplacer.replace(item, dungeon.replacePlaceholders());
                    ItemReplacer.replace(item, dungeon.getHologramSettings().replacePlaceholders());
                    ItemReplacer.replace(item, dungeon.getDungeonRegion().replacePlaceholders());
                    ItemReplacer.replace(item, dungeon.getSettings().replacePlaceholders());
                }));
            }
        });

        this.registerListeners();
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void clear() {
        super.clear();
        this.unregisterListeners();
        if (this.editorRewards != null) {
            this.editorRewards.clear();
            this.editorRewards = null;
        }
    }

    @NotNull
    public DungeonRewardListEditor getEditorRewards() {
        if (this.editorRewards == null) {
            this.editorRewards = new DungeonRewardListEditor(this.object);
        }
        return this.editorRewards;
    }

    @NotNull
    public HologramSettingsEditor getHologramEditor() {
        if (this.hologramSettingsEditor == null) {
            this.hologramSettingsEditor = new HologramSettingsEditor(this.object.getHologramSettings());
        }
        return this.hologramSettingsEditor;
    }


    @NotNull
    public DungeonEffectListEditor getEffectEditor() {
        if (this.effectEditor == null) {
            this.effectEditor = new DungeonEffectListEditor(this.object);
        }
        return this.effectEditor;
    }

    @NotNull
    public DungeonRegionMainEditor getRegionMainEditor() {
        if (this.regionMainEditor == null) {
            this.regionMainEditor = new DungeonRegionMainEditor(this.object.getDungeonRegion());
        }
        return regionMainEditor;
    }

    @NotNull
    public DungeonSettingsEditor getSettingsEditor() {
        if (this.settingsEditor == null) {
            this.settingsEditor = new DungeonSettingsEditor(this.object.getSettings());
        }
        return settingsEditor;
    }

    @NotNull
    public PartySettingsEditor getPartySettingsEditor() {
        if (this.partySettingsEditor == null) {
            this.partySettingsEditor = new PartySettingsEditor(this.object.getPartySettings());
        }
        return partySettingsEditor;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
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