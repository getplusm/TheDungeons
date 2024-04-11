package t.me.p1azmer.plugin.dungeons.dungeon.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonDespawnEvent;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonSpawnEvent;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.MainSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.PartySettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.utils.Cuboid;

import java.util.Collection;
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
        if (block == null || (event.useInteractedBlock() == Event.Result.DENY && block.getType() != Material.BARRIER)) return;

        Dungeon dungeon = this.manager.getDungeonByBlock(block);
        if (dungeon == null) return;

        event.setCancelled(true);

        if (event.getHand() != EquipmentSlot.HAND) return;

        this.manager.interactDungeon(player, dungeon, block);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) return;

        Dungeon dungeon = manager.getDungeonByLocation(event.getTo(), event.getTo().getBlock());
        if (dungeon == null) return;

        MainSettings mainSettings = dungeon.getSettings();
        if (mainSettings.isLetPlayersWhenClose()) return;

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        DungeonStage dungeonStage = dungeon.getStage();
        PartySettings partySettings = dungeon.getPartySettings();
        PartyHandler partyHandler = plugin.getPartyHandler();

        if (dungeonStage.isClosed() || dungeonStage.isPrepare()) {
            player.setVelocity(direction.setY(-0.4D).multiply(-0.45D));
            if (messageCache.add(player))
                plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_CLOSE).send(player);
            return;
        }

        if (partyHandler != null && partySettings.isEnabled()) {
            boolean notAllowed = !partyHandler.isInParty(player) || partyHandler.getPartySize(player) < partySettings.getSize();
            if (notAllowed) {
                player.setVelocity(direction.setY(-0.4D).multiply(-0.45D));
                if (messageCache.add(player))
                    plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_NOT_PARTY)
                            .replace(partySettings.replacePlaceholders())
                            .send(player);
            }
        }
    }

    @EventHandler
    public void onSpawn(DungeonSpawnEvent event){
        Dungeon dungeon = event.getDungeon();
        Location result = event.getLocation();
        Region region = dungeon.getRegion();

        dungeon.setLocation(result);

        Location lowerLocation = new Location(result.getWorld(), result.getBlockX(), result.getBlockY(), result.getBlockZ());
        Location upperLocation = new Location(result.getWorld(), result.getBlockX(), result.getBlockY(), result.getBlockZ());
        int size = region.getRadius();

        lowerLocation.subtract(size, size, size);
        upperLocation.add(size, size, size);

        if (lowerLocation.getY() > upperLocation.getY()) {
            double temp = lowerLocation.getY();
            lowerLocation.setY(upperLocation.getY());
            upperLocation.setY(temp);
        }
        Cuboid cuboid = new Cuboid(lowerLocation, upperLocation);
        dungeon.setCuboid(cuboid);
        plugin.sendDebug(dungeon.getId() + ": Location & Cuboid success installed");
    }

    @EventHandler
    public void onDespawn(DungeonDespawnEvent event){
        Dungeon dungeon = event.getDungeon();
        dungeon.setLocation(null);
        dungeon.setCuboid(null);
        plugin.sendDebug(dungeon.getId() + ": Location & Cuboid success removed");
    }

    @EventHandler
    public void onStageChange(DungeonChangeStageEvent event){
        Dungeon dungeon = event.getDungeon();
        DungeonStage stage = event.getStage();
        String from = event.getChangeFrom();
        GenerationSettings generationSettings = dungeon.getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();

        if (stage.isDeleting() || stage.isCancelled()) {
            Collection<AbstractModule> modules = dungeon.getModuleManager().getModules();
            if (modules.stream()
                    .filter(module -> {
                        boolean generationAllowed = !generationType.isDynamic() && generationSettings.getSpawnLocation().isPresent();
                        return !generationAllowed || !module.isImportantly();
                    })
                    .anyMatch(module -> !module.tryDeactivate(AbstractModule.ActionType.NATURAL))) return;

            dungeon.cancel(false);
            return;
        }
        dungeon.setStage(stage);
        dungeon.setSelfTick(0);

        plugin.sendDebug("Call the dungeon '" + dungeon.getId() + "' from " + from + ". Change stage to " + stage.name() + " from " + dungeon.getStage().name());
    }
}