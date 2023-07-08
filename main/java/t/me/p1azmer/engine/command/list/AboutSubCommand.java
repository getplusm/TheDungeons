package t.me.p1azmer.engine.command.list;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.lang.CoreLang;
import t.me.p1azmer.engine.utils.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AboutSubCommand<P extends NexPlugin<P>> extends AbstractCommand<P> {

    public AboutSubCommand(@NotNull P plugin) {
        super(plugin, new String[]{"about"});
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(CoreLang.CORE_COMMAND_ABOUT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        List<String> info = StringUtil.color(Arrays.asList(
                "&7",
                "&6" + plugin.getName() + " v" + plugin.getDescription().getVersion() + " &7написано &6" + plugin.getDescription().getAuthors() + " &7с любовью",
                "&7Используй &6/" + plugin.getLabel() + " help&7, чтобы посмотреть команды.",
                "&7",
                "&7Координатор &6" + EngineCore.get().getName() + "&7, © 2021-2023"));

        info.forEach(sender::sendMessage);
    }
}