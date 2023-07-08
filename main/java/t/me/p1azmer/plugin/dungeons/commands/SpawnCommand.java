package t.me.p1azmer.plugin.dungeons.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Perms;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Arrays;
import java.util.List;

public class SpawnCommand extends AbstractCommand<DungeonPlugin> {

    public SpawnCommand(@NotNull DungeonPlugin plugin) {
        super(plugin, new String[]{"spawn", "summon", "drop"}, Perms.COMMAND_DROP);
        this.setDescription(plugin.getMessage(Lang.COMMAND_DROP_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_DROP_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getDungeonManager().getDungeonIds(false);
        }
        if (arg == 2) {
            return CollectionsUtil.worldNames();
        }
        if (arg == 3) {
            return Arrays.asList("<x>", NumberUtil.format(player.getLocation().getX()));
        }
        if (arg == 4) {
            return Arrays.asList("<y>", NumberUtil.format(player.getLocation().getY()));
        }
        if (arg == 5) {
            return Arrays.asList("<z>", NumberUtil.format(player.getLocation().getZ()));
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() != 6) {
            this.printUsage(sender);
            return;
        }

        Dungeon dungeon = plugin.getDungeonManager().getDungeonByKey(result.getArg(1));
        if (dungeon == null) {
            plugin.getMessage(Lang.DUNGEON_ERROR_INVALID).send(sender);
            return;
        }

        World world = plugin.getServer().getWorld(result.getArg(2));
        if (world == null) {
            plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }

        double x = result.getDouble(3, 0);
        double y = result.getDouble(4, 0);
        double z = result.getDouble(5, 0);
        Location location = new Location(world, x, y, z);

        if (!plugin.getDungeonManager().spawnDungeon(dungeon, location)) {
            return;
        }

        plugin.getMessage(Lang.COMMAND_DROP_DONE)
                .replace(dungeon.replacePlaceholders())
                .replace(Placeholders.LOCATION.replacer(location))
                .send(sender);
    }
}