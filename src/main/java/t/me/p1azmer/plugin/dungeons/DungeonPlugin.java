package t.me.p1azmer.plugin.dungeons;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.plugin.dungeons.announce.AnnounceManager;
import t.me.p1azmer.plugin.dungeons.api.handler.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.handler.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.commands.EditorCommand;
import t.me.p1azmer.plugin.dungeons.commands.dungeon.DeleteCommand;
import t.me.p1azmer.plugin.dungeons.commands.dungeon.SpawnCommand;
import t.me.p1azmer.plugin.dungeons.commands.key.KeyCommand;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.state.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageLang;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.editor.EditorMainMenu;
import t.me.p1azmer.plugin.dungeons.integration.holograms.HologramDecentHandler;
import t.me.p1azmer.plugin.dungeons.integration.holograms.HologramDisplaysHandler;
import t.me.p1azmer.plugin.dungeons.integration.party.PartyHandlerPaF;
import t.me.p1azmer.plugin.dungeons.integration.region.*;
import t.me.p1azmer.plugin.dungeons.integration.schematics.SchematicFAWEHandler;
import t.me.p1azmer.plugin.dungeons.key.KeyManager;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;
import t.me.p1azmer.plugin.dungeons.mob.style.MobStyleType;
import t.me.p1azmer.plugin.dungeons.placeholders.DungeonPlaceholder;
import t.me.p1azmer.plugin.dungeons.utils.SessionConsole;

@Getter
public final class DungeonPlugin extends NexPlugin<DungeonPlugin> {
    private DungeonManager dungeonManager;
    private KeyManager keyManager;
    private MobManager mobManager;
    private AnnounceManager announceManager;

    private EditorMainMenu editor;
    private SessionConsole sessionConsole;

    private HologramHandler hologramHandler;
    private SchematicHandler schematicHandler;
    private RegionHandler regionHandler;
    private PartyHandler partyHandler;
    private DungeonPlaceholder placeholder;

    @Override
    protected @NotNull DungeonPlugin getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.keyManager = new KeyManager(this);
        this.keyManager.setup();

        this.mobManager = new MobManager(this);
        this.mobManager.setup();

        this.announceManager = new AnnounceManager(this);
        this.announceManager.setup();

        this.dungeonManager = new DungeonManager(this);
        this.dungeonManager.setup();

        if (this.schematicHandler != null)
            this.sessionConsole = new SessionConsole(this);
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
        if (this.announceManager != null){
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
        initialIntegrations();
    }

    private void initialIntegrations() {
        if (EngineUtils.hasPlugin("HolographicDisplays")) {
            this.hologramHandler = new HologramDisplaysHandler(this);
            this.hologramHandler.setup();
            this.warn("Use HD for hologram handler");
        } else if (EngineUtils.hasPlugin("DecentHolograms")) {
            this.hologramHandler = new HologramDecentHandler();
            this.hologramHandler.setup();
            this.warn("Use DecentHolograms for hologram handler");
        }
        if (EngineUtils.hasPlugin("WorldGuard")) {
            this.regionHandler = new RegionHandlerWG();
            this.regionHandler.setup();
            this.warn("Use WorldGuard for region handler");
        } else if (EngineUtils.hasPlugin("GriefPrevention")) {
            this.regionHandler = new RegionHandlerGP(this);
            this.regionHandler.setup();
            this.warn("Use GriefPrevention for region handler");
        } else if (EngineUtils.hasPlugin("GriefDefender")) {
            this.regionHandler = new RegionHandlerGD(this);
            this.regionHandler.setup();
            this.warn("Use GriefDefender for region handler");
        } else if (EngineUtils.hasPlugin("KingdomsX")) {
            this.regionHandler = new RegionHandlerKingdoms(this);
            this.regionHandler.setup();
            this.warn("Use KingdomsX for region handler");
        } else if (EngineUtils.hasPlugin("Towny")) {
            this.regionHandler = new RegionHandlerTowny(this);
            this.regionHandler.setup();
            this.warn("Use Towny for region handler");
        } else if (EngineUtils.hasPlugin("ProtectionBlocks")) {
            this.regionHandler = new RegionHandlerPB(this);
            this.regionHandler.setup();
            this.warn("Use ProtectionBlocks for region handler");
        }
        if (EngineUtils.hasPlugin("PartyAndFriends")) {
            this.partyHandler = new PartyHandlerPaF();
            this.partyHandler.setup();
            this.warn("Use PartyAndFriends for party handler");
        }
        if (EngineUtils.hasPlugin("WorldEdit") || EngineUtils.hasPlugin("FastAsyncWorldEdit")) {
            this.schematicHandler = new SchematicFAWEHandler(this);
            this.schematicHandler.setup();
            this.warn("Use " + (EngineUtils.hasPlugin("WorldEdit") ? "WorldEdit" : "FAWE") + " for schematic handler!");
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
        if (Config.DEBUG.get()) this.debug(text);
    }
}