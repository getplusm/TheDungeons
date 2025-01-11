package t.me.p1azmer.plugin.dungeons.commands.dungeon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.utils.debug.PastebinUtil;

import java.util.concurrent.Executors;

public class DebugCommand extends AbstractCommand<DungeonPlugin> {

    public DebugCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"debug"}, Perms.COMMAND_RELOAD);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        sender.sendMessage("Prepare pastebin link..");
        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            String pasteUrl = PastebinUtil.pasteAsync();
            String discordUrl = "https://discord.gg/ajnPb3fdKq";
            sender.sendMessage(Component.text("Drop this link: ", NamedTextColor.GREEN)
                    .append(Component.text("[PASTEBIN]", NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pasteUrl))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GOLD)))
                    )
                    .append(Component.newline())
                    .append(Component.text("To our discord: ", NamedTextColor.GREEN))
                    .append(Component.text("[DISCORD URL]", NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, discordUrl))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to open", NamedTextColor.GOLD))))
            );
        });
    }
}