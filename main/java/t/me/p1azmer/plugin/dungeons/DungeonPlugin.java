package t.me.p1azmer.plugin.dungeons;

import com.sk89q.worldedit.WorldEdit;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.api.editor.EditorLocales;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.menu.impl.MenuListener;
import t.me.p1azmer.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.engine.config.CoreConfig;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.hooks.Hooks;
import t.me.p1azmer.engine.hooks.external.VaultHook;
import t.me.p1azmer.engine.lang.CoreLang;
import t.me.p1azmer.engine.utils.MessageUtil;
import t.me.p1azmer.plugin.dungeons.commands.EditorCommand;
import t.me.p1azmer.plugin.dungeons.commands.SpawnCommand;
import t.me.p1azmer.plugin.dungeons.commands.key.KeyCommand;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.editor.EditorMainMenu;
import t.me.p1azmer.plugin.dungeons.key.KeyManager;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.task.AliveTask;
import t.me.p1azmer.plugin.dungeons.utils.SessionConsole;

import java.util.HashSet;
import java.util.Set;

public final class DungeonPlugin extends NexPlugin<DungeonPlugin> {

    // core
    private Set<NexPlugin<?>> childrens = new HashSet<>();

    private EditorManager editorManager;
    private MenuListener menuListener;

    private MessageUtil.MessageItemEvents messageItemEvents;
    // * core


    private DungeonManager dungeonManager;
    private KeyManager keyManager;
    private AliveTask aliveTask;

    private HolographicDisplaysAPI hologramAPI;

    private EditorMainMenu editor;

    private WorldEdit worldEdit;
    private SessionConsole sessionConsole;

    @Override
    protected @NotNull DungeonPlugin getSelf() {
        return this;
    }

    @Override
    public void enable() {
        /*
        core start
         */

        this.editorManager = new EditorManager(this);
        this.editorManager.setup();

        this.menuListener = new MenuListener(this);
        this.menuListener.registerListeners();

        this.messageItemEvents = new MessageUtil.MessageItemEvents(this);
        this.messageItemEvents.registerListeners();
        /*
        core end
         */

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        } else if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            getLogger().severe("*** WorldGuard is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        } else if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            getLogger().severe("*** WorldEdit is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        this.hologramAPI = HolographicDisplaysAPI.get(this);

        this.keyManager = new KeyManager(this);
        this.keyManager.setup();

        this.dungeonManager = new DungeonManager(this);
        this.dungeonManager.setup();

        this.aliveTask = new AliveTask(this);
        this.aliveTask.start();

        this.worldEdit = WorldEdit.getInstance();
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
        if (this.keyManager != null) {
            this.keyManager.shutdown();
            this.keyManager = null;
        }
        if (this.aliveTask != null) {
            this.aliveTask.stop();
            this.aliveTask = null;
        }
        if (this.worldEdit != null) {
            this.worldEdit = null;
        }
                /*
        core start
         */
        if (this.editorManager != null) {
            this.editorManager.shutdown();
            this.editorManager = null;
        }
        if (!MessageUtil.ITEM_MESSAGE_CACHE.isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(player -> MessageUtil.restoreMessageItem(player, true));
        }
        if (this.messageItemEvents != null) {
            this.messageItemEvents.unregisterListeners();
            this.messageItemEvents = null;
        }

        if (this.menuListener != null) {
            this.menuListener.unregisterListeners();
            this.menuListener = null;
        }
        if (Hooks.hasVault()) VaultHook.shutdown();
        /*
        core end
         */
    }

    @Override
    public void loadConfig() {
        CoreConfig.load(this);
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(CoreLang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLangManager().setupEditorEnum(MenuItemType.class);
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEditor(t.me.p1azmer.plugin.dungeons.editor.EditorLocales.class);
        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {
        if (Hooks.hasVault()) {
            VaultHook.setup();
        }
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<DungeonPlugin> mainCommand) {
        mainCommand.addChildren(new EditorCommand(this));
        mainCommand.addChildren(new SpawnCommand(this));
        mainCommand.addChildren(new KeyCommand(this));
        mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    public void addChildren(@NotNull NexPlugin<?> child) {
        this.childrens.add(child);
    }

    @NotNull
    public Set<NexPlugin<?>> getChildrens() {
        return this.childrens;
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public HolographicDisplaysAPI getHologramAPI() {
        return hologramAPI;
    }

    @NotNull
    public EditorMainMenu getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMainMenu(this);
        }
        return this.editor;
    }

    public WorldEdit getWorldEdit() {
        return worldEdit;
    }

    public SessionConsole getSessionConsole() {
        return sessionConsole;
    }
}