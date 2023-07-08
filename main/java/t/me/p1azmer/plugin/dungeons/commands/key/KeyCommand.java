package t.me.p1azmer.plugin.dungeons.commands.key;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.command.list.HelpSubCommand;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class KeyCommand extends GeneralCommand<DungeonPlugin> {

    public KeyCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"key"}, Perms.COMMAND_KEY);
        this.setDescription(plugin.getMessage(Lang.COMMAND_KEY_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_KEY_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new GiveCommand(plugin));
        this.addChildren(new GiveAllCommand(plugin));
        this.addChildren(new TakeCommand(plugin));
        this.addChildren(new SetCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}