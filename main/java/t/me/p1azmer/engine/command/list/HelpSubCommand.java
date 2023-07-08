package t.me.p1azmer.engine.command.list;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.lang.CoreLang;

import java.util.Map;


public class HelpSubCommand<P extends NexPlugin<P>> extends AbstractCommand<P> {

    public HelpSubCommand(@NotNull P plugin) {
        super(plugin, new String[]{"help"});
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(CoreLang.CORE_COMMAND_HELP_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (!this.parent.hasPermission(sender)) {
            this.errorPermission(sender);
            return;
        }

        for (String line : plugin.getMessage(CoreLang.CORE_COMMAND_HELP_LIST).asList()) {
            if (line.contains(AbstractCommand.PLACEHOLDER_LABEL)) {
                for (AbstractCommand<P> cmd : this.parent.getChildrens()) {
                    if (!cmd.hasPermission(sender)) continue;

                    sender.sendMessage(cmd.replacePlaceholders().apply(line));
                }
                continue;
            }
            sender.sendMessage(line);
        }
    }
}