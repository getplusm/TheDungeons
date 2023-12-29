package t.me.p1azmer.plugin.dungeons.dungeon.editor.reward;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class DungeonRewardMainEditor extends EditorMenu<DungeonPlugin, DungeonReward> {

    public DungeonRewardMainEditor(@NotNull DungeonReward reward) {
        super(reward.plugin(), reward, Config.EDITOR_TITLE_DUNGEON.get(), 45);
        Dungeon dungeon = reward.getDungeon();

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> dungeon.getEditor().getEditorRewards().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.REWARD_ITEM, 4).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), reward.getItem());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
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
                dungeon.save();
                return true;
            });
        });

//        this.addItem(Material.ENDER_EYE, EditorLocales.REWARD_BROADCAST, 20).setClick((viewer, event) -> {
//            reward.setBroadcast(!reward.isBroadcast());
//            this.save(viewer);
//        });

        this.addItem(Material.COMPARATOR, EditorLocales.REWARD_CHANCE, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
                double chance = wrapper.asDouble();
                if (chance > 100) chance = 100;
                else if (chance < 0) chance = 0;
                reward.setChance(chance);
                dungeon.save();
                return true;
            });
        });


        this.addItem(Material.REPEATER, EditorLocales.REWARD_LIMITS, 22).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_MAX_AMOUNT, wrapper -> {
                    int amount = wrapper.asInt(3);
                    if (reward.getMinAmount() >= amount) {
                        EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_REWARD_ERROR_LIMIT_MAX).getLocalized());
                        return false;
                    }
                    reward.setMaxAmount(amount);
                    dungeon.save();
                    return true;
                });
            } else {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_MIN_AMOUNT, wrapper -> {
                    int amount = wrapper.asInt(3);
                    if (amount >= reward.getMaxAmount()) {
                        EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_REWARD_ERROR_LIMIT_MIN).getLocalized());
                        return false;
                    }
                    reward.setMinAmount(amount);
                    dungeon.save();
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
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}