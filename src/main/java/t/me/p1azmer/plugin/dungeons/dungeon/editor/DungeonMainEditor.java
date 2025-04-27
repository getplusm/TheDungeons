package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.effect.DungeonEffectListEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.region.DungeonRegionMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.reward.DungeonRewardListEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.settings.*;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DungeonMainEditor extends EditorMenu<DungeonPlugin, Dungeon> implements EventListener {

    StageSettingsEditor stageSettingsEditor;
    CommandsSettingsEditor commandsSettingsEditor;
    SchematicsSettingsEditor schematicsSettingsEditor;
    ModuleSettingsEditor moduleSettingsEditor;
    AnnounceSettingsEditor announceSettingsEditor;
    ChestSettingsEditor chestStateSettingsEditor;
    HologramSettingsEditor hologramSettingsEditor;
    DungeonRewardListEditor editorRewards;
    DungeonEffectListEditor effectEditor;
    DungeonRegionMainEditor regionMainEditor;
    MainSettingsEditor settingsEditor;
    PartySettingsEditor partySettingsEditor;
    GenerationSettingsEditor generationSettingsEditor;
    AccessSettingsEditor accessSettingsEditor;

    final AutoRemovalCollection<Dungeon> rebootCache = AutoRemovalCollection.newHashSet(1, TimeUnit.MINUTES);

    public DungeonMainEditor(@NotNull Dungeon dungeon) {
        super(dungeon.plugin(), dungeon, Config.EDITOR_TITLE_DUNGEON.get(), 54);

        this.addReturn(53).setClick((viewer, event) -> this.plugin.runTask(task -> this.plugin.getEditor().getDungeonEditor().open(viewer.getPlayer(), 1)));

        this.addItem(Material.NAME_TAG, EditorLocales.DUNGEON_NAME, 3).setClick((viewer, event) -> this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
            dungeon.setName(wrapper.getText());
            dungeon.save();
            return true;
        }));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNWU4ZWJlZmYwYTIwMDhmMTRiZGEwNzJmNzU2MTVkYWNhYzBjNGRhYmM5MGQ5ZDUyY2MzMjE0ZTVjNTM1NyJ9fX0="),
                EditorLocales.DUNGEON_KEYS, 5).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_KEY_ID, wrapper -> {
                    dungeon.getKeys().getKeyIds().add(wrapper.getTextRaw());
                    dungeon.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKeyManager().getKeyIds(), false);
            } else if (event.isRightClick()) {
                dungeon.getKeys().getKeyIds().clear();
                this.save(viewer);
            }
        });

        // other editors
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQzMWFlN2RjZDFlMmRkMzZjMzNhMGM5YTExNmI1NmUxNGFjZGFmMGRhZmIyYTA0OTg2ZDY1YWVhMGUzNTMxNCJ9fX0="),
                EditorLocales.HOLOGRAM_SETTINGS, 19).setClick((viewer, event) -> this.plugin.runTask(task -> this.getHologramEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhiZGM3YTFkNmNmZjc2YTkyNTU2NTJkMzE2NTUzMjI4NWFjYzNhOWQxYzBmMTJmMzljYTAwNzc2OWE3ZWExNCJ9fX0="),
                EditorLocales.CHEST_BLOCK_SETTINGS, 20).setClick((viewer, event) -> this.plugin.runTask(task -> this.getChestStateSettingsEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFlN2JmNDUyMmIwM2RmY2M4NjY1MTMzNjNlYWE5MDQ2ZmRkZmQ0YWE2ZjFmMDg4OWYwM2MxZTYyMTZlMGVhMCJ9fX0="),
                EditorLocales.ANNOUNCE_SETTINGS, 21).setClick((viewer, event) -> this.plugin.runTask(task -> this.getAnnounceSettingsEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZiNzc5ZTU0Nzc5YWZiMmM2ZmQwNjE5YWI0ZTgwNTZmNmQ5MTQwM2U4ZjQyYzJlYzQ1YzdmNjIxMTcwMmVkZiJ9fX0="),
                EditorLocales.MODULES_SETTINGS, 22).setClick((viewer, event) -> this.plugin.runTask(task -> this.getModuleSettingsEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJjZGUwNjhlOTlhNGY5OGMzMWY4N2I0Y2MwNmJlMTRiMjI5YWNhNGY3MjgxYTQxNmM3ZTJmNTUzMjIzZGI3NCJ9fX0="),
                EditorLocales.DUNGEON_REWARDS, 23).setClick((viewer, event) -> this.plugin.runTask(task -> this.getEditorRewards().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhjZWI4NjMxYWRkN2NiYjU2NWRjYjcwNWYxMjEyMzQ5Y2NjZDc1NTk2NWM0NmE5MjI4NTJjOWZkOTQ4YjRiYiJ9fX0="),
                EditorLocales.DUNGEON_EFFECTS, 24).setClick((viewer, event) -> this.plugin.runTask(task -> this.getEffectEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhlMTZiZjlkNTYxNTlkZjI1ODlmZjc2NTZmODdjYWYwZjc2MjQwZDE0ZGZhNTU2ZjJiN2FjZGUzNzYzMWY4ZCJ9fX0="),
                EditorLocales.SCHEMATICS_SETTINGS, 25).setClick((viewer, event) -> this.plugin.runTask(task -> this.getSchematicsSettingsEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIyODM5ZDVjN2ZjMDY3ODA2MmYxYzZjOGYyN2IzMzIwOTQzODRlM2JiNWM0YjVlYmQxNjc2YjI3OWIwNmJmIn19fQ=="),
                EditorLocales.DUNGEON_REGION, 29).setClick((viewer, event) -> this.plugin.runTask(task -> this.getRegionMainEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg2YjlkNThiY2QxYTU1NWY5M2U3ZDg2NTkxNTljZmQyNWI4ZGQ2ZTliY2UxZTk3MzgyMjgyNDI5MTg2MiJ9fX0="),
                EditorLocales.STAGES_SETTINGS, 30).setClick((viewer, event) -> this.plugin.runTask(task -> this.getStageSettingsEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMxOGVlNTI3OGUwOGQ5ZTZmZTkxNjNlYzQyNjdjNzkxZjUyNDhhMzU3ZjVmNzgwZDYzNDY4MTJjNzA0ZWI4ZiJ9fX0="),
                EditorLocales.DUNGEON_SETTINGS, 31).setClick((viewer, event) -> this.plugin.runTask(task -> this.getSettingsEditor().open(viewer.getPlayer(), 1)));
//        this.addItem(barrierHead, EditorLocales.DUNGEON_PARTICLE, 16).setClick((viewer, event) -> {
//            viewer.getPlayer().sendMessage(Colorizer.apply("&cThis page will be available later\nFollow the news in our discord: &lhttps://discord.gg/ajnPb3fdKq"));
//        });
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZjYmFlNzI0NmNjMmM2ZTg4ODU4NzE5OGM3OTU5OTc5NjY2YjRmNWE0MDg4ZjI0ZTI2ZTA3NWYxNDBhZTZjMyJ9fX0="),
                EditorLocales.DUNGEON_PARTY, 32).setClick((viewer, event) -> this.getPartySettingsEditor().openNextTick(viewer.getPlayer(), 1));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ5MjQ0ODkyODJhYmViMzhhZWFjYmY0YzBlYjNhZDQwMGQ1MjU3ZmZiM2E2MDViODdjZjIzMWM5MmZhMmY0YyJ9fX0="),
                EditorLocales.COMMANDS_SETTINGS, 33).setClick((viewer, event) -> this.getCommandsSettingsEditor().openNextTick(viewer.getPlayer(), 1));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRlMDBhNjEyZGE2NDFmYWFjNzc4OWU3ZmZkZjRmZThjODY4ODQwZTc1MmUwYWFhNzc2ODYyMGFkOTkyN2U0MCJ9fX0="),
                        EditorLocales.GENERATION_SETTINGS, 40)
                .setClick((viewer, event) -> this.getGenerationSettingsEditor().openNextTick(viewer.getPlayer(), 1));

        if (EngineUtils.hasPlugin("Fabled")) {
            addItem(
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhMjhiYTNiYTc5YmUxOTU0NzEwZDRkYjJhM2ZkMjI3NzNmNjE5ZjE4ZmVjZjU5ODIzNTNmYjdhYzE4MzkzYSJ9fX0="),
                    EditorLocales.ACCESS_SETTINGS, 41
            ).setClick((menuViewer, inventoryClickEvent) -> {
                getAccessSettingsEditor().openNextTick(menuViewer.getPlayer(), 1);
            });
        }

        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjliMjg4OTAyMDU4MzU2NWY4OGQ1MDUzNzg3MGM1OWFhZDgwMjU5NGZhYmQ4MzdlMWQxNGY1YTA2YWUzNDUwOSJ9fX0="),
                EditorLocales.DUNGEON_WORLD, 4).setClick((viewer, event) -> {
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
                    dungeon.getTimer().reboot();
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
                    ItemReplacer.replace(item, dungeon.getRegion().replacePlaceholders());
                    ItemReplacer.replace(item, dungeon.getSettings().replacePlaceholders());
                    ItemReplacer.replace(item, dungeon.getAccessSettings().replacePlaceholders());
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
    public ChestSettingsEditor getChestStateSettingsEditor() {
        if (this.chestStateSettingsEditor == null) {
            this.chestStateSettingsEditor = new ChestSettingsEditor(this.object.getChestSettings());
        }
        return chestStateSettingsEditor;
    }

    @NotNull
    public AnnounceSettingsEditor getAnnounceSettingsEditor() {
        if (this.announceSettingsEditor == null) {
            this.announceSettingsEditor = new AnnounceSettingsEditor(this.object.getAnnounceSettings());
        }
        return this.announceSettingsEditor;
    }

    @NotNull
    public ModuleSettingsEditor getModuleSettingsEditor() {
        if (this.moduleSettingsEditor == null) {
            this.moduleSettingsEditor = new ModuleSettingsEditor(this.object.getModuleSettings());
        }
        return moduleSettingsEditor;
    }

    @NotNull
    public SchematicsSettingsEditor getSchematicsSettingsEditor() {
        if (this.schematicsSettingsEditor == null) {
            this.schematicsSettingsEditor = new SchematicsSettingsEditor(this.object.getSchematicSettings());
        }
        return schematicsSettingsEditor;
    }

    @NotNull
    public CommandsSettingsEditor getCommandsSettingsEditor() {
        if (this.commandsSettingsEditor == null) {
            this.commandsSettingsEditor = new CommandsSettingsEditor(this.object.getCommandsSettings());
        }
        return commandsSettingsEditor;
    }

    @NotNull
    public StageSettingsEditor getStageSettingsEditor() {
        if (this.stageSettingsEditor == null) {
            this.stageSettingsEditor = new StageSettingsEditor(this.object.getStageSettings());
        }
        return stageSettingsEditor;
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
            this.regionMainEditor = new DungeonRegionMainEditor(this.object.getRegion());
        }
        return regionMainEditor;
    }

    @NotNull
    public MainSettingsEditor getSettingsEditor() {
        if (this.settingsEditor == null) {
            this.settingsEditor = new MainSettingsEditor(this.object.getSettings());
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

    @NotNull
    public GenerationSettingsEditor getGenerationSettingsEditor() {
        if (this.generationSettingsEditor == null) {
            this.generationSettingsEditor = new GenerationSettingsEditor(this.object.getGenerationSettings());
        }
        return generationSettingsEditor;
    }

    @NotNull
    public AccessSettingsEditor getAccessSettingsEditor() {
        if (this.accessSettingsEditor == null) {
            this.accessSettingsEditor = new AccessSettingsEditor(this.object.getAccessSettings());
        }
        return accessSettingsEditor;
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