package t.me.p1azmer.engine.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.lang.CoreLang;

import java.util.Map;

public class ReloadSubCommand<P extends NexPlugin<P>> extends AbstractCommand<P> {

    public ReloadSubCommand(@NotNull P plugin, @NotNull Permission permission) {
        this(plugin, permission.getName());
    }

    public ReloadSubCommand(@NotNull P plugin, @NotNull String permission) {
        super(plugin, new String[]{"reload"}, permission);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(CoreLang.CORE_COMMAND_RELOAD_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        plugin.reload();
        plugin.getMessage(CoreLang.CORE_COMMAND_RELOAD_DONE).send(sender);
    }
}