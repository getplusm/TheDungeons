package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.editor.InputHandler;
import t.me.p1azmer.engine.api.manager.IListener;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class DungeonMainEditor extends EditorMenu<DungeonPlugin, Dungeon> implements IListener {

    private DungeonRewardListEditor editorRewards;
    private boolean isReadyForBlock = false;

    public DungeonMainEditor(@NotNull Dungeon crate) {
        super(crate.plugin(), crate, Config.EDITOR_TITLE_CRATE.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().getCratesEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.NAME_TAG, EditorLocales.DUNGEON_NAME, 4).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
                crate.setName(wrapper.getText());
                crate.save();
                return true;
            });
        });
        this.addItem(Material.DIRT, EditorLocales.DUNGEON_SCHEMATIC, 9).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    crate.setSchematics(new ArrayList<>());
//                    crate.updateHologram();
                    this.save(viewer);
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_ENTER_SCHEMATIC, wrapper -> {
                        List<String> list = crate.getSchematics();
                        list.add(wrapper.getText());
                        crate.setSchematics(list);
//                        crate.updateHologram();
                        crate.save();
                        return true;
                    });
                }
            }
        });

        this.addItem(Material.TRIPWIRE_HOOK, EditorLocales.DUNGEON_KEYS, 11).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_KEY_ID, wrapper -> {
                    crate.getKeyIds().add(wrapper.getTextRaw());
                    crate.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKeyManager().getKeyIds(), true);
            } else if (event.isRightClick()) {
                crate.getKeyIds().clear();
                this.save(viewer);
            }
        });

        this.addItem(Material.EMERALD, EditorLocales.DUNGEON_REWARDS, 13).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getEditorRewards().open(viewer.getPlayer(), 1));
        });


        this.addItem(Material.ARMOR_STAND, EditorLocales.DUNGEON_BLOCK_HOLOGRAM_OPEN, 15).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    crate.setOpenMessage(new ArrayList<>());
//                    crate.updateHologram();
                    this.save(viewer);
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_BLOCK_HOLOGRAM_TEXT, wrapper -> {
                        List<String> list = crate.getOpenMessage();
                        list.add(wrapper.getText());
                        crate.setOpenMessage(list);
//                        crate.updateHologram();
                        crate.save();
                        return true;
                    });
                }
            }
        });
        this.addItem(Material.ARMOR_STAND, EditorLocales.DUNGEON_BLOCK_HOLOGRAM_CLOSE, 16).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    crate.setCloseMessage(new ArrayList<>());
//                    crate.updateHologram();
                    this.save(viewer);
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_BLOCK_HOLOGRAM_TEXT, wrapper -> {
                        List<String> list = crate.getCloseMessage();
                        list.add(wrapper.getText());
                        crate.setCloseMessage(list);
//                        crate.updateHologram();
                        crate.save();
                        return true;
                    });
                }
            }
        });
        this.addItem(Material.ARMOR_STAND, EditorLocales.DUNGEON_BLOCK_HOLOGRAM_WAIT, 17).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    crate.setWaitMessage(new ArrayList<>());
                    this.save(viewer);
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_BLOCK_HOLOGRAM_TEXT, wrapper -> {
                        List<String> list = crate.getWaitMessage();
                        list.add(wrapper.getText());
                        crate.setWaitMessage(list);
                        crate.save();
                        return true;
                    });
                }
            }
        });
        this.addItem(Material.STONE_BUTTON, EditorLocales.DUNGEON_OPEN_TYPE, 26).setClick((viewer, event) -> {
            crate.setOpenType(crate.getOpenType().isClick() ? Dungeon.OpenType.TIMER : Dungeon.OpenType.CLICK);
            crate.save();
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemUtil.replace(item, crate.replacePlaceholders());
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