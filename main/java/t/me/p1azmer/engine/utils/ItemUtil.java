package t.me.p1azmer.engine.utils;

import com.google.common.base.Splitter;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.config.CoreConfig;
import t.me.p1azmer.engine.hooks.Hooks;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ItemUtil {

    @Deprecated
    public static final String LORE_FIX_PREFIX = "fogus_loren-";
    @Deprecated
    public static final String NAME_FIX_PREFIX = "fogus_namel-";
    @Deprecated
    public static final String TAG_SPLITTER = "__x__";
    private static final DungeonPlugin ENGINE = EngineCore.get();
    @Deprecated
    private static final Map<String, NamespacedKey> LORE_KEYS_CACHE = new HashMap<>();
    @Deprecated
    private static final Map<String, NamespacedKey> NAME_KEYS_CACHE = new HashMap<>();


    public static void clear() {
        LORE_KEYS_CACHE.clear();
        NAME_KEYS_CACHE.clear();
    }

    public static int addToLore(@NotNull List<String> lore, int pos, @NotNull String value) {
        if (pos >= lore.size() || pos < 0) {
            lore.add(value);
        } else {
            lore.add(pos, value);
        }
        return pos < 0 ? pos : pos + 1;
    }

    @NotNull
    public static String getItemName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta == null || !meta.hasDisplayName()) ? LangManager.getMaterial(item.getType()) : meta.getDisplayName();
    }

    public static void mapMeta(@NotNull ItemStack item, @NotNull Consumer<ItemMeta> function) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        function.accept(meta);
        item.setItemMeta(meta);
    }

    @NotNull
    public static List<String> getLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta == null || meta.getLore() == null) ? new ArrayList<>() : meta.getLore();
    }

    @NotNull
    public static ItemStack createCustomHead(@NotNull String texture) {
        ItemStack item = new ItemStack(Material.SKULL);
        setSkullTexture(item, texture);
        return item;
    }

    public static void setSkullTexture(@NotNull ItemStack item, @NotNull String value) {
        if (item.getType() != Material.SKULL_ITEM) return;
        if (!(item.getItemMeta() instanceof SkullMeta)) return;
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(CoreConfig.getIdForSkullTexture(value), null);
        profile.getProperties().put("textures", new Property("textures", value));

        Method method = Reflex.getMethod(meta.getClass(), "setProfile", GameProfile.class);
        if (method != null) {
            Reflex.invokeMethod(method, meta, profile);
        } else {
            Reflex.setFieldValue(meta, "profile", profile);
        }

        item.setItemMeta(meta);
    }

    @NotNull
    public static ItemStack returnSkullTexture(@NotNull ItemStack item, @NotNull String value) {
        if (item.getType() != Material.SKULL_ITEM) return item;
        if (!(item.getItemMeta() instanceof SkullMeta)) return item;
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(CoreConfig.getIdForSkullTexture(value), null);
        profile.getProperties().put("textures", new Property("textures", value));

        Method method = Reflex.getMethod(meta.getClass(), "setProfile", GameProfile.class);
        if (method != null) {
            Reflex.invokeMethod(method, meta, profile);
        } else {
            Reflex.setFieldValue(meta, "profile", profile);
        }

        item.setItemMeta(meta);
        return item;
    }

    @Nullable
    public static String getSkullTexture(@NotNull ItemStack item) {
        if (item.getType() != Material.SKULL_ITEM) return null;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return null;

        GameProfile profile = (GameProfile) Reflex.getFieldValue(meta, "profile");
        if (profile == null) return null;

        Collection<Property> properties = profile.getProperties().get("textures");
        Optional<Property> opt = properties.stream().filter(prop -> {
            return prop.getName().equalsIgnoreCase("textures") || prop.getSignature().equalsIgnoreCase("textures");
        }).findFirst();

        return opt.map(Property::getValue).orElse(null);
    }

    public static void setPlaceholderAPI(@NotNull Player player, @NotNull ItemStack item) {
        if (!Hooks.hasPlaceholderAPI()) return;
        replace(item, str -> Colorizer.apply(PlaceholderAPI.setPlaceholders(player, str)));
    }

    public static void replace(@NotNull ItemStack item, @NotNull UnaryOperator<String> replacer) {
        mapMeta(item, meta -> replace(meta, replacer));
    }

    public static void replace(@NotNull ItemMeta meta, @NotNull UnaryOperator<String> replacer) {
        if (meta.hasDisplayName()) {
            meta.setDisplayName(replacer.apply(meta.getDisplayName()));
        }

        List<String> loreHas = meta.getLore();
        //List<String> loreReplaced = new ArrayList<>();
        if (loreHas != null) {
            // Should perform much faster
            String single = replacer.apply(String.join("\n", loreHas));
            meta.setLore(StringUtil.stripEmpty(Splitter.on("\n").splitToList(single)));

            //loreHas.replaceAll(replacer);
            //loreHas.forEach(line -> loreReplaced.addAll(Arrays.asList(line.split("\\n"))));
            //meta.setLore(StringUtil.stripEmpty(loreReplaced));
        }
    }

    public static void replaceLore(@NotNull ItemStack item, @NotNull String placeholder, @NotNull List<String> replacer) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> loreHas = meta.getLore();
        if (loreHas == null) return;

        List<String> loreReplaced = new ArrayList<>();
        for (String lineHas : loreHas) {
            if (lineHas.contains(placeholder)) {
                replacer.forEach(lineRep -> {
                    loreReplaced.add(lineHas.replace(placeholder, lineRep));
                });
                continue;
            }
            loreReplaced.add(lineHas);
        }
        meta.setLore(StringUtil.stripEmpty(loreReplaced));
        item.setItemMeta(meta);
    }

    @Deprecated
    public static boolean isWeapon(@NotNull ItemStack item) {
        return isSword(item) || isAxe(item);//|| isTrident(item);
    }

    public static boolean isTool(@NotNull ItemStack item) {
        return isAxe(item) || isHoe(item) || isPickaxe(item) || isShovel(item);
    }

    public static boolean isArmor(@NotNull ItemStack item) {
        return isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
    }

    public static boolean isBow(@NotNull ItemStack item) {
        return item.getType() == Material.BOW;
    }

    public static boolean isSword(@NotNull ItemStack item) {
        Material material = item.getType();
        return material == Material.DIAMOND_SWORD || material == Material.GOLD_SWORD
                || material == Material.IRON_SWORD //|| material == Material.NETHERITE_SWORD
                || material == Material.STONE_SWORD || material == Material.WOOD_SWORD;
    }

    public static boolean isAxe(@NotNull ItemStack item) {
        Material material = item.getType();
        return material == Material.DIAMOND_AXE || material == Material.GOLD_AXE
                || material == Material.IRON_AXE //|| material == Material.NETHERITE_AXE
                || material == Material.STONE_AXE || material == Material.WOOD_AXE;
    }

//    public static boolean isTrident(@NotNull ItemStack item) {
//        return item.getType() == Material.TRIDENT;
//    }

    public static boolean isPickaxe(@NotNull ItemStack item) {
        Material material = item.getType();
        return material == Material.DIAMOND_PICKAXE || material == Material.GOLD_PICKAXE
                || material == Material.IRON_PICKAXE //|| material == Material.NETHERITE_PICKAXE
                || material == Material.STONE_PICKAXE || material == Material.WOOD_PICKAXE;
    }

    public static boolean isShovel(@NotNull ItemStack item) {

        Material material = item.getType();
        return material == Material.DIAMOND_SPADE || material == Material.GOLD_SPADE
                || material == Material.IRON_SPADE //|| material == Material.NETHERITE_SHOVEL
                || material == Material.STONE_SPADE || material == Material.WOOD_SPADE;
    }

    public static boolean isHoe(@NotNull ItemStack item) {

        Material material = item.getType();
        return material == Material.DIAMOND_HOE || material == Material.GOLD_HOE
                || material == Material.IRON_HOE //|| material == Material.NETHERITE_HOE
                || material == Material.STONE_HOE || material == Material.WOOD_HOE;
    }

    public static boolean isElytra(@NotNull ItemStack item) {
        return item.getType() == Material.ELYTRA;
    }

    public static boolean isFishingRod(@NotNull ItemStack item) {
        return item.getType() == Material.FISHING_ROD;
    }

    public static boolean isHelmet(@NotNull ItemStack item) {
        return getEquipmentSlot(item) == EquipmentSlot.HEAD;
    }

    public static boolean isChestplate(@NotNull ItemStack item) {
        return getEquipmentSlot(item) == EquipmentSlot.CHEST;
    }

    public static boolean isLeggings(@NotNull ItemStack item) {
        return getEquipmentSlot(item) == EquipmentSlot.LEGS;
    }

    public static boolean isBoots(@NotNull ItemStack item) {
        return getEquipmentSlot(item) == EquipmentSlot.FEET;
    }

    @NotNull
    public static EquipmentSlot getEquipmentSlot(@NotNull ItemStack item) {
        Material material = item.getType();
        return //material.isItem() ? material.getEquipmentSlot() :
                EquipmentSlot.HAND;
    }

    @Deprecated
    public static void addLore(@NotNull ItemStack item, @NotNull String id, @NotNull String text, int pos) {
        String[] lines = text.split(TAG_SPLITTER);
        addLore(item, id, Arrays.asList(lines), pos);
    }

    @Deprecated
    public static void addLore(@NotNull ItemStack item, @NotNull String id, @NotNull List<String> text, int pos) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        Colorizer.apply(text);
        StringBuilder loreTag = new StringBuilder();

        delLore(item, id);
        for (String line : text) {
            pos = addToLore(lore, pos, line);

            if (loreTag.length() > 0)
                loreTag.append(TAG_SPLITTER);
            loreTag.append(line);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        addLoreTag(item, id, loreTag.toString());
    }

    @Deprecated
    public static void delLore(@NotNull ItemStack item, @NotNull String id) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        int index = getLoreIndex(item, id, 0);
        if (index < 0) return;

        int lastIndex = getLoreIndex(item, id, 1);
        int diff = lastIndex - index;

        for (int i = 0; i < (diff + 1); i++) {
            lore.remove(index);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        delLoreTag(item, id);
    }

    @Deprecated
    public static int getLoreIndex(@NotNull ItemStack item, @NotNull String id) {
        return getLoreIndex(item, id, 0);
    }

    @Deprecated
    public static int getLoreIndex(@NotNull ItemStack item, @NotNull String id, int type) {
        String storedText = PDCUtil.getStringData(item, ItemUtil.getLoreKey(id));
        if (storedText == null) return -1;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;

        List<String> lore = meta.getLore();
        if (lore == null) return -1;

        String[] lines = storedText.split(TAG_SPLITTER);
        String lastText = null;
        int count = 0;

        if (type == 0) {
            for (String line : lines) {
                lastText = line;
                if (!StringUtil.colorOff(lastText).isEmpty()) {
                    break;
                }
                count--;
            }
        } else {
            for (int i = lines.length; i > 0; i--) {
                lastText = lines[i - 1];
                if (!StringUtil.colorOff(lastText).isEmpty()) {
                    break;
                }
                count++;
            }
        }

        if (lastText == null)
            return -1;

        int index = lore.indexOf(lastText) + count;

        // Clean up invalid lore tags.
        if (index < 0) {
            delLoreTag(item, id);
        }
        return index;
    }

    @NotNull
    @Deprecated
    private static NamespacedKey getLoreKey(@NotNull String id2) {
        String id = id2.toLowerCase();
        return LORE_KEYS_CACHE.computeIfAbsent(id, key -> new NamespacedKey(ENGINE, LORE_FIX_PREFIX + id));
    }

    @NotNull
    @Deprecated
    private static NamespacedKey getNameKey(@NotNull String id2) {
        String id = id2.toLowerCase();
        return NAME_KEYS_CACHE.computeIfAbsent(id, key -> new NamespacedKey(ENGINE, NAME_FIX_PREFIX + id));
    }

    @Deprecated
    public static void addLoreTag(@NotNull ItemStack item, @NotNull String id, @NotNull String text) {
        ItemUtil.addLoreTag(item, ItemUtil.getLoreKey(id), text);
    }

    @Deprecated
    public static void addLoreTag(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull String text) {
        PDCUtil.setData(item, key, text);
    }

    @Deprecated
    public static void delLoreTag(@NotNull ItemStack item, @NotNull String id) {
        ItemUtil.delLoreTag(item, ItemUtil.getLoreKey(id));
    }

    @Deprecated
    public static void delLoreTag(@NotNull ItemStack item, @NotNull NamespacedKey key) {
        PDCUtil.removeData(item, key);
    }

    @Nullable
    @Deprecated
    public static String getLoreTag(@NotNull ItemStack item, @NotNull String id) {
        return ItemUtil.getLoreTag(item, ItemUtil.getLoreKey(id));
    }

    @Nullable
    @Deprecated
    public static String getLoreTag(@NotNull ItemStack item, @NotNull NamespacedKey key) {
        return PDCUtil.getStringData(item, key);
    }

    @Deprecated
    public static void addNameTag(@NotNull ItemStack item, @NotNull String id, @NotNull String text) {
        PDCUtil.setData(item, ItemUtil.getNameKey(id), text);
    }

    @Deprecated
    public static void delNameTag(@NotNull ItemStack item, @NotNull String id) {
        PDCUtil.removeData(item, ItemUtil.getNameKey(id));
    }

    @Nullable
    @Deprecated
    public static String getNameTag(@NotNull ItemStack item, @NotNull String id) {
        return PDCUtil.getStringData(item, ItemUtil.getNameKey(id));
    }


    @NotNull
    public static String getNBTTag(@NotNull ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsItem.getTag();
        return compound == null ? "null" : compound.toString();
    }

    public static String toBase64(ItemStack... items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack stack : items) {
                dataOutput.writeObject(stack);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack fromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            dataInput.close();
            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            try {
                throw new IOException("Unable to decode class type.", e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}