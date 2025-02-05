package t.me.p1azmer.plugin.dungeons.dungeon.listener;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonChangeStageEvent;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonDespawnEvent;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonSpawnEvent;
import t.me.p1azmer.plugin.dungeons.api.handler.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DungeonListener extends AbstractListener<DungeonPlugin> {
    static final AutoRemovalCollection<Player> messageCache = AutoRemovalCollection.newHashSet(3, TimeUnit.SECONDS);
    DungeonManager manager;

    public DungeonListener(@NotNull DungeonManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleInteractDungeon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null || (event.useInteractedBlock() == Event.Result.DENY && block.getType() != Material.BARRIER)) {
            return;
        }

        Dungeon dungeon = manager.getDungeonByBlock(block);
        if (dungeon == null) return;

        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) return;

        manager.interactDungeon(player, dungeon, block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleMoveAtDungeon(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Dungeon dungeon = manager.getDungeonByLocation(event.getTo());
        if (dungeon == null) return;

        MainSettings mainSettings = dungeon.getSettings();
        if (mainSettings.isLetPlayersWhenClose()) return;

        DungeonStage dungeonStage = dungeon.getStage();
        PartySettings partySettings = dungeon.getPartySettings();
        PartyHandler partyHandler = plugin.getPartyHandler();
        AccessHandler accessHandler = plugin.getAccessHandler();

        if (accessHandler != null && !accessHandler.allowedToEnterDungeon(dungeon, player)) {
            handleAccessDenied(player, dungeon.getAccessSettings().getNotAccessMessage());
            return;
        }

        if (dungeonStage.isClosed() || dungeonStage.isPrepare()) {
            handleAccessDenied(player, plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_CLOSE).toString());
            return;
        }

        if (partyHandler != null && partySettings.isEnabled() &&
                (!partyHandler.isInParty(player) || partyHandler.getPartySize(player) < partySettings.getSize())) {
            handleAccessDenied(player, plugin.getMessage(Lang.DUNGEON_BACKTRACK_PLAYER_WHEN_NOT_PARTY)
                    .replace(partySettings.replacePlaceholders()).toString());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleDungeonSpawn(AsyncDungeonSpawnEvent event) {
        Dungeon dungeon = event.getDungeon();
        Location result = event.getLocation();
        Region region = dungeon.getRegion();

        dungeon.setLocation(result);

        int size = region.getRadius();
        Location lowerLocation = result.clone().subtract(size, size, size);
        Location upperLocation = result.clone().add(size, size, size);

        if (lowerLocation.getY() > upperLocation.getY()) {
            double temp = lowerLocation.getY();
            lowerLocation.setY(upperLocation.getY());
            upperLocation.setY(temp);
        }

        dungeon.setCuboid(new Cuboid(lowerLocation, upperLocation));
        if (Config.OTHER_DEBUG.get()) {
            plugin.sendDebug(dungeon.getId() + ": Location & Cuboid successfully installed");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleDungeonDespawn(AsyncDungeonDespawnEvent event) {
        Dungeon dungeon = event.getDungeon();
        dungeon.setLocation(null);
        dungeon.setCuboid(null);
        if (Config.OTHER_DEBUG.get()) {
            plugin.sendDebug(dungeon.getId() + ": Location & Cuboid successfully removed");
        }
        manager.removeDungeonFromCache(dungeon);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleDungeonChangeStage(AsyncDungeonChangeStageEvent event) {
        Dungeon dungeon = event.getDungeon();
        DungeonStage stage = event.getStage();
        String from = event.getChangeFrom();
        GenerationSettings generationSettings = dungeon.getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();

        if (stage.isDeleting() || stage.isCancelled()) {
            Collection<AbstractModule> modules = dungeon.getModuleManager().getModules();
            boolean anyModuleFailed = modules.stream()
                    .filter(module -> !generationType.isDynamic() && generationSettings.getSpawnLocation().isPresent() && !module.isImportantly())
                    .anyMatch(module -> !module.tryDeactivate(AbstractModule.ActionType.NATURAL));

            if (anyModuleFailed) {
                dungeon.cancel(false);
                return;
            }
        }

        if (Config.OTHER_DEBUG.get()) {
            plugin.sendDebug("Call the dungeon '" + dungeon.getId() + "' from " + from + ". Change stage to " + stage.name() + " from " + dungeon.getStage().name());
        }
        dungeon.setStage(stage);
        dungeon.setSelfTick(0);
    }

    private static void handleAccessDenied(@NotNull Player player, @Nullable String message) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        player.setVelocity(direction.setY(-0.4D).multiply(-0.45D));
        if (message != null && messageCache.add(player)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(message));
        }
    }
}