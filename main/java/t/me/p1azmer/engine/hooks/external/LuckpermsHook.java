package t.me.p1azmer.engine.hooks.external;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.hook.AbstractHook;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LuckpermsHook extends AbstractHook<DungeonPlugin> {

    private static final int weight = 1000;

    public static LuckPerms getPlugin() {
        return LuckPermsProvider.get();
    }

    public LuckpermsHook(@NotNull DungeonPlugin plugin, @NotNull String pluginName) {
        super(plugin, pluginName);
    }

    @Override
    public boolean setup() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }

    @Override
    public void shutdown() {
        this.unregisterListeners();
    }

    public static void setPrefix(Player player, String string) {
        modifyUser(player.getUniqueId(), user -> {
            String value = user.getCachedData().getMetaData().getPrefix();
            PrefixNode node = PrefixNode.builder(string, weight).build();
            if (!string.equals(value)) {
                if (value != null) user.data().clear(n -> n.getType().matches(node));
                if (!string.isEmpty()) user.data().add(node);
            }
        });
    }

    public static void setPrefix(UUID uuid, String string) {
        modifyUser(uuid, user -> {
            String value = user.getCachedData().getMetaData().getPrefix();
            PrefixNode node = PrefixNode.builder(string, weight).build();
            if (!string.equals(value)) {
                if (value != null) user.data().clear(n -> n.getType().matches(node));
                if (!string.isEmpty()) user.data().add(node);
            }
        });
    }

    public static void removePrefix(Player player) {
        modifyUser(player.getUniqueId(), user -> {
            String value = user.getCachedData().getMetaData().getPrefix();
            PrefixNode node = PrefixNode.builder("", weight).build();
            if (value != null && !value.isEmpty()) {
                user.data().clear(n -> n.getType().matches(node));

            }
        });
    }

    public static void removePrefix(UUID uuid) {
        modifyUser(uuid, user -> {
            String value = user.getCachedData().getMetaData().getPrefix();
            PrefixNode node = PrefixNode.builder("", weight).build();
            if (value != null && !value.isEmpty()) {
                user.data().clear(n -> n.getType().matches(node));

            }
        });
    }

    public static void setSuffix(Player player, String string) {
        modifyUser(player.getUniqueId(), user -> {
            String value = user.getCachedData().getMetaData().getSuffix();
            SuffixNode node = SuffixNode.builder(string, weight).build();
            if (!string.equals(value)) {
                if (value != null) user.data().clear(n -> n.getType().matches(node));
                if (!string.isEmpty()) user.data().add(node);
            }
        });
    }

    public static void setSuffix(UUID uuid, String string) {
        modifyUser(uuid, user -> {
            String value = user.getCachedData().getMetaData().getSuffix();
            SuffixNode node = SuffixNode.builder(string, weight).build();
            if (!string.equals(value)) {
                if (value != null) user.data().clear(n -> n.getType().matches(node));
                if (!string.isEmpty()) user.data().add(node);
            }
        });
    }

    public static void removeSuffix(Player player) {
        modifyUser(player.getUniqueId(), user -> {
            String value = user.getCachedData().getMetaData().getSuffix();
            SuffixNode node = SuffixNode.builder("", weight).build();
            if (value != null && !value.isEmpty()) {
                user.data().clear(n -> n.getType().matches(node));
            }
        });
    }

    public static void removeSuffix(UUID uuid) {
        modifyUser(uuid, user -> {
            String value = user.getCachedData().getMetaData().getSuffix();
            SuffixNode node = SuffixNode.builder("", weight).build();
            if (value != null && !value.isEmpty()) {
                user.data().clear(n -> n.getType().matches(node));
            }
        });
    }

    @NotNull
    public static User getUser(Player player) {
        return getPlugin().getPlayerAdapter(Player.class).getUser(player);
    }

    @Nullable
    public static User getUser(String player) {
        User user = null;
        try {
            user = getPlugin().getUserManager().loadUser(offlineUUID(player)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static void addPermission(Player player, String permission, boolean value) {
        addPermission(player.getUniqueId(), permission, value);
    }

    public static void addPermission(UUID uuid, String permission, boolean value) {
        getPlugin().getUserManager().modifyUser(uuid, user -> user.data().add(Node.builder(permission).value(value).build()));
    }

    public static void addPermissionExpiry(Player player, String permission, boolean value, long duration) {
        addPermissionExpiry(player.getUniqueId(), permission, value, duration);
    }

    public static void addPermissionExpiry(UUID uuid, String permission, boolean value, long duration) {
        if (duration < 1) {
            addPermission(uuid, permission, value);
        } else {
            modifyUser(uuid, user -> user.data().add(Node.builder(permission).value(value).expiry(duration, TimeUnit.MILLISECONDS).build()));
        }
    }

    public static void removePermission(Player player, String permission) {
        removePermission(player.getUniqueId(), permission);
    }

    public static void removePermission(UUID uuid, String permission) {
        modifyUser(uuid, user -> user.data().toCollection().forEach(node -> {
            if (node.getKey().equalsIgnoreCase(permission)) user.data().clear(n -> n.equals(node));
        }));
    }

    public static void modifyUser(UUID uuid, Consumer<? super User> action) {
        getPlugin().getUserManager().modifyUser(uuid, action);
    }

    public static TreeMap<String, String> getUserGroups(Player player) {
        return getUserGroups(player != null ? player.getName() : null);
    }

    public static TreeMap<String, String> getUserGroups(String player) { // SERVER:GROUP
        TreeMap<String, String> data = new TreeMap<>();
        if (player == null) return data;

        try {
            User user = getPlugin().getUserManager().loadUser(offlineUUID(player)).get();
            if (user == null) return data;

            for (InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) {
                if (node.hasExpired() || node.hasExpiry()) continue;
                Optional<String> context = node.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY);
                if (!context.isPresent()) continue;

                if (data.containsKey(context.get())) {
                    if (getGroupWeight(data.get(context.get())) > getGroupWeight(node.getGroupName())) continue;
                }
                data.put(context.get(), node.getGroupName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }

        return data;
    }

    public static boolean hasPermission(Player player, @NotNull String permission) {
        User user = getPlugin().getPlayerAdapter(Player.class).getUser(player);
        return user.getCachedData().getPermissionData().queryPermission(permission).result().asBoolean();
    }


    public static boolean isDonater(Player player) {
        return isDonater(player != null ? player.getName() : null);
    }

    public static boolean isDonater(String player) {
        if (player == null || player.isEmpty()) return false;
        try {
            User user = getPlugin().getUserManager().loadUser(offlineUUID(player)).get();
            if (user == null) return false;
            for (InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) {
                if (node.hasExpiry() || node.hasExpired()) continue;
                Optional<String> context = node.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY);
                if (!context.isPresent()) continue;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getGroupWeight(String name) {
        Group group = getPlugin().getGroupManager().getGroup(name);
        if (group == null || !group.getWeight().isPresent()) return 0;
        return group.getWeight().getAsInt();
    }


    public static Group getPrimaryGroup(Player player) {
        User user;
        Group group = getPlugin().getGroupManager().getGroup("default");
        if (group == null) {
            return getPlugin().getGroupManager().getLoadedGroups().stream().findFirst().get();
        }
        try {
            user = getPlugin().getUserManager().loadUser(offlineUUID(player.getName())).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            return group;
        }
//        Optional<String> optional = CollectionsUtil.sortDescent(getUserGroups(player)).values().stream().findFirst();
//        if (optional.isPresent())
//            return group;
        return getPlugin().getGroupManager().getGroup(user.getPrimaryGroup());
//        String primaryGroup = Objects.requireNonNull(getPlugin().getUserManager().getUser(player.getUniqueId())).getPrimaryGroup();
//        if (primaryGroup.isEmpty())
//            return getPlugin().getGroupManager().getGroup("default");
//        return getPlugin().getGroupManager().getGroup(primaryGroup);
    }

    public static Group getPrimaryGroup(UUID uuid) {
        User user;
        Group group = getPlugin().getGroupManager().getGroup("default");
        if (group == null) {
            return getPlugin().getGroupManager().getLoadedGroups().stream().findFirst().get();
        }
        try {
            user = getPlugin().getUserManager().loadUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            return group;
        }
        return getPlugin().getGroupManager().getGroup(user.getPrimaryGroup());
    }

    private static UUID offlineUUID(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}