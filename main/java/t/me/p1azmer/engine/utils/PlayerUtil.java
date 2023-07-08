package t.me.p1azmer.engine.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.hooks.Hooks;
import t.me.p1azmer.engine.lang.CoreLang;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PlayerUtil {

    private static final Collection<Player> messageCache = AutoRemovalCollection.newArrayList(2, TimeUnit.SECONDS);

    public static void dispatchCommands(@NotNull Player player, @NotNull String... commands) {
        for (String command : commands) {
            dispatchCommand(player, command);
        }
    }

    public static void dispatchCommand(@NotNull Player player, @NotNull String command) {
        command = command.replace("[CONSOLE]", "");
        command = command.trim().replace("%player%", player.getName());
        command = Placeholders.Player.replacer(player).apply(command);
        if (Hooks.hasPlaceholderAPI()) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }
        if (command.startsWith("[BUNGEE]")) {
            command = command.replace("[BUNGEE]", "");
            connectToServer(player, command);
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void connectToServer(Player player, String server) {
        EngineCore.get().runTaskAsync(task -> {
            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(EngineCore.get(), "BungeeCord", out.toByteArray());
            } catch (Exception ignored) {
            }
        });
    }

    @NotNull
    public static List<String> getPlayerNames() {
        return CollectionsUtil.playerNames();
    }

    public static boolean hasEmptyInventory(@NotNull Player player) {
        return Stream.of(player.getInventory().getContents()).allMatch(item -> item == null || item.getType().equals(Material.AIR));
    }

    public static boolean hasEmptyContents(@NotNull Player player) {
        return Stream.of(player.getInventory().getStorageContents()).allMatch(item -> item == null || item.getType().equals(Material.AIR));
    }

    public static int countItemSpace(@NotNull Player player, @NotNull ItemStack item) {
        int stackSize = item.getType().getMaxStackSize();
        return Stream.of(player.getInventory().getStorageContents()).mapToInt(itemHas -> {
            if (itemHas == null || itemHas.getType().equals(Material.AIR)) {
                return stackSize;
            }
            if (itemHas.isSimilar(item)) {
                return (stackSize - itemHas.getAmount());
            }
            return 0;
        }).sum();
    }

    public static int countArmor(@NotNull Player player, @NotNull Predicate<ItemStack> predicate) {
        return Stream.of(player.getEquipment().getArmorContents())
                .filter(item -> item != null && !item.getType().equals(Material.AIR) && predicate.test(item))
                .mapToInt(ItemStack::getAmount).sum();
    }

    public static int countArmor(@NotNull Player player, @NotNull ItemStack item) {
        return countArmor(player, item::isSimilar);
    }

    public static int countArmor(@NotNull Player player, @NotNull Material material) {
        return countArmor(player, itemHas -> itemHas.getType() == material);
    }

    public static int countItem(@NotNull Player player, @NotNull Predicate<ItemStack> predicate) {
        return Stream.of(player.getInventory().getStorageContents())
                .filter(item -> item != null && !item.getType().equals(Material.AIR) && predicate.test(item))
                .mapToInt(ItemStack::getAmount).sum();
    }

    public static int countItem(@NotNull Player player, @NotNull ItemStack item) {
        return countItem(player, item::isSimilar);
    }

    public static int countItem(@NotNull Player player, @NotNull Material material) {
        return countItem(player, itemHas -> itemHas.getType() == material);
    }

    public static boolean takeItem(@NotNull Player player, @NotNull ItemStack item) {
        return takeItem(player, itemHas -> itemHas.isSimilar(item), countItem(player, item));
    }

    public static boolean takeItem(@NotNull Player player, @NotNull ItemStack item, int amount) {
        return takeItem(player, itemHas -> itemHas.isSimilar(item), amount);
    }

    public static boolean takeItem(@NotNull Player player, @NotNull Material material) {
        return takeItem(player, itemHas -> itemHas.getType() == material, countItem(player, material));
    }

    public static boolean takeItem(@NotNull Player player, @NotNull Material material, int amount) {
        return takeItem(player, itemHas -> itemHas.getType() == material, amount);
    }

    public static boolean takeItem(@NotNull Player player, @NotNull Predicate<ItemStack> predicate) {
        return takeItem(player, predicate, countItem(player, predicate));
    }

    public static boolean takeItem(@NotNull Player player, @NotNull Predicate<ItemStack> predicate, int amount) {
        if (countItem(player, predicate) < amount) return false;

        int takenAmount = 0;

        Inventory inventory = player.getInventory();
        for (ItemStack itemHas : inventory.getStorageContents()) {
            if (itemHas == null || !predicate.test(itemHas)) continue;

            int hasAmount = itemHas.getAmount();
            if (takenAmount + hasAmount > amount) {
                int diff = (takenAmount + hasAmount) - amount;
                itemHas.setAmount(diff);
                break;
            }

            itemHas.setAmount(0);
            if ((takenAmount += hasAmount) == amount) {
                break;
            }
        }
        return true;
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull ItemStack item) {
        return takeArmor(player, itemHas -> itemHas.isSimilar(item), countArmor(player, item));
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull ItemStack item, int amount) {
        return takeArmor(player, itemHas -> itemHas.isSimilar(item), amount);
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull Material material) {
        return takeArmor(player, itemHas -> itemHas.getType() == material, countArmor(player, material));
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull Material material, int amount) {
        return takeArmor(player, itemHas -> itemHas.getType() == material, amount);
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull Predicate<ItemStack> predicate) {
        return takeArmor(player, predicate, countArmor(player, predicate));
    }

    public static boolean takeArmor(@NotNull Player player, @NotNull Predicate<ItemStack> predicate, int amount) {
        if (countArmor(player, predicate) < amount) return false;

        int takenAmount = 0;

        for (ItemStack itemHas : player.getEquipment().getArmorContents()) {
            if (itemHas == null || !predicate.test(itemHas)) continue;

            int hasAmount = itemHas.getAmount();
            if (takenAmount + hasAmount > amount) {
                int diff = (takenAmount + hasAmount) - amount;
                itemHas.setAmount(diff);
                break;
            }

            itemHas.setAmount(0);
            if ((takenAmount += hasAmount) == amount) {
                break;
            }
        }
        return true;
    }

    public static void addItem(@NotNull Player player, @NotNull ItemStack... items) {
        Arrays.asList(items).forEach(item -> addItem(player, item, item.getAmount()));
    }

    public static void addItem(@NotNull Player player, @NotNull ItemStack item2, int amount) {
        if (amount <= 0 || item2.getType().equals(Material.AIR)) return;

        World world = player.getWorld();

        ItemStack item = new ItemStack(item2);
        item.setAmount(Math.min(item.getMaxStackSize(), amount));
        player.getInventory().addItem(item).values().forEach(left -> {
            world.dropItem(player.getLocation(), left);
            if (messageCache.add(player))
                EngineCore.get().getMessage(CoreLang.CANT_ADD_ITEM_AND_DROP).send(player);
        });

        amount -= item.getAmount();
        if (amount > 0) addItem(player, item2, amount);
    }
}