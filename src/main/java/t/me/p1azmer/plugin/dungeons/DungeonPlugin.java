package t.me.p1azmer.plugin.dungeons;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.plugin.dungeons.announce.AnnounceManager;
import t.me.p1azmer.plugin.dungeons.api.handler.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.commands.EditorCommand;
import t.me.p1azmer.plugin.dungeons.commands.dungeon.DebugCommand;
import t.me.p1azmer.plugin.dungeons.commands.dungeon.DeleteCommand;
import t.me.p1azmer.plugin.dungeons.commands.dungeon.SpawnCommand;
import t.me.p1azmer.plugin.dungeons.commands.key.KeyCommand;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageLang;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.editor.EditorMainMenu;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.integration.access.AccessHandlerPSAPI;
import t.me.p1azmer.plugin.dungeons.integration.holograms.FancyHologramsHandler;
import t.me.p1azmer.plugin.dungeons.integration.holograms.HologramDecentHandler;
import t.me.p1azmer.plugin.dungeons.integration.holograms.HologramDisplaysHandler;
import t.me.p1azmer.plugin.dungeons.integration.party.PartyHandlerPaF;
import t.me.p1azmer.plugin.dungeons.integration.party.PartyHandlerParties;
import t.me.p1azmer.plugin.dungeons.integration.region.*;
import t.me.p1azmer.plugin.dungeons.integration.schematics.SchematicFAWEHandler;
import t.me.p1azmer.plugin.dungeons.key.KeyManager;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;
import t.me.p1azmer.plugin.dungeons.mob.style.MobStyleType;
import t.me.p1azmer.plugin.dungeons.placeholders.DungeonPlaceholder;
import t.me.p1azmer.plugin.dungeons.scheduler.ThreadSync;
import t.me.p1azmer.plugin.dungeons.utils.SessionConsole;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class DungeonPlugin extends NexPlugin<DungeonPlugin> {
    @Getter
    static Logger log;
    DungeonManager dungeonManager;
    KeyManager keyManager;
    MobManager mobManager;
    AnnounceManager announceManager;

    EditorMainMenu editor;

    HologramHandler hologramHandler;
    SchematicHandler schematicHandler;
    RegionHandler regionHandler;
    PartyHandler partyHandler;
    AccessHandler accessHandler;
    DungeonPlaceholder placeholder;

    ThreadSync threadSync;
    LocationGenerator locationGenerator;

    @Override
    protected @NotNull DungeonPlugin getSelf() {
        return this;
    }

    @Override
    public void enable() {
        log = getLogger();
        if (schematicHandler == null) {
            log.severe("FAWE or WorldEdit not found! Please install them to use this plugin!");
            getPluginManager().disablePlugin(this);
            return;
        }
        this.locationGenerator = new LocationGenerator(regionHandler);
        this.threadSync = new ThreadSync(this);

        this.keyManager = new KeyManager(this);
        this.keyManager.setup();

        this.mobManager = new MobManager(this, threadSync);
        this.mobManager.setup();

        this.announceManager = new AnnounceManager(this);
        this.announceManager.setup();

        this.dungeonManager = new DungeonManager(this, locationGenerator, threadSync);
        this.dungeonManager.setup();
    }

    @Override
    public void disable() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.dungeonManager != null) {
            this.dungeonManager.shutdown();
            this.dungeonManager = null;
        }
        if (this.mobManager != null) {
            this.mobManager.shutdown();
            this.mobManager = null;
        }
        if (this.keyManager != null) {
            this.keyManager.shutdown();
            this.keyManager = null;
        }
        if (this.announceManager != null) {
            this.announceManager.shutdown();
            this.announceManager = null;
        }
        if (this.hologramHandler != null) {
            this.hologramHandler.shutdown();
            this.hologramHandler = null;
        }
        if (this.schematicHandler != null) {
            this.schematicHandler.shutdown();
            this.schematicHandler = null;
        }
        if (this.regionHandler != null) {
            this.regionHandler.shutdown();
            this.regionHandler = null;
        }
        if (this.partyHandler != null) {
            this.partyHandler.shutdown();
            this.partyHandler = null;
        }
        if (this.placeholder != null) {
            this.placeholder.shutdown();
            this.placeholder = null;
        }
        locationGenerator.shutdownExecutor();
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadMissing(StageLang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLangManager().loadEnum(MobStyleType.class);
        this.getLangManager().loadEnum(DungeonStage.class);
        this.getLangManager().loadEnum(ChestState.class);
        this.getLangManager().loadEnum(GenerationType.class);
        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {
        try {
            initialIntegrations();
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Got an exception while registering integrations: ", exception);
        }
    }

    private void initialIntegrations() {
        if (EngineUtils.hasPlugin("HolographicDisplays")) {
            this.hologramHandler = new HologramDisplaysHandler(this);
            this.hologramHandler.setup();
            this.warn("Using HD for hologram handler");
        } else if (EngineUtils.hasPlugin("DecentHolograms")) {
            this.hologramHandler = new HologramDecentHandler();
            this.hologramHandler.setup();
            this.warn("Using DecentHolograms for hologram handler");
        } else if (EngineUtils.hasPlugin("FancyHolograms")) {
            this.hologramHandler = new FancyHologramsHandler();
            this.hologramHandler.setup();
            this.warn("Using FancyHolograms for hologram handler");
        }
        if (EngineUtils.hasPlugin("WorldGuard")) {
            this.regionHandler = new RegionHandlerWG();
            this.regionHandler.setup();
            this.warn("Using WorldGuard for region handler");
        } else if (EngineUtils.hasPlugin("GriefPrevention")) {
            this.regionHandler = new RegionHandlerGP(this);
            this.regionHandler.setup();
            this.warn("Using GriefPrevention for region handler");
        } else if (EngineUtils.hasPlugin("GriefDefender")) {
            this.regionHandler = new RegionHandlerGD(this);
            this.regionHandler.setup();
            this.warn("Using GriefDefender for region handler");
        } else if (EngineUtils.hasPlugin("KingdomsX")) {
            this.regionHandler = new RegionHandlerKingdoms();
            this.regionHandler.setup();
            this.warn("Using KingdomsX for region handler");
        } else if (EngineUtils.hasPlugin("Towny")) {
            this.regionHandler = new RegionHandlerTowny();
            this.regionHandler.setup();
            this.warn("Using Towny for region handler");
        } else if (EngineUtils.hasPlugin("ProtectionBlocks")) {
//            this.regionHandler = new RegionHandlerPB(this);
//            this.regionHandler.setup();
//            this.warn("Using ProtectionBlocks for region handler");
            this.warn("ProtectionBlocks plugin is not supported");
        }
        if (EngineUtils.hasPlugin("PartyAndFriends")) {
            this.partyHandler = new PartyHandlerPaF();
            this.partyHandler.setup();
            this.warn("Using PartyAndFriends for party handler");
        }
        if (EngineUtils.hasPlugin("Parties")) {
            this.partyHandler = new PartyHandlerParties();
            this.partyHandler.setup();
            this.warn("Using Parties for party handler");
        }
        if (EngineUtils.hasPlugin("Fabled")) {
            this.accessHandler = new AccessHandlerPSAPI();
            this.accessHandler.setup();
            this.warn("Using Fabled (ProSkillAPI) for access handler");
        }
        if (EngineUtils.hasPlugin("WorldEdit") || EngineUtils.hasPlugin("FastAsyncWorldEdit")) {
            this.schematicHandler = new SchematicFAWEHandler(new SessionConsole(this), new ThreadSync(this));
            this.schematicHandler.setup();
            this.warn("Using FAWE/WorldEdit for schematic handler!");
        }
        if (EngineUtils.hasPlaceholderAPI()) {
            this.placeholder = new DungeonPlaceholder(this);
            this.placeholder.setup();
        }
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<DungeonPlugin> mainCommand) {
        mainCommand.addChildren(new EditorCommand(this));
        mainCommand.addChildren(new SpawnCommand(this));
        mainCommand.addChildren(new DeleteCommand(this));
        mainCommand.addChildren(new KeyCommand(this));
        mainCommand.addChildren(new DebugCommand(this));
        mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @NotNull
    public EditorMainMenu getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMainMenu(this);
        }
        return this.editor;
    }

    public void sendDebug(@NotNull String text) {
        this.debug(text);
    }
}