package t.me.p1azmer.plugin.dungeons.commands.dungeon;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.engine.utils.Constants;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.SpawnModule;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends AbstractCommand<DungeonPlugin> {

    public DeleteCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"despawn", "remove", "del", "delete"}, Perms.COMMAND_DESPAWN);
        this.setDescription(plugin.getMessage(Lang.COMMAND_DEL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_DEL_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            List<String> list = new ArrayList<>(List.of(Constants.MASK_ANY));
            list.addAll(plugin.getDungeonManager().getDungeonIds(false));
            return list;
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 1) {
            this.printUsage(sender);
            return;
        }
        if (result.getArg(1).equalsIgnoreCase(Constants.MASK_ANY)) {
            plugin.getDungeonManager().getDungeons().stream().filter(dungeon -> dungeon.getModuleManager().getModule(SpawnModule.class).isPresent() && dungeon.getModuleManager().getModule(SpawnModule.class).get().isSpawned()).forEach(f -> f.cancel(false));
            plugin.getMessage(Lang.COMMAND_DEL_DONE).replace(Placeholders.DUNGEON_NAME, Constants.MASK_ANY).send(sender);
            return;
        }

        Dungeon dungeon = plugin.getDungeonManager().getDungeonById(result.getArg(1));
        if (dungeon == null) {
            plugin.getMessage(Lang.DUNGEON_ERROR_INVALID).send(sender);
            return;
        }
        plugin.getMessage(Lang.COMMAND_DEL_DONE).replace(dungeon.replacePlaceholders()).send(sender);
    }
}