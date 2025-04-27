package t.me.p1azmer.plugin.dungeons.dungeon.editor.reward;

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
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static t.me.p1azmer.plugin.dungeons.editor.EditorLocales.REWARDS_LIMITS;

public class DungeonRewardListEditor extends EditorMenu<DungeonPlugin, Dungeon> implements AutoPaged<Reward> {

    public DungeonRewardListEditor(@NotNull Dungeon dungeon) {
        super(dungeon.plugin(), dungeon, Config.EDITOR_TITLE_DUNGEON.get(), 54);

        this.addReturn(49).setClick((viewer, event) -> this.plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1)));
        this.addNextPage(50);
        this.addPreviousPage(48);

        this.addCreation(EditorLocales.REWARD_CREATE, 52).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (!cursor.getType().isAir()) {
                String id = StringUtil.lowerCaseUnderscore(ItemUtil.getItemName(cursor));
                int count = 0;
                while (dungeon.getRewards().getReward(count == 0 ? id : id + count).orElse(null) != null) {
                    count++;
                }
                Reward reward = new Reward(dungeon, count == 0 ? id : id + count);
                reward.setItem(cursor);
                dungeon.getRewards().addReward(reward);
                event.getView().setCursor(null);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_ID, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (dungeon.getRewards().getReward(id).isPresent()) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REWARD_ERROR_CREATE_EXIST).getLocalized());
                    return false;
                }
                Reward reward = new Reward(dungeon, id);
                dungeon.getRewards().addReward(reward);
                return true;
            });
        });

        this.addItem(Material.HOPPER, EditorLocales.REWARD_SORT, 46).setClick((viewer, event) -> {
            Comparator<Reward> comparator;
            t.me.p1azmer.engine.api.menu.click.ClickType type = t.me.p1azmer.engine.api.menu.click.ClickType.from(event);
            if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_1) {
                comparator = Comparator.comparingDouble(Reward::getChance).reversed();
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_2) {
                comparator = Comparator.comparing(r -> r.getItem().getType().name());
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_3) {
                comparator = Comparator.comparing(r -> ItemUtil.getItemName(r.getItem()));
            } else return;
            dungeon.getRewards().setRewards(dungeon.getRewardCollection().stream().sorted(comparator).toList());
            this.save(viewer);
        });

        this.addItem(Material.CHEST_MINECART, REWARDS_LIMITS, 45).setClick((viewer, event) -> this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_UNI_LIMIT, wrapper -> {
            dungeon.getRewardSettings().setLimit(wrapper.asUniInt());
            dungeon.save();
            return true;
        })).getOptions().setDisplayModifier((viewer, itemStack) -> {
            itemStack.setAmount(Math.max(1, dungeon.getRewardSettings().getLimit().getMaxValue()));
            ItemReplacer.create(itemStack)
                    .readLocale(REWARDS_LIMITS)
                    .replace(dungeon.getRewardSettings().replacePlaceholders())
                    .writeMeta();
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
    public List<Reward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRewardCollection());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Reward reward) {
        ItemStack item = new ItemStack(reward.getItem());
        ItemReplacer.create(item).readLocale(EditorLocales.REWARD_OBJECT)
                .trimmed()
                .hideFlags()
                .replace(reward.replacePlaceholders())
                .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Reward reward) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.getClick() == ClickType.DROP) {
                this.object.getRewards().removeReward(reward);
                this.save(viewer);
                return;
            }

            if (event.isShiftClick()) {
                // Reward position move.
                List<Reward> all = new ArrayList<>(this.object.getRewardCollection());
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
                this.object.getRewards().setRewards(all);
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