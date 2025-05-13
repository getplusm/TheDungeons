package t.me.p1azmer.plugin.dungeons.core.common.groups;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupInfo {
    @Setting(nodeFromParent = true)
    @NotNull List<String> groups;

    public CompletableFuture<Boolean> checkGroupAsync(@NotNull UUID playerUuid) {
        return getOrLoad(playerUuid).thenApply(this::haveAnyGroup);
    }

    public boolean checkGroupSync(@NotNull UUID playerUuid) {
        return checkGroupAsync(playerUuid).join();
    }

    private boolean haveAnyGroup(@NotNull User user) {
        return groups.stream()
                .anyMatch(group -> user.getPrimaryGroup().equalsIgnoreCase(group));
    }

    public @NotNull List<String> getGroups() {
        return new ArrayList<>(groups);
    }

    private CompletableFuture<User> getOrLoad(@NotNull UUID playerUuid) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(playerUuid);
        if (user == null) return api.getUserManager().loadUser(playerUuid);
        return CompletableFuture.completedFuture(user);
    }

    public static @NotNull GroupInfo of(@NotNull String... groups) {
        if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            throw new RuntimeException("Trying to use GroupInfo without LuckPerms plugin!");
        }
        return new GroupInfo(Arrays.asList(groups));
    }
}
