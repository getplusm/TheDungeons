package t.me.p1azmer.plugin.dungeons.commands.key;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.commands.CommandFlags;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;
import t.me.p1azmer.plugin.dungeons.key.Key;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class GiveAllCommand extends AbstractCommand<DungeonPlugin> {

  public GiveAllCommand(@NotNull DungeonPlugin plugin) {
    super(plugin, new String[]{"giveall"}, Perms.COMMAND_KEY_GIVE);
    this.setDescription(plugin.getMessage(Lang.COMMAND_KEY_GIVE_ALL_DESC));
    this.setUsage(plugin.getMessage(Lang.COMMAND_KEY_GIVE_ALL_USAGE));
    this.addFlag(CommandFlags.SILENT);
  }

  @Override
  @NotNull
  public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
    if (arg == 2) {
      return plugin.getKeyManager().getKeyIds();
    }
    if (arg == 3) {
      return Arrays.asList("1", "5", "10");
    }
    return super.getTab(player, arg, args);
  }

  @Override
  protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
    if (result.length() < 4) {
      this.printUsage(sender);
      return;
    }

    Key key = plugin.getKeyManager().getKeyById(result.getArg(2));
    if (key == null) {
      plugin.getMessage(Lang.DUNGEON_KEY_ERROR_INVALID).send(sender);
      return;
    }

    int amount = Math.abs(result.getInt(3, 1));
    if (amount <= 0) return;

    Collection<? extends Player> users = this.plugin.getServer().getOnlinePlayers();

    users.forEach(user -> {
      this.plugin.getKeyManager().giveKey(user, key, amount);

      Player target = user.getPlayer();
      if (target != null && !result.hasFlag(CommandFlags.SILENT)) {
        this.plugin.getMessage(Lang.COMMAND_KEY_GIVE_NOTIFY)
                   .replace(Placeholders.GENERIC_AMOUNT, amount)
                   .replace(key.replacePlaceholders())
                   .send(sender);
      }
    });

    this.plugin.getMessage(Lang.COMMAND_KEY_GIVE_ALL_DONE)
               .replace(Placeholders.GENERIC_AMOUNT, amount)
               .replace(key.replacePlaceholders())
               .send(sender);
  }
}