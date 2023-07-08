package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonReward;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class DungeonRewardMainEditor extends EditorMenu<DungeonPlugin, DungeonReward> {

    public DungeonRewardMainEditor(@NotNull DungeonReward reward) {
        super(reward.plugin(), reward, Config.EDITOR_TITLE_CRATE.get(), 45);
        Dungeon crate = reward.getDungeon();

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> crate.getEditor().getEditorRewards().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.REWARD_ITEM, 4).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), reward.getItem());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().equals(Material.AIR)) {
                reward.setItem(cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(reward.getItem().getType());
            item.setItemMeta(reward.getItem().getItemMeta());
            ItemUtil.mapMeta(item, meta -> {
                meta.setDisplayName(EditorLocales.REWARD_ITEM.getLocalizedName());
                meta.setLore(EditorLocales.REWARD_ITEM.getLocalizedLore());
                meta.addItemFlags(ItemFlag.values());
            });
        }));

        this.addItem(Material.NAME_TAG, EditorLocales.REWARD_NAME, 19).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.setName(ItemUtil.getItemName(reward.getItem()));
                this.save(viewer);
                return;
            }
            if (event.isShiftClick() && event.isLeftClick()) {
                ItemStack preview = reward.getItem();
                ItemUtil.mapMeta(preview, meta -> meta.setDisplayName(reward.getName()));
                reward.setItem(preview);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
                reward.setName(wrapper.getText());
                crate.save();
                return true;
            });
        });

//        this.addItem(Material.ENDER_EYE, EditorLocales.REWARD_BROADCAST, 20).setClick((viewer, event) -> {
//            reward.setBroadcast(!reward.isBroadcast());
//            this.save(viewer);
//        });

        this.addItem(Material.REDSTONE_COMPARATOR, EditorLocales.REWARD_CHANCE, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
                reward.setChance(wrapper.asDouble());
                crate.save();
                return true;
            });
        });


        this.addItem(Material.COMMAND_REPEATING, EditorLocales.REWARD_LIMITS, 22).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_WIN_LIMIT_AMOUNT, wrapper -> {
                    reward.setMaxAmount(wrapper.asAnyInt(3));
                    crate.save();
                    return true;
                });
            } else {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_WIN_LIMIT_COOLDOWN, wrapper -> {
                    reward.setMinAmount(wrapper.asAnyInt(1));
                    crate.save();
                    return true;
                });
            }
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemUtil.replace(item, reward.replacePlaceholders())));
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