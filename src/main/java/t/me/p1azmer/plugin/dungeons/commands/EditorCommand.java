package t.me.p1azmer.plugin.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class EditorCommand extends AbstractCommand<DungeonPlugin> {

    public EditorCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"editor"}, Perms.COMMAND_EDITOR);
        this.setDescription(plugin.getMessage(Lang.COMMAND_EDITOR_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        this.plugin.getEditor().open((Player) sender, 1);
    }
}