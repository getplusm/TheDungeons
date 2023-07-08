package t.me.p1azmer.engine.utils;

import com.google.common.collect.Sets;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.hooks.Hooks;
import t.me.p1azmer.engine.hooks.external.VaultHook;
import t.me.p1azmer.engine.utils.json.text.ClickText;
import t.me.p1azmer.engine.utils.json.text.ClickWord;
import t.me.p1azmer.engine.utils.message.NexParser;
import t.me.p1azmer.engine.utils.regex.RegexUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    @Deprecated
    private static final Pattern PATTERN_LEGACY_JSON_FULL = Pattern.compile("((\\{json:)+(.*?)(\\})+(.*?))(\\{end-json\\})");
    @Deprecated
    private static final Map<String, Pattern> PATTERN_JSON_PARAMS = new HashMap<>();
    //private static final Pattern              PATTERN_JSON_FULL   = Pattern.compile("((\\{json:)+(.+?)\\}+(.*?))");
    //private static final Pattern              PATTERN_JSON_FULL   = Pattern.compile("((\\{json:){1}(.*?)\\}{1})");
    @Deprecated
    private static final Pattern PATTERN_JSON_FULL = Pattern.compile("(\\{json:(.*?)\\}+)");

    public static final Map<Player, ItemStack> ITEM_MESSAGE_CACHE = new LinkedHashMap<>();
    private static final NamespacedKey ITEM_MESSAGE_KEY = new NamespacedKey(EngineCore.get(), "NEX_MESSAGE_ITEM");

    static {
        for (String parameter : new String[]{"text", "hint", "hover", "showText", "chat-type", "runCommand", "chat-suggest", "suggestCommand", "url", "openUrl", "showItem", "copyToClipboard"}) {
            PATTERN_JSON_PARAMS.put(parameter, Pattern.compile("~+(" + parameter + ")+?:+(.*?);"));
        }
    }

    public static void sendCustom(@NotNull CommandSender sender, @NotNull String message) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(NexParser.toPlainText(message));
            return;
        }
        NexParser.toMessage(message).send(sender);
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String msg) {
        if (Hooks.hasPlaceholderAPI() && PlaceholderAPI.containsPlaceholders(msg))
            msg = PlaceholderAPI.setPlaceholders(player, msg);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, NexParser.toMessage(msg).build());
    }

    @NotNull
    @Deprecated
    public static String toNewFormat(@NotNull String message) {
        Matcher matcherOld = RegexUtil.getMatcher(PATTERN_LEGACY_JSON_FULL, message);
        int index = 0;
        while (RegexUtil.matcherFind(matcherOld)) {
            String jsonRaw = matcherOld.group(0); // Full json text, like '{json: <args>}Text{end-json}
            String jsonArgs = matcherOld.group(3).trim(); // Only json parameters, like '~hover: Text; ~openUrl: google.com;'
            String jsonText = matcherOld.group(5); // The text to apply JSON on.

            message = message.replace(jsonRaw, "{json: ~text:" + jsonText + "; " + jsonArgs + "}");
        }
        return message;
    }

    @Deprecated
    public static boolean isJSON(@NotNull String str) {
        Matcher matcher = RegexUtil.getMatcher(PATTERN_LEGACY_JSON_FULL, str);
        return matcher.find();
    }

    @Deprecated
    public static boolean hasJson(@NotNull String str) {
        Matcher matcher = RegexUtil.getMatcher(PATTERN_JSON_FULL, str);
        return matcher.find() || isJSON(str);
    }

    @NotNull
    @Deprecated
    public static String stripJsonOld(@NotNull String message) {
        /*Matcher matcher = RegexUtil.getMatcher(PATTERN_LEGACY_JSON_FULL, message);
        if (matcher == null) return message;
        while (matcher.find()) {
            String jsonRaw = matcher.group(0); // Full json text, like '{json: <args>}Text{end-json}
            String jsonText = matcher.group(5); // The text to apply JSON on.
            message = message.replace(jsonRaw, jsonText);
        }*/
        return stripJson(toNewFormat(message));
    }

    @NotNull
    @Deprecated
    public static String stripJson(@NotNull String message) {
        Matcher matcher = RegexUtil.getMatcher(PATTERN_JSON_FULL, message);
        while (RegexUtil.matcherFind(matcher)) {
            String jsonRaw = matcher.group(0); // Full json text
            message = message.replace(jsonRaw, "");
        }
        return message;
    }

    @NotNull
    @Deprecated
    public static String toSimpleText(@NotNull String message) {
        message = toNewFormat(message);

        Matcher matcher = RegexUtil.getMatcher(PATTERN_JSON_FULL, message);
        while (RegexUtil.matcherFind(matcher)) {
            String jsonRaw = matcher.group(0); // Full json text, like '{json: <args>}Text{end-json}
            String jsonArgs = matcher.group(2).trim(); // Only json parameters, like '~hover: Text; ~openUrl: google.com;'
            String text = getParamValue(jsonArgs, "text");
            message = message.replace(jsonRaw, text == null ? "" : text);
        }
        return message;
    }

    @Deprecated
    public static void sendWithJSON(@NotNull CommandSender sender, @NotNull String message) {
        sendWithJson(sender, message);
    }

    @Deprecated
    public static String[] extractNonJson(@NotNull String message) {
        message = Colorizer.apply(message.replace("\n", " "));
        message = toNewFormat(message);
        return PATTERN_JSON_FULL.split(message);
    }

    @Deprecated
    public static void sendWithJson(@NotNull CommandSender sender, @NotNull String message) {
        message = Colorizer.apply(message.replace("\n", " "));
        message = toNewFormat(message);
        if (!(sender instanceof Player)) {
            sender.sendMessage(toSimpleText(message));
            return;
        }

        if (Hooks.hasPlaceholderAPI() && PlaceholderAPI.containsPlaceholders(message))
            message = PlaceholderAPI.setPlaceholders((Player) sender, message);

        Matcher matcher = RegexUtil.getMatcher(PATTERN_JSON_FULL, message);
        Map<String, String> textParams = new HashMap<>();
        int index = 0;
        while (RegexUtil.matcherFind(matcher)) {
            String jsonRaw = matcher.group(0); // Full json text, like '{json: <args>}
            String jsonArgs = matcher.group(2).trim(); // Only json parameters, like '~hover: Text; ~openUrl: google.com;'
            //String jsonText = matcher.group(5); // The text to apply JSON on.

            String placeholder = "{%" + (index++) + "%}";
            message = message.replace(jsonRaw, placeholder);
            textParams.put(placeholder, jsonArgs);
        }

        ClickText clickText = new ClickText(message);
        for (Map.Entry<String, String> entry : textParams.entrySet()) {
            String placeholder = entry.getKey();
            String params = entry.getValue();

            String text = getParamValue(params, "text");
            if (text == null) text = "";

            ClickWord clickWord = clickText.addComponent(placeholder, text);
            for (Map.Entry<String, Pattern> entryParams : PATTERN_JSON_PARAMS.entrySet()) {
                String param = entryParams.getKey();
                String paramValue = getParamValue(params, param);
                if (paramValue == null) continue;

                switch (param) {
                    case "hint":
                    case "hover":
                    case "showText":
                        clickWord.showText(paramValue.split("\\|"));
                        break;
                    case "chat-type":
                    case "runCommand":
                        clickWord.runCommand(paramValue);
                        break;
                    case "chat-suggest":
                    case "suggestCommand":
                        clickWord.suggestCommand(paramValue);
                        break;
                    case "url":
                    case "openUrl":
                        clickWord.openURL(StringUtil.colorOff(paramValue));
                        break;
                    case "copyToClipboard":
                        clickWord.copyToClipboard(paramValue);
                        break;
                    case "showItem":
                        ItemStack item = ItemUtil.fromBase64(paramValue);
                        clickWord.showItem(item == null ? new ItemStack(Material.AIR) : item);
                        break;
                    default:
                        break;
                }
            }
        }

        clickText.send(sender);
    }

    @Nullable
    @Deprecated
    private static String getParamValue(@NotNull String from, @NotNull String param) {
        Pattern pattern = PATTERN_JSON_PARAMS.get(param);
        if (pattern == null) return null;

        Matcher matcher = RegexUtil.getMatcher(pattern, from);
        if (!RegexUtil.matcherFind(matcher)) return null;

        return matcher.group(2);//.stripLeading();
    }

    public static void sound(@NotNull Player player, @Nullable Sound sound) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, 0.9f, 0.9f);
    }

    public static void sound(@NotNull Location location, @Nullable Sound sound) {
        World world = location.getWorld();
        if (world == null || sound == null) return;

        world.playSound(location, sound, 0.9f, 0.9f);
    }

    public static void sound(@NotNull Player player, @Nullable String sound) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, 0.9f, 0.9f);
    }

    public static void sound(@NotNull Location location, @Nullable String sound) {
        World world = location.getWorld();
        if (world == null || sound == null) return;

        world.playSound(location, sound, 0.9f, 0.9f);
    }

    public static void sound(@NotNull Player player, @Nullable Sound sound, float volume, float pitch) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void sound(@NotNull Location location, @Nullable Sound sound, float volume, float pitch) {
        World world = location.getWorld();
        if (world == null || sound == null) return;

        world.playSound(location, sound, volume, pitch);
    }

    public static void sound(@NotNull Player player, @Nullable String sound, float volume, float pitch) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void sound(@NotNull Location location, @Nullable String sound, float volume, float pitch) {
        World world = location.getWorld();
        if (world == null || sound == null) return;

        world.playSound(location, sound, volume, pitch);
    }

    public static void debug(@NotNull Player player, @NotNull String... message) {
        if (!player.hasPermission("aves.admin")) return;
        for (String text : message) {
            player.sendMessage(Colorizer.apply("&8[debug] | &f" + text));
        }
    }

    public static void debug(@NotNull Player player, @NotNull Object prefix, @NotNull String... message) {
        if (!player.hasPermission("aves.admin")) return;
        for (String text : message) {
            player.sendMessage(Colorizer.apply("&8[&7" + prefix + "&8-debug] | &f" + text));
        }
    }

    public static void debug(@NotNull Player player, @NotNull String message) {
        if (!player.hasPermission("aves.admin")) return;
        player.sendMessage(Colorizer.apply("&8[debug] | &f" + message));
    }

    public static void debug(@NotNull Player player, @NotNull Object prefix, @NotNull String message) {
        if (!player.hasPermission("aves.admin")) return;
        player.sendMessage(Colorizer.apply("&8[&7" + prefix + "&8-debug] | &f" + message));
    }

    public static void debug(@NotNull LivingEntity entity, @NotNull String... message) {
        if (!entity.hasPermission("aves.admin")) return;
        for (String text : message) {
            entity.sendMessage(Colorizer.apply("&8[debug] | &f" + text));
        }
    }

    public static void debug(@NotNull LivingEntity entity, @NotNull Object prefix, @NotNull String... message) {
        if (!entity.hasPermission("aves.admin")) return;
        for (String text : message) {
            entity.sendMessage(Colorizer.apply("&8[&7" + prefix + "&8-debug] | &f" + text));
        }
    }

    public static void debug(@NotNull LivingEntity entity, @NotNull String message) {
        if (!entity.hasPermission("aves.admin")) return;
        entity.sendMessage(Colorizer.apply("&8[debug] | &f" + message));
    }

    public static void debug(@NotNull LivingEntity entity, @NotNull Object prefix, @NotNull String message) {
        if (!entity.hasPermission("aves.admin")) return;
        entity.sendMessage(Colorizer.apply("&8[&7" + prefix + "&8-debug] | &f" + message));
    }

    public static void debug(@NotNull Object prefix, @NotNull String message) {
        Bukkit.getOnlinePlayers().forEach(player -> debug(player, prefix, message));
    }

    public static void debug(@NotNull String message) {
        Bukkit.getOnlinePlayers().forEach(player -> debug(player, message));
    }

    public static void debug(@NotNull Object prefix, @NotNull String... message) {
        Bukkit.getOnlinePlayers().forEach(player -> debug(player, prefix, message));
    }

    public static void debug(@NotNull String group, @NotNull Object prefix, @NotNull String message) {
        Bukkit.getOnlinePlayers()
                .stream().filter(player -> VaultHook.getPermissionGroup(player).toLowerCase(Locale.ROOT).equalsIgnoreCase(group.toLowerCase()))
                .forEach(player -> debug(player, prefix, message));
    }

    public static void sendItemMessage(Player player, String message) {

//        if (PLAYER_ITEM_MESSAGE_CACHE.add(player)) {
        if (Hooks.hasPlaceholderAPI() && PlaceholderAPI.containsPlaceholders(message))
            message = PlaceholderAPI.setPlaceholders(player, message);

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemStack activeItem = item.clone();
        ITEM_MESSAGE_CACHE.put(player, activeItem);

        if (item.getType().equals(Material.AIR)) {
            item.setType(Material.STAINED_GLASS);
        }
//        int slot = player.getInventory().getHeldItemSlot();

//        ItemStack newItem = new ItemStack(item.clone());
//        if (newItem.getType().isAir()) newItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage("Returned message item but itemmeta is null!");
            return;
        }
        meta.setDisplayName(message);
        item.setItemMeta(meta);

        //PDCUtil.set(newItem, ITEM_MESSAGE_KEY, true);
        PDCUtil.set(item, ITEM_MESSAGE_KEY, true);
        sendSetSlotPacket(player, player.getInventory().getHeldItemSlot(), item);
        //player.getInventory().setItemInMainHand(item);

//        ITEM_MESSAGE_CACHE.put(player, new TripleReturn<>(item, newItem, slot));
//        player.getInventory().setItem(slot, newItem);
//        player.sendMessage("Change your item to message item!");
        restoreMessageItem(player, false);
//        }
    }

    public static void restoreMessageItem(Player player, boolean fast) {
//        if (!fast) {
//            Schedulers.sync().runLater(() -> {
//                if (ITEM_MESSAGE_CACHE.containsKey(player)) {
//                    ItemStack oldItem = ITEM_MESSAGE_CACHE.remove(player);
//                    sendSetSlotPacket(player, player.getInventory().getHeldItemSlot(), oldItem);
        //player.getInventory().setItemInMainHand(oldItem);
//                    TripleReturn<ItemStack, ItemStack, Integer> pair = ITEM_MESSAGE_CACHE.get(player);
//                    ItemStack oldItem = pair.first();
//                    ItemStack replaceItem = pair.second();
//                    int slot = pair.third();
//
//                    if (replaceItem != null && PDCUtil.getBooleanData(replaceItem, ITEM_MESSAGE_KEY)) {
//                        if (oldItem != null)
//                            player.getInventory().setItem(slot, oldItem);
//                        PDCUtil.removeData(replaceItem, ITEM_MESSAGE_KEY);
//                        ITEM_MESSAGE_CACHE.remove(player, pair);
//                    }
//                }
//            }, 2, TimeUnit.SECONDS);
//        } else {
        if (ITEM_MESSAGE_CACHE.containsKey(player)) {
            ItemStack oldItem = ITEM_MESSAGE_CACHE.get(player);
            sendSetSlotPacket(player, player.getInventory().getHeldItemSlot(), oldItem);
            ITEM_MESSAGE_CACHE.remove(player);
//                player.getInventory().setItemInMainHand(oldItem);
//                TripleReturn<ItemStack, ItemStack, Integer> pair = ITEM_MESSAGE_CACHE.get(player);
//                ItemStack oldItem = pair.first();
//                ItemStack replaceItem = pair.second();
//                int slot = pair.third();
//
//                if (replaceItem != null && PDCUtil.getBooleanData(replaceItem, ITEM_MESSAGE_KEY)) {
//                    if (oldItem != null)
//                        player.getInventory().setItem(slot, oldItem);
//                    PDCUtil.removeData(replaceItem, ITEM_MESSAGE_KEY);
//                    ITEM_MESSAGE_CACHE.remove(player, pair);
//                }
        }

//        }

    }

    public static void sendSetSlotPacket(Player player, int slot, ItemStack item) {
        EngineCore.get().error("Method sendSlotPacket cannot use now!");
//        ServerPlayer craftPlayer = ((CraftPlayer) player).getHandle();
//        ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(ClientboundContainerSetSlotPacket.PLAYER_INVENTORY, 1, slot, CraftItemStack.asNMSCopy(item));
//        craftPlayer.connection.send(packet);
    }

    // Далее идут ивенты для @sendItemMessage

    public static class MessageItemEvents extends AbstractListener<DungeonPlugin> {

        public MessageItemEvents(@NotNull DungeonPlugin plugin) {
            super(plugin);
        }

        private boolean isMessageItem(ItemStack item) {
            return PDCUtil.getBoolean(item, ITEM_MESSAGE_KEY).orElse(false);
        }

        @EventHandler
        public void onDeath(PlayerDeathEvent event) {
            restoreMessageItem(event.getEntity(), true);
        }

//        @EventHandler
//        public void onOpenInventory(InventoryOpenEvent event) {
//            if (event.getPlayer() instanceof Player player) restoreMessageItem(player, true);
//        }
//
//        @EventHandler
//        public void onPlayerTeleport(PlayerTeleportEvent event) {
//            restoreMessageItem(event.getPlayer(), true);
//        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (isMessageItem(item)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onConsume(PlayerItemConsumeEvent event) {
            ItemStack item = event.getItem();
            if (isMessageItem(item)) event.setCancelled(true);
        }

        @EventHandler
        public void onDrag(InventoryDragEvent event) {
            ItemStack item = event.getCursor();
            if (item != null) if (isMessageItem(item)) event.setCancelled(true);
        }

        @EventHandler
        public void onSwitch(PlayerSwapHandItemsEvent event) {
            ItemStack item = event.getMainHandItem();
            if (item != null) if (isMessageItem(item)) {
                event.setCancelled(true);
            }
        }


        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onRequirementsItemHeld(PlayerItemHeldEvent e) {
            int slot = e.getNewSlot();
            Player player = e.getPlayer();
            ItemStack item = player.getInventory().getItem(slot);
            //if (item == null || item.getType().isAir()) return;

            if (isMessageItem(item)) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onRequirementsItemDispense(BlockDispenseArmorEvent e) {
            ItemStack item = e.getItem();
            LivingEntity entity = e.getTargetEntity();
            if (entity instanceof Player) {
                if (isMessageItem(item)) {
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onRequirementsItemDrag(InventoryDragEvent e) {
            if (e.getInventory().getType() != InventoryType.CRAFTING) return;

            ItemStack drag = e.getOldCursor();
            //if (drag.getType().isAir()) return;

            Set<Integer> slots = e.getRawSlots();
            Set<Integer> deny = Sets.newHashSet(5, 6, 7, 8, 45);

            boolean doCheck = slots.stream().anyMatch(deny::contains);

            if (doCheck && isMessageItem(drag)) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onRequirementsItemClick(InventoryClickEvent e) {
            ItemStack item = e.getCursor();
            //if (item == null || item.getType().isAir()) return;

            if (isMessageItem(item)) {
                e.setCancelled(true);
                return;
            }
            item = e.getCurrentItem();
            //if (item == null || item.getType().isAir()) return;

            if (isMessageItem(item)) {
                e.setCancelled(true);
                return;
            }

            if (e.getInventory().getType() != InventoryType.CRAFTING) return;

            Player player = (Player) e.getWhoClicked();
            int slot = e.getSlot();
            if ((slot >= 36 && slot <= 40) || slot == player.getInventory().getHeldItemSlot()) {
                ItemStack drag = e.getCursor();
                if (drag != null && isMessageItem(drag)) {
                    e.setCancelled(true);
                    return;
                }
            }

            item = e.getCurrentItem();
            if (item == null) return;

            if (e.getAction() == InventoryAction.HOTBAR_SWAP && isMessageItem(item)) {
                e.setCancelled(true);
                return;
            }

            if (e.isShiftClick() && isMessageItem(item)) {
                e.setCancelled(true);
            }
        }
    }

}