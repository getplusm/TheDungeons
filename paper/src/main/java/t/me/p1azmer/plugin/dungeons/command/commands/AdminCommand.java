package t.me.p1azmer.plugin.dungeons.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.command.CommandPermissionPredicate;
import t.me.p1azmer.plugin.dungeons.config.ConfigContainer;
import t.me.p1azmer.plugin.dungeons.config.configs.CommandConfig;
import t.me.p1azmer.plugin.dungeons.config.configs.Lang;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCommand {

    ConfigContainer configContainer;
    Command.Builder<CommandSender> builder;

    public AdminCommand(CommandManager<CommandSender> manager, ConfigContainer configContainer) {
        this.configContainer = configContainer;

        CommandConfig commandConfig = configContainer.getCommandConfig();
        CommandConfig.Command command = commandConfig.getAdminCommand();
        this.builder = manager.commandBuilder(command.getName(), command.getAliases().toArray(new String[0]))
                .permission(CommandPermissionPredicate.getPredicate(command));

        manager.command(reloadCommand());
    }

    private Command.Builder<CommandSender> reloadCommand() {
        return builder.literal("reload").handler(ctx -> {
            CommandSender sender = ctx.getSender();
            Lang lang = configContainer.getLang();

            reload();
            sender.sendMessage(lang.getCommandsMessages().getSuccessReloaded());
        });
    }

    private void reload() {
        configContainer.reload();
        configContainer.getDungeonsConfig().getDungeons().values().forEach(Dungeon::reload);
    }
}
