package t.me.p1azmer.plugin.dungeons.dungeon.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.concurrent.TimeUnit;

public class DungeonListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager manager;
    private final AutoRemovalCollection<Player> messageCache = AutoRemovalCollection.newHashSet(3, TimeUnit.SECONDS);

    public DungeonListener(@NotNull DungeonManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDungeonUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null || (event.useInteractedBlock() == Event.Result.DENY && block.getType() != Material.BARRIER)) {
            return;
        }

        Dungeon dungeon = this.manager.getDungeonByBlock(block);
        if (dungeon == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.manager.interactDungeon(player, dungeon, block);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) {
            return;
        }
        Dungeon dungeon = manager.getDungeonByLocation(event.getTo(), event.getTo().getBlock());
        if (dungeon == null) {
            return;
        }
        if (dungeon.getSettings().isLetPlayersWhenClose()) {
            return;
        }
        if (dungeon.getStage().isClosed() || dungeon.getStage().isPrepare()) {
            player.setVelocity(player.getEyeLocation().getDirection().setY(-0.4D).multiply(-0.45D));
            if (messageCache.add(player))
                plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_CLOSE).send(player);
        }
        if (plugin.getPartyHandler() != null && dungeon.getPartySettings().isEnabled()) {
            if (!this.plugin.getPartyHandler().isInParty(player) || plugin.getPartyHandler().getPartySize(player) < dungeon.getPartySettings().getSize()) {
                player.setVelocity(player.getEyeLocation().getDirection().setY(-0.4D).multiply(-0.45D));
                if (messageCache.add(player))
                    plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_NOT_PARTY)
                            .replace(dungeon.getPartySettings().replacePlaceholders())
                            .send(player);
            }
        }
    }
}