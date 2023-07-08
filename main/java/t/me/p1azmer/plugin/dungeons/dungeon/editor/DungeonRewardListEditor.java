package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonReward;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DungeonRewardListEditor extends EditorMenu<DungeonPlugin, Dungeon> implements AutoPaged<DungeonReward> {

    public DungeonRewardListEditor(@NotNull Dungeon crate) {
        super(crate.plugin(), crate, Config.EDITOR_TITLE_CRATE.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(rask -> crate.getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REWARD_CREATE, 42).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().equals(Material.AIR)) {
                String id = StringUtil.lowerCaseUnderscore(ItemUtil.getItemName(cursor));
                int count = 0;
                while (crate.getReward(count == 0 ? id : id + count) != null) {
                    count++;
                }
                DungeonReward reward = new DungeonReward(crate, count == 0 ? id : id + count);
                reward.setName(ItemUtil.getItemName(cursor));
                reward.setItem(cursor);
                crate.addReward(reward);
                event.getView().setCursor(null);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_ID, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (crate.getReward(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REWARD_ERROR_CREATE_EXIST).getLocalized());
                    return false;
                }
                DungeonReward reward = new DungeonReward(crate, id);
                crate.addReward(reward);
                return true;
            });
        });

        this.addItem(Material.HOPPER, EditorLocales.REWARD_SORT, 38).setClick((viewer, event) -> {
            Comparator<DungeonReward> comparator;
            t.me.p1azmer.engine.api.menu.click.ClickType type = t.me.p1azmer.engine.api.menu.click.ClickType.from(event);
            if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_1) {
                comparator = Comparator.comparingDouble(DungeonReward::getChance).reversed();
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_2) {
                comparator = Comparator.comparing(r -> r.getItem().getType().name());
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_3) {
                comparator = Comparator.comparing(r -> ItemUtil.getItemName(r.getItem()));
            }
            else return;
            crate.setRewards(crate.getRewards().stream().sorted(comparator).collect(Collectors.toList()));
            this.save(viewer);
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<DungeonReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRewards());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull DungeonReward reward) {
        ItemStack item = new ItemStack(reward.getItem());
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.REWARD_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.REWARD_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, reward.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull DungeonReward reward) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.getClick() == ClickType.DROP) {
                this.object.removeReward(reward);
                this.save(viewer);
                return;
            }

            if (event.isShiftClick()) {
                // Reward position move.
                List<DungeonReward> all = new ArrayList<>(this.object.getRewards());
                int index = all.indexOf(reward);
                int allSize = all.size();

                if (event.isLeftClick()) {
                    if (index + 1 >= allSize) return;

                    all.remove(index);
                    all.add(index + 1, reward);
                } else if (event.isRightClick()) {
                    if (index == 0) return;

                    all.remove(index);
                    all.add(index - 1, reward);
                }
                this.object.setRewards(all);
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.plugin.runTask(task -> reward.getEditor().open(player, 1));
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