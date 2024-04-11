package t.me.p1azmer.plugin.dungeons.dungeon.editor.reward;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.values.UniInt;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class DungeonRewardMainEditor extends EditorMenu<DungeonPlugin, Reward> {

    public DungeonRewardMainEditor(@NotNull Reward reward) {
        super(reward.plugin(), reward, Config.EDITOR_TITLE_DUNGEON.get(), 45);
        Dungeon dungeon = reward.dungeon();

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

        this.addItem(Material.COMPARATOR, EditorLocales.REWARD_CHANCE, 20).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
                double chance = wrapper.asDouble();
                if (chance > 100) chance = 100;
                else if (chance < 0) chance = 0;
                reward.setChance(chance);
                dungeon.save();
                return true;
            });
        });


        this.addItem(Material.REPEATER, EditorLocales.REWARD_AMOUNT, 22).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_UNI_AMOUNT, wrapper -> {
                    UniInt amount = wrapper.asUniInt();
                    reward.setAmount(amount);
                    dungeon.save();
                    return true;
                });
            }
        });
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZlMGU0ZjA2MDlhMmM1YWQ2MTdhOGM0MWQ3MTZlNjM3ZDNkMDU3MmEyYzAwN2ZlN2QzN2VkNTA2OTZiM2RkYiJ9fX0="),
                EditorLocales.REWARD_COMMANDS, 24).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.getCommands().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_COMMAND, wrapper -> {
                reward.getCommands().add(wrapper.getText());
                dungeon.save();
                return true;
            });
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, reward.replacePlaceholders()))));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
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