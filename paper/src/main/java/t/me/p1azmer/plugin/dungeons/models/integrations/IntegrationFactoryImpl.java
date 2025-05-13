package t.me.p1azmer.plugin.dungeons.models.integrations;

import com.cjcrafter.foliascheduler.ServerImplementation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.integrations.IntegrationFactory;
import t.me.p1azmer.plugin.dungeons.api.integrations.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.integration.impl.access.AccessProSkill;
import t.me.p1azmer.plugin.dungeons.integration.impl.schematic.SchematicFAWEHandler;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IntegrationFactoryImpl implements IntegrationFactory {

    @NonFinal AccessHandler accessHandler;
    @NonFinal HologramHandler hologramHandler;
    @NonFinal PartyHandler partyHandler;
    @NonFinal RegionHandler regionHandler;
    @NonFinal SchematicHandler schematicHandler;

    public IntegrationFactoryImpl(JavaPlugin plugin, ServerImplementation syncThread) {
        registerAccessHandlers();
        registerHologramHandlers();
        registerPartyHandlers();
        registerRegionHandlers();
        registerSchematicHandlers(plugin, syncThread);
    }

    private void registerAccessHandlers() {
        if (hasPlugin("Fabled")) {
            accessHandler = new AccessProSkill();
            DungeonPlugin.getLog().info("Using Fabled (ProSkillAPI) for access handler");
        }
    }

    private void registerHologramHandlers() {
//        if (hasPlugin("HolographicDisplays")) {
//            this.hologramHandler = new HologramDisplaysHandler(this);
//            this.hologramHandler.setup();
//            DungeonPlugin.getLog().info("Using HD for hologram handler");
//        } else if (hasPlugin("DecentHolograms")) {
//            this.hologramHandler = new HologramDecentHandler();
//            this.hologramHandler.setup();
//            DungeonPlugin.getLog().info("Using DecentHolograms for hologram handler");
//        } else if (hasPlugin("FancyHolograms")) {
//            this.hologramHandler = new FancyHologramsHandler();
//            this.hologramHandler.setup();
//            DungeonPlugin.getLog().info("Using FancyHolograms for hologram handler");
//        }
    }

    private void registerPartyHandlers() {
//        if (hasPlugin("PartyAndFriends")) {
//            this.partyHandler = new PartyHandlerPaF();
//            this.partyHandler.setup();
//            DungeonPlugin.getLog().info("Using PartyAndFriends for party handler");
//        }
//        if (hasPlugin("Parties")) {
//            this.partyHandler = new PartyHandlerParties();
//            this.partyHandler.setup();
//            DungeonPlugin.getLog().info("Using Parties for party handler");
//        }
    }

    private void registerRegionHandlers() {
//        if (hasPlugin("WorldGuard")) {
//            this.regionHandler = new RegionHandlerWG();
//            this.regionHandler.setup();
//            DungeonPlugin.getLog().info("Using WorldGuard for region handler");
//        } else if (hasPlugin("GriefPrevention")) {
//            this.regionHandler = new RegionHandlerGP(this);
//            this.regionHandler.setup();
//            DungeonPlugin.getLog().info("Using GriefPrevention for region handler");
//        } else if (hasPlugin("GriefDefender")) {
//            this.regionHandler = new RegionHandlerGD(this);
//            this.regionHandler.setup();
//            DungeonPlugin.getLog().info("Using GriefDefender for region handler");
//        } else if (hasPlugin("KingdomsX")) {
//            this.regionHandler = new RegionHandlerKingdoms();
//            this.regionHandler.setup();
//            DungeonPlugin.getLog().info("Using KingdomsX for region handler");
//        } else if (hasPlugin("Towny")) {
//            this.regionHandler = new RegionHandlerTowny();
//            this.regionHandler.setup();
//            DungeonPlugin.getLog().info("Using Towny for region handler");
//        }
    }

    private void registerSchematicHandlers(JavaPlugin plugin, ServerImplementation syncThread) {
        if (hasPlugin("WorldEdit") || hasPlugin("FastAsyncWorldEdit")) {
            this.schematicHandler = new SchematicFAWEHandler(plugin, syncThread);
            DungeonPlugin.getLog().info("Using FAWE/WorldEdit for schematic handler!");
        }
    }

    public void shutdown() {
        if (schematicHandler != null) {
            schematicHandler.shutdown();
        }
    }

    private static boolean hasPlugin(String name) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        return pluginManager.getPlugin(name) != null;
    }
}
