package t.me.p1azmer.plugin.dungeons.dungeon.categories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.reward.DungeonRewardMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

// TODO rewrite
public class DungeonReward implements ICleanable, IPlaceholderMap {
    private final Dungeon dungeon;
    private final String id;

    private String name;
    private double chance;
    private ItemStack item;
    private int minAmount;
    private int maxAmount;

    private DungeonRewardMainEditor editor;
    private final PlaceholderMap placeholderMap;

    public DungeonReward(@NotNull Dungeon dungeon, @NotNull String id) {
        this(dungeon,
                id,
                ChatColor.GREEN + StringUtil.capitalizeUnderscored(id),
                25,
                1,
                3,
                new ItemStack(Material.STONE)
        );
    }

    public DungeonReward(
            @NotNull Dungeon dungeon,
            @NotNull String id,
            @NotNull String name,
            double chance,
            int minAmount,
            int maxAmount,
            @NotNull ItemStack item
    ) {
        this.dungeon = dungeon;
        this.id = id.toLowerCase();

        this.setName(name);
        this.setChance(chance);

        this.setMinAmount(minAmount);
        this.setMaxAmount(maxAmount);

        this.setItem(item);

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REWARD_ID, this::getId)
                .add(Placeholders.REWARD_NAME, this::getName)
                .add(Placeholders.REWARD_CHANCE, () -> NumberUtil.format(this.getChance()))
                .add(Placeholders.REWARD_ITEM_NAME, () -> ItemUtil.getItemName(this.getItem()))
                .add(Placeholders.REWARD_ITEM_LORE, () -> String.join("\n", ItemUtil.getLore(this.getItem())))
                .add(Placeholders.REWARD_MAX_AMOUNT, () -> String.valueOf(this.getMaxAmount()))
                .add(Placeholders.REWARD_MIN_AMOUNT, () -> String.valueOf(getMinAmount()))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public DungeonPlugin plugin() {
        return this.getDungeon().plugin();
    }

    @NotNull
    public DungeonRewardMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new DungeonRewardMainEditor(this);
        }
        return this.editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Dungeon getDungeon() {
        return this.dungeon;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = Math.max(0, chance);
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(item);
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = item;
    }
}
