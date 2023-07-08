package t.me.p1azmer.plugin.dungeons.menu;

import org.bukkit.Bukkit;
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
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonReward;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class DungeonGUI extends AbstractListener<DungeonPlugin> implements ICleanable {

    private Inventory inventory;
    private Collection<Player> players;

    public DungeonGUI(@NotNull Dungeon dungeon, List<DungeonReward> rewards) {
        super(dungeon.plugin());
        this.players = new HashSet<>();
        this.inventory = Bukkit.createInventory(null, 27, Colorizer.apply(dungeon.getName()));
        this.inventory.setContents(rewards.stream().map(DungeonReward::getItem).toArray(ItemStack[]::new));
        this.registerListeners();
    }

    public void open(Player player) {
        this.players.add(player);
        player.openInventory(this.getInventory());
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            if (event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();
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
    }
}
