package t.me.p1azmer.engine;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.ILogger;
import t.me.p1azmer.engine.api.menu.AbstractMenu;
import t.me.p1azmer.engine.api.menu.impl.Menu;
import t.me.p1azmer.engine.command.CommandManager;
import t.me.p1azmer.engine.command.PluginMainCommand;
import t.me.p1azmer.engine.config.ConfigManager;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.Reflex;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class NexPlugin<P extends NexPlugin<P>> extends JavaPlugin implements ILogger {

    public static boolean isPaper = false;
    protected boolean useCommandManager = true;

    protected ConfigManager<P> configManager;
    protected LangManager<P> langManager;
    protected CommandManager<P> commandManager;

    private Logger logger;
    private boolean isEngine;

    @NotNull
    public static DungeonPlugin getEngine() {
        return DungeonAPI.PLUGIN;
    }

    public final boolean isEngine() {
        return this.isEngine;
    }

    @NotNull
    protected abstract P getSelf();

    @NotNull
    @Override
    public File getFile() {
        return super.getFile();
    }

    @Override
    public final void onEnable() {
        long loadTook = System.currentTimeMillis();
        this.logger = this.getLogger();
        this.isEngine = this instanceof DungeonPlugin;

        DungeonPlugin engine = getEngine();
        if (this.isEngine()) {
            if (!this.getServer().getVersion().contains("Spigot")) {
                isPaper = true;
                this.info("Seems like we have Paper based fork here...");
            }
        } else {
            engine.addChildren(this);
            this.info("Powered by: " + engine.getName());
        }
        /*
        Для коммерческих проектов
         */
        if (Version.CURRENT.isDeprecated()) {
            this.warn("==================================");
            this.warn("WARNING: You're running an outdated/deprecated server version (" + Version.CURRENT.getLocalized() + ")!");
            this.warn("Support for this version will be dropped soon.");
            this.warn("Please, upgrade your server to at least " + CollectionsUtil.next(Version.CURRENT, f -> !f.isDeprecated()).getLocalized() + ".");
            this.warn("==================================");
        }
        this.loadManagers();
        this.error("Спасибо за покупку <3\nt.me/getplusm - разработчик");
        this.info("Plugin loaded in " + (System.currentTimeMillis() - loadTook) + " ms!");
    }

    @Override
    public final void onDisable() {
        this.unloadManagers();
    }

    public abstract void enable();

    public abstract void disable();

    public final void reload() {
        if (this.isEngine()) {
            this.loadConfig();
            this.loadLang();
            return;
        }
        this.unloadManagers();
        this.loadManagers();
    }

    @Override
    public final void reloadConfig() {
        this.getConfig().reload();
        this.loadConfig();
    }

    public final void reloadLang() {
        this.getLang().reload();
        this.loadLang();
    }

    public abstract void loadConfig();

    public abstract void loadLang();

    public abstract void registerHooks();

    public abstract void registerCommands(@NotNull GeneralCommand<P> mainCommand);

    public abstract void registerPermissions();

    public void registerPermissions(@NotNull Class<?> clazz) {
        for (Field field : Reflex.getFields(clazz)) {
            if (!Permission.class.isAssignableFrom(field.getType())) continue;
            if (!field.isAccessible()) continue;

            try {
                Permission permission = (Permission) field.get(null);
                if (this.getPluginManager().getPermission(permission.getName()) == null) {
                    this.getPluginManager().addPermission(permission);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @NotNull
    public final JYML getConfig() {
        return this.getConfigManager().getConfig();
    }

    @NotNull
    public final JYML getLang() {
        return this.getLangManager().getConfig();
    }

    @Override
    public final void info(@NotNull String msg) {
        this.logger.info(msg);
    }

    @Override
    public final void warn(@NotNull String msg) {
        this.logger.warning(msg);
    }

    @Override
    public final void error(@NotNull String msg) {
        this.logger.severe(msg);
    }

    private void unregisterListeners() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            AbstractMenu<?> menu = AbstractMenu.getMenu(player);
            if (menu != null) {
                player.closeInventory();
            }

            Menu<?> menu2 = Menu.getMenu(player);
            if (menu2 != null) {
                player.closeInventory();
            }
        }
        HandlerList.unregisterAll(this);
    }


    protected void loadManagers() {
        // Setup plugin Hooks.
        this.registerHooks();

        // Setup ConfigManager before any other managers.
        this.configManager = new ConfigManager<>(this.getSelf());
        this.configManager.setup();
        this.loadConfig();

        // Setup language manager after the main config.
        this.langManager = new LangManager<>(this.getSelf());
        this.langManager.setup();
        this.loadLang();

        this.registerPermissions();

        // Register plugin commands.
        if (useCommandManager) {
            this.commandManager = new CommandManager<>(this.getSelf());
            this.commandManager.setup();
        } else {
            this.warn("-------------------------------------------------------------");
            this.warn("Plugin not using commandManager! All NexCommands not working!");
            this.warn("-------------------------------------------------------------");
        }

        // Custom plugin loaders.
        this.enable();
    }

    private void unloadManagers() {
        this.getServer().getScheduler().cancelTasks(this); // First stop all plugin tasks

        this.disable();
        if (this.commandManager != null) {
            this.commandManager.shutdown();
        }

        // Unregister all plugin traits and NPC listeners.
//        if (Hooks.hasCitizens()) {
//            CitizensHook.unregisterTraits(this);
//            CitizensHook.unregisterListeners(this);
//        }

        // Unregister ALL plugin listeners.
        this.unregisterListeners();

        this.getConfigManager().shutdown();
        this.getLangManager().shutdown();
    }

    @NotNull
    public final String getLabel() {
        return this.getLabels()[0];
    }

    @NotNull
    public final String[] getLabels() {
        return this.getConfigManager().commandAliases;
    }

    public final PluginMainCommand<P> getMainCommand() {
        return this.getCommandManager().getMainCommand();
    }

    @NotNull
    public final ConfigManager<P> getConfigManager() {
        return this.configManager;
    }

    @NotNull
    public final LangManager<P> getLangManager() {
        return this.langManager;
    }

    @NotNull
    public final LangMessage getMessage(@NotNull LangKey key) {
        return this.getLangManager().getMessage(key);
    }

    @NotNull
    public final CommandManager<P> getCommandManager() {
        return this.commandManager;
    }

    @NotNull
    public final BukkitScheduler getScheduler() {
        return this.getServer().getScheduler();
    }

    @NotNull
    public final PluginManager getPluginManager() {
        return this.getServer().getPluginManager();
    }

    public ClassLoader getClazzLoader() {
        return this.getClassLoader();
    }

    @Deprecated
    public final void runTask(@NotNull Consumer<BukkitRunnable> consume, boolean async) {
        if (async) {
            this.getServer().getScheduler().runTaskAsynchronously(this, new BukkitRunnable() {
                @Override
                public void run() {
                    consume.accept(this);
                }
            });
        } else {
            this.getServer().getScheduler().runTask(this, new BukkitRunnable() {
                @Override
                public void run() {
                    consume.accept(this);
                }
            });
        }
    }

    public void runTask(@NotNull Consumer<BukkitRunnable> consumer) {
        this.getScheduler().runTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        });
    }

    @Deprecated
    public void runTaskAsynchronously(@NotNull Consumer<BukkitRunnable> consumer) {
        this.runTaskAsync(consumer);
    }

    public void runTaskAsync(@NotNull Consumer<BukkitRunnable> consumer) {
        this.getScheduler().runTaskAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        });
    }

    public void runTaskLater(@NotNull Consumer<BukkitRunnable> consumer, long delay) {
        this.getScheduler().runTaskLater(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        }, delay);
    }

    public void runTaskLaterAsync(@NotNull Consumer<BukkitRunnable> consumer, long delay) {
        this.getScheduler().runTaskLaterAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        }, delay);
    }

    public void runTaskTimer(@NotNull Consumer<BukkitRunnable> consumer, long delay, long interval) {
        this.getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        }, delay, interval);
    }

    public void runTaskTimerAsync(@NotNull Consumer<BukkitRunnable> consumer, long delay, long interval) {
        this.getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        }, delay, interval);
    }

    public boolean isUseCommandManager() {
        return useCommandManager;
    }

    public void disableCommandManager() {
        this.useCommandManager = false;
        if (this.commandManager != null) {
            this.commandManager.shutdown();
            this.commandManager = null;
        }
    }
}