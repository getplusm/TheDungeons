package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.ChestSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.RewardSettings;

import java.util.*;
import java.util.function.UnaryOperator;

@Deprecated
public class DungeonChestMenu extends AbstractListener<DungeonPlugin> implements ICleanable {
    private final Dungeon dungeon;
    private Inventory inventory;
    private Collection<Player> players;
    private Block block;
    private final Set<Reward> cachedRewards;
    private boolean commandsTriggered;

    public DungeonChestMenu(@NotNull Block block, @NotNull Dungeon dungeon, @NotNull Map<Reward, Double> rewards) {
        super(dungeon.plugin());
        this.dungeon = dungeon;

        this.cachedRewards = new HashSet<>();
        this.commandsTriggered = false;
        this.block = block;
        this.players = new HashSet<>();
        this.inventory = Bukkit.createInventory(null, dungeon.getChestSettings().isBigMenu() ? 54 : 27, Colorizer.apply(dungeon.getName()));

        this.initRewards(rewards, dungeon.getChestSettings(), dungeon.getRewardSettings());
        this.registerListeners();
    }

    public void initRewards(@NotNull Map<Reward, Double> rewards, @NotNull ChestSettings settings, @NotNull RewardSettings rewardSettings) {
        for (int i = 0; i < rewardSettings.getLimit().roll(); i++) {
            Reward reward = Rnd.getByWeight(rewards);
            ItemStack item = reward.getItem();
            item.setAmount(reward.getAmount().roll());
            this.cachedRewards.add(reward);

            if (settings.isRandomSlots()) {
                int slot = Rnd.get(0, this.inventory.getSize()-1);
                ItemStack currentItem = this.inventory.getItem(slot);
                if (currentItem == null || currentItem.getType().isAir())
                    this.inventory.setItem(slot, item);
                continue;
            }
            this.inventory.addItem(reward.getItem());
        }
    }

    public void open(@NotNull Block block, @NotNull Player player) {
        if (this.block.equals(block) || block.getLocation().equals(this.block.getLocation())) {
            this.players.add(player);
            player.openInventory(this.getInventory());
            if (!this.commandsTriggered) {
                this.cachedRewards.forEach(reward -> reward.getCommands().forEach(command -> {
                    UnaryOperator<String> inter = PlaceholderMap.fusion(reward.getPlaceholders(), this.dungeon.getPlaceholders()).replacer();
                    PlayerUtil.dispatchCommand(player, inter.apply(command));
                }));
                this.commandsTriggered = true;
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            if (event.getPlayer() instanceof Player player) {
                this.players.remove(player);
            }
        }
    }

    @Override
    public void clear() {
        this.unregisterListeners();
        if (this.players != null) {
            this.players.clear();
            this.players = null;
        }
        if (this.inventory != null) {
            new HashSet<>(this.inventory.getViewers()).forEach(HumanEntity::closeInventory);
            this.inventory.clear();
            this.inventory = null;
        }

        this.cachedRewards.clear();
        this.commandsTriggered = false;
        this.block = null;
    }
}
