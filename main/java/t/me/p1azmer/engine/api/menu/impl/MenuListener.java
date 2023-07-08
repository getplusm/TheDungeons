package t.me.p1azmer.engine.api.menu.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class MenuListener extends AbstractListener<DungeonPlugin> {

    private static final Map<UUID, Long> FAST_CLICK = new WeakHashMap<>();

    public MenuListener(@NotNull DungeonPlugin engine) {
        super(engine);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Menu.PLAYER_MENUS.remove(e.getPlayer().getUniqueId());
        FAST_CLICK.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuItemClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        Menu<?> menu = Menu.getMenu(player);
        if (menu == null) return;

        MenuViewer viewer = menu.getViewer(player);
        if (viewer == null) return;

        // Fix visual glitch when item goes in player's offhand.
        // Or classic "DUPE BUG SHIFT CLICK AND CLOSE GUI!!!"
        if (//e.getClick() == ClickType.SWAP_OFFHAND ||
                e.isShiftClick()) {
            this.plugin.runTask(task -> player.updateInventory());
        }

        // Prevent clicks spam in our GUIs.
        long lastClick = FAST_CLICK.getOrDefault(player.getUniqueId(), 0L);
        if (System.currentTimeMillis() - lastClick < 150) {
            e.setCancelled(true);
            return;
        }

        Inventory inventory = e.getInventory();
        ItemStack item = e.getCurrentItem();

        int slot = e.getRawSlot();
        boolean isPlayerSlot = slot >= inventory.getSize();
        boolean isEmptyItem = item == null || item.getType().equals(Material.AIR);

        Menu.SlotType slotType;
        if (isPlayerSlot) {
            slotType = isEmptyItem ? Menu.SlotType.PLAYER_EMPTY : Menu.SlotType.PLAYER;
        } else slotType = isEmptyItem ? Menu.SlotType.MENU_EMPTY : Menu.SlotType.MENU;

        menu.onClick(viewer, item, slotType, slot, e);
        FAST_CLICK.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuItemDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();

        Menu<?> menu = Menu.getMenu(player);
        if (menu == null) return;

        MenuViewer viewer = menu.getViewer(player);
        if (viewer == null) return;

        menu.onDrag(viewer, e);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMenuClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        Menu<?> menu = Menu.getMenu(player);
        if (menu == null) return;

        MenuViewer viewer = menu.getViewer(player);
        if (viewer == null) return;

        menu.onClose(viewer, e);
        FAST_CLICK.remove(player.getUniqueId());
    }
}