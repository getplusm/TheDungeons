package t.me.p1azmer.plugin.dungeons;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.cjcrafter.foliascheduler.FoliaCompatibility;
import com.cjcrafter.foliascheduler.ServerImplementation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.integrations.IntegrationFactory;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.container.SettingsContainer;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.types.GenerationType;
import t.me.p1azmer.plugin.dungeons.api.models.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.api.models.module.container.ModuleContainer;
import t.me.p1azmer.plugin.dungeons.command.commands.AdminCommand;
import t.me.p1azmer.plugin.dungeons.config.ConfigContainer;
import t.me.p1azmer.plugin.dungeons.config.configs.DungeonsConfig;
import t.me.p1azmer.plugin.dungeons.config.configs.Lang;
import t.me.p1azmer.plugin.dungeons.models.generations.GroundGenerator;
import t.me.p1azmer.plugin.dungeons.models.generations.UndergroundGenerator;
import t.me.p1azmer.plugin.dungeons.models.integrations.IntegrationFactoryImpl;
import t.me.p1azmer.plugin.dungeons.modules.container.ModuleContainerImpl;
import t.me.p1azmer.plugin.dungeons.schedulers.DungeonTickScheduler;
import t.me.p1azmer.plugin.dungeons.settings.container.SettingsContainerImpl;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DungeonPlugin extends JavaPlugin {

    @Getter static Logger log;

    ServerImplementation syncScheduler;
    DungeonTickScheduler dungeonTickScheduler;
    Map<GenerationType, LocationGenerator> locationGenerators;

    @Override
    public void onEnable() {
        log = getLogger();

        try {
            ConfigContainer configContainer = new ConfigContainer(getDataFolder());
            SettingsContainer settingsContainer = new SettingsContainerImpl(getDataFolder());
            syncScheduler = new FoliaCompatibility(this).getServerImplementation();
            IntegrationFactoryImpl integrationFactory = new IntegrationFactoryImpl(this, syncScheduler);

            registerLocationGenerators(configContainer, integrationFactory);
            initializeAllDungeons(configContainer, settingsContainer, integrationFactory);
            registerCommands(configContainer);
        } catch (Exception exception) {
            log.log(Level.SEVERE, "Got exception while initializing plugin", exception);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeAllDungeons(ConfigContainer configContainer, SettingsContainer settingsContainer,
                                       IntegrationFactory integrationFactory) {
        DungeonsConfig dungeonsConfig = configContainer.getDungeonsConfig();
        for (Dungeon dungeon : dungeonsConfig.getDungeons().values()) {
            LocationGenerator locationGenerator = locationGenerators.get(dungeon.getGenerationType());

            dungeon.setSettingsContainer(settingsContainer);
            dungeon.setDataFolder(getDataFolder());

            ModuleContainer moduleContainer = new ModuleContainerImpl(dungeon, locationGenerator, integrationFactory);
            dungeon.setModuleContainer(moduleContainer);
        }
        Logger.getGlobal().severe("loaded " + dungeonsConfig.getDungeons().size() + " dungeons");
        dungeonTickScheduler = new DungeonTickScheduler(syncScheduler, configContainer);
    }

    private void registerLocationGenerators(ConfigContainer configContainer, IntegrationFactory integrationFactory) {
        RegionHandler regionHandler = integrationFactory.getRegionHandler();
        locationGenerators = Map.of(
                GenerationType.GROUND, new GroundGenerator(configContainer, regionHandler),
                GenerationType.UNDERGROUND, new UndergroundGenerator(configContainer, regionHandler)
        );
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        dungeonTickScheduler.shutdown();
        syncScheduler.cancelTasks();
        locationGenerators.forEach((generationType, locationGenerator) -> {
            if (generationType.equals(GenerationType.UNDERGROUND)) {
                ((UndergroundGenerator) locationGenerator).shutdown();
            } else if (generationType.equals(GenerationType.GROUND)) {
                ((GroundGenerator) locationGenerator).shutdown();
            }
        });
    }

    private void registerCommands(ConfigContainer configContainer) {
        try {
            CommandManager<CommandSender> manager = setupCommandManager(configContainer);
            new AdminCommand(manager, configContainer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private @NotNull CommandManager<CommandSender> setupCommandManager(@NotNull ConfigContainer configContainer) throws Exception {
        CommandManager<CommandSender> manager = new PaperCommandManager<>(
                this, CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        Lang lang = configContainer.getLang();
        Lang.CommandsMessages commandsMessages = lang.getCommandsMessages();
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (sender, exception) ->
                        commandsMessages.getNoPermissionMessage())
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (sender, exception) ->
                        commandsMessages.getInvalidSyntaxMessage())
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, (sender, exception) ->
                        commandsMessages.getInvalidSenderMessage())
                .apply(manager, s -> s);
        return manager;
    }
}
