package t.me.p1azmer.plugin.dungeons.commands.key;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.key.Key;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

class TakeCommand extends ManageCommand {

    public TakeCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"take"}, Perms.COMMAND_KEY_TAKE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_KEY_TAKE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_KEY_TAKE_USAGE));
        this.setMessageNotify(plugin.getMessage(Lang.COMMAND_KEY_TAKE_NOTIFY));
        this.setMessageDone(plugin.getMessage(Lang.COMMAND_KEY_TAKE_DONE));
    }

    @Override
    protected void manage(@NotNull Player user, @NotNull Key key, int amount) {
        plugin.getKeyManager().takeKey(user, key, amount);
    }
}