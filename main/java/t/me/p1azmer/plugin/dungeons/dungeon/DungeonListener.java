package t.me.p1azmer.plugin.dungeons.dungeon;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

public class DungeonListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager crateManager;

    public DungeonListener(@NotNull DungeonManager crateManager) {
        super(crateManager.plugin());
        this.crateManager = crateManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDungeonUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null || (e.useInteractedBlock() == Event.Result.DENY && block.getType() != Material.BARRIER))
            return;

        Dungeon dungeon = this.crateManager.getDungeonByBlock(block);
        if (dungeon == null) {
            return;
        }

        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setCancelled(true);

        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.crateManager.interactDungeon(player, dungeon, block);
    }
}