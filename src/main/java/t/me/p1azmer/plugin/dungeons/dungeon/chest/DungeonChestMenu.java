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
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;

import java.util.*;

public class DungeonChestMenu extends AbstractListener<DungeonPlugin> implements ICleanable {

    private Inventory inventory;
    private Collection<Player> players;
    private Block block;

    public DungeonChestMenu(@NotNull Block block, @NotNull Dungeon dungeon, @NotNull List<DungeonReward> rewards) {
        super(dungeon.plugin());

        this.block = block;
        this.players = new HashSet<>();
        this.inventory = Bukkit.createInventory(null, dungeon.getChestSettings().isBigMenu() ? 54 : 27, Colorizer.apply(dungeon.getName()));
        if (dungeon.getChestSettings().isRandomSlots()) {
            this.addRewardsToRandomSlots(this.inventory, rewards);
        } else {
            this.inventory.setContents(rewards.stream().map(f -> { // TODO add check if max is more at 64
                ItemStack itemStack = f.getItem();
                itemStack.setAmount(Rnd.get(f.getMinAmount(), f.getMaxAmount()));
                return itemStack;
            }).toArray(ItemStack[]::new));
        }
        this.registerListeners();
    }

    public void addRewardsToRandomSlots(Inventory inventory, List<DungeonReward> rewards) {
        List<DungeonReward> shuffledRewards = new ArrayList<>(rewards);
        Collections.shuffle(shuffledRewards);

        Random random = new Random();

        for (DungeonReward reward : shuffledRewards) {
            ItemStack item = reward.getItem();
            item.setAmount(Rnd.get(reward.getMinAmount(), reward.getMaxAmount()));
            int maxSlots = inventory.getSize();

            for (int i = 0; i < maxSlots; i++) {
                int slot = random.nextInt(maxSlots);
                ItemStack currentSlotItem = inventory.getItem(slot);

                if (currentSlotItem == null) {
                    inventory.setItem(slot, item);
                    break;
                }
            }
        }
    }

    public void open(@NotNull Block block, @NotNull Player player) {
        if (this.block.equals(block) || block.getLocation().equals(this.block.getLocation())) {
            this.players.add(player);
            player.openInventory(this.getInventory());
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

        this.block = null;
    }
}
