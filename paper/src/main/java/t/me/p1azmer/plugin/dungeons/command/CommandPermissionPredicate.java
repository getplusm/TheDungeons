package t.me.p1azmer.plugin.dungeons.command;

import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.permission.PredicatePermission;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.config.configs.CommandConfig;
import t.me.p1azmer.plugin.dungeons.core.common.groups.GroupInfo;

import java.util.UUID;

@UtilityClass
public class CommandPermissionPredicate {
    public @NotNull PredicatePermission<CommandSender> getPredicate(@NotNull CommandConfig.Command command) {
        CloudKey<Void> cloudKey = SimpleCloudKey.of(String.join(",", command.getGroup().getGroups()));

        return PredicatePermission.of(cloudKey, sender -> {
            if (!(sender instanceof Player player) || (!command.isPermissionCheck() && !command.isGroupCheck())) {
                return true;
            }
            if (command.isPermissionCheck() && !player.hasPermission(command.getPermissionAccess())) {
                return false;
            }

            return command.isGroupCheck() && (isAdmin(command.getGroup(), player) || player.hasPermission(command.getPermissionAccess()));
        });
    }

    private static boolean isAdmin(@NotNull GroupInfo groupInfo, @NotNull Player player) {
        UUID playerUuid = player.getUniqueId();
        return groupInfo.checkGroupSync(playerUuid);
    }
}
