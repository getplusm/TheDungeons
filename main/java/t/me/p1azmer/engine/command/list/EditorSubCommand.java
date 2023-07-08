package t.me.p1azmer.engine.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.editor.EditorHolder;
import t.me.p1azmer.engine.lang.CoreLang;

import java.util.Map;

public class EditorSubCommand<P extends NexPlugin<P>> extends AbstractCommand<P> {

    protected final EditorHolder<P, ?> editorHolder;

    public EditorSubCommand(@NotNull P plugin, @NotNull EditorHolder<P, ?> editorHolder, @NotNull Permission permission) {
        this(plugin, editorHolder, permission.getName());
    }

    public EditorSubCommand(@NotNull P plugin, @NotNull EditorHolder<P, ?> editorHolder, @NotNull String permission) {
        super(plugin, new String[]{"editor"}, permission);
        this.editorHolder = editorHolder;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(CoreLang.CORE_COMMAND_EDITOR_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        this.editorHolder.getEditor().open(player, 1);
    }
}