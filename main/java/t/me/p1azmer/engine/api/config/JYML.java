package t.me.p1azmer.engine.api.config;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.menu.MenuItem;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.type.ClickType;
import t.me.p1azmer.engine.utils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JYML extends YamlConfiguration {

    private final File file;
    private boolean isChanged = false;

    public JYML(@NotNull String path, @NotNull String file) {
        this(new File(path, file));
    }

    public JYML(@NotNull File file) {
        FileUtil.create(file);
        this.file = file;
        this.reload();
    }

    @NotNull
    public static JYML loadOrExtract(@NotNull NexPlugin<?> plugin, @NotNull String path, @NotNull String file) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        return loadOrExtract(plugin, path + file);
    }

    @NotNull
    public static JYML loadOrExtract(@NotNull NexPlugin<?> plugin, @NotNull String filePath) {
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        File file = new File(plugin.getDataFolder() + filePath);
        if (FileUtil.create(file)) {
            try {
                InputStream input = plugin.getClass().getResourceAsStream(filePath);
                if (input != null) FileUtil.copy(input, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new JYML(file);
    }

    @NotNull
    public static List<JYML> loadAll(@NotNull String path) {
        return loadAll(path, false);
    }

    @NotNull
    public static List<JYML> loadAll(@NotNull String path, boolean deep) {
        return FileUtil.getFiles(path, deep).stream().filter(file -> file.getName().endsWith(".yml")).map(JYML::new).collect(Collectors.toList());
    }

    public void initializeOptions(@NotNull Object from) {
        initializeOptions(from, this);
    }

    public static void initializeOptions(@NotNull Object from, @NotNull JYML cfg) {
        boolean isStatic = from instanceof Class;
        Class<?> clazz = isStatic ? (Class<?>) from : from.getClass();

        for (Field field : Reflex.getFields(clazz)) {
            if (!JOption.class.isAssignableFrom(field.getType())) continue;
            if (!field.isAccessible()) continue;

            try {
                JOption<?> option = (JOption<?>) field.get(from);
                option.read(cfg);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cfg.saveChanges();
    }

    @NotNull
    public File getFile() {
        return this.file;
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            EngineCore.get().error("Could not save config: " + file.getName());
            e.printStackTrace();
        }
    }

    public boolean saveChanges() {
        if (this.isChanged) {
            this.save();
            this.isChanged = false;
            return true;
        }
        return false;
    }

    public boolean reload() {
        try {
            this.load(this.file);
            this.isChanged = false;
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addMissing(@NotNull String path, @Nullable Object val) {
        if (this.contains(path)) return false;
        this.set(path, val);
        return true;
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        if (value instanceof String) {
            String str = (String) value;
            value = Colorizer.plain(str);
        } else if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> list = new ArrayList<>(collection);
            list.replaceAll(obj -> obj instanceof String ? Colorizer.plain((String) obj) : obj);
            value = list;
        } else if (value instanceof Location) {
            Location location = (Location) value;
            value = LocationUtil.serialize(location);
        } else if (value instanceof Enum<?>) {
            Enum<?> en = (Enum<?>) value;
            value = en.name();
        }
        super.set(path, value);
        this.isChanged = true;
    }

    public boolean remove(@NotNull String path) {
        if (!this.contains(path)) return false;
        this.set(path, null);
        return true;
    }

    @NotNull
    public Set<String> getSection(@NotNull String path) {
        ConfigurationSection section = this.getConfigurationSection(path);
        return section == null ? Collections.emptySet() : section.getKeys(false);
    }

    @Override
    @Nullable
    public String getString(@NotNull String path) {
        String str = super.getString(path);
        return str == null || str.isEmpty() ? null : str;
    }

    @Override
    @NotNull
    public String getString(@NotNull String path, @Nullable String def) {
        String str = super.getString(path, def);
        return str == null ? "" : str;
    }

    @NotNull
    public Set<String> getStringSet(@NotNull String path) {
        return new HashSet<>(this.getStringList(path));
    }

//    @Override
//    @Nullable
//    public Location getLocation(@NotNull String path) {
//        String raw = this.getString(path);
//        return raw == null ? null : LocationUtil.deserialize(raw);
//    }

    public int[] getIntArray(@NotNull String path) {
        int[] slots = new int[0];

        String str = this.getString(path);
        return str == null ? slots : StringUtil.getIntArray(str);
    }

    public void setIntArray(@NotNull String path, int[] arr) {
        if (arr == null) {
            this.set(path, null);
            return;
        }
        this.set(path, String.join(",", IntStream.of(arr).boxed().map(String::valueOf).collect(Collectors.toList())));
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return StringUtil.getEnum(this.getString(path, ""), clazz).orElse(null);
    }

    @NotNull
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @NotNull T def) {
        return StringUtil.getEnum(this.getString(path, ""), clazz).orElse(def);
    }

    @NotNull
    public <T extends Enum<T>> List<T> getEnumList(@NotNull String path, @NotNull Class<T> clazz) {
        return this.getStringSet(path).stream().map(str -> StringUtil.getEnum(str, clazz).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /*@NotNull
    public Set<FireworkEffect> getFireworkEffects(@NotNull String path) {
        Set<FireworkEffect> effects = new HashSet<>();
        for (String sId : this.getSection(path)) {
            String path2 = path + "." + sId + ".";
            FireworkEffect.Type type = this.getEnum(path2 + "Type", FireworkEffect.Type.class);
            if (type == null) continue;
            boolean flicker = this.getBoolean(path2 + "Flicker");
            boolean trail = this.getBoolean(path2 + "Trail");
            Set<Color> colors = new HashSet<>();
            for (String colorRaw : this.getStringList(path2 + "Colors")) {
                colors.add(StringUtil.parseColor(colorRaw));
            }
            Set<Color> fadeColors = new HashSet<>();
            for (String colorRaw : this.getStringList(path2 + "Fade_Colors")) {
                fadeColors.add(StringUtil.parseColor(colorRaw));
            }
            FireworkEffect.Builder builder = FireworkEffect.builder()
                .with(type).flicker(flicker).trail(trail).withColor(colors).withFade(fadeColors);
            effects.add(builder.build());
        }
        return effects;
    }*/

    @NotNull
    public ItemStack getItem(@NotNull String path, @Nullable ItemStack def) {
        ItemStack item = this.getItem(path);
        return item.getType().equals(Material.AIR) && def != null ? def : item;
    }

    @NotNull
    public ItemStack getItem(@NotNull String path) {
        if (!path.isEmpty() && !path.endsWith(".")) path = path + ".";

//        if (this.getBoolean(path + "Encoded.Use")) {
//            ItemStack item = this.getItemEncoded(path + "Encoded.Value");
//            return item == null ? new ItemStack(Material.AIR) : item;
//        }

        Material material = Material.getMaterial(this.getString(path + "Material", "").toUpperCase());
        if (material == null || material == Material.AIR) return new ItemStack(Material.AIR);

        ItemStack item = new ItemStack(material);
        item.setAmount(this.getInt(path + "Amount", 1));

        String headTexture = this.getString(path + "Head_Texture", "");
        if (!headTexture.isEmpty()) {
            ItemUtil.setSkullTexture(item, headTexture);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        int durability = this.getInt(path + "Durability");
        if (durability > 0 && meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(durability);
        }

        String name = this.getString(path + "Name");
        meta.setDisplayName(name != null ? Colorizer.apply(name) : null);
        meta.setLore(Colorizer.apply(this.getStringList(path + "Lore")));

        for (String sKey : this.getSection(path + "Enchants")) {
            Enchantment enchantment = Enchantment.getByName(sKey);
            if (enchantment == null) continue;

            int eLvl = this.getInt(path + "Enchants." + sKey);
            if (eLvl <= 0) continue;

            meta.addEnchant(enchantment, eLvl, true);
        }

//        int model = this.getInt(path + "Custom_Model_Data");
//        meta.se(model != 0 ? model : null);

        List<String> flags = this.getStringList(path + "Item_Flags");
        if (flags.contains(Placeholders.WILDCARD)) {
            meta.addItemFlags(ItemFlag.values());
        } else {
            flags.stream().map(str -> StringUtil.getEnum(str, ItemFlag.class).orElse(null)).filter(Objects::nonNull).forEach(meta::addItemFlags);
        }

        String colorRaw = this.getString(path + "Color");
        if (colorRaw != null && !colorRaw.isEmpty()) {
            Color color = StringUtil.parseColor(colorRaw);
            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
                armorMeta.setColor(color);
            } else if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                potionMeta.setColor(color);
            }
        }

        meta.setUnbreakable(this.getBoolean(path + "Unbreakable"));
        item.setItemMeta(meta);

        return item;
    }

    @NotNull
    @Deprecated
    public MenuItem getMenuItem(@NotNull String path) {
        return this.getMenuItem(path, MenuItemType.class);
    }

    @NotNull
    @Deprecated
    public <T extends Enum<T>> MenuItem getMenuItem(@NotNull String path, @Nullable Class<T> clazzEnum) {
        if (!path.endsWith(".")) path = path + ".";

        String[] pathSplit = path.split("\\.");
        String id = pathSplit[pathSplit.length - 1];
        if (id == null || id.isEmpty()) id = UUID.randomUUID().toString();

        for (String displayId : this.getSection(path + "Display")) {
            String path2 = path + "Display." + displayId + ".";
            ItemStack dItem = this.getItem(path2 + "Item");
            this.setItem(path + "Item", dItem);
            break;
        }
        this.remove(path + "Display");


        Enum<?> type = clazzEnum == null ? MenuItemType.NONE : this.getEnum(path + "Type", clazzEnum, clazzEnum.getEnumConstants()[0]);
        if (type != MenuItemType.NONE) {
            this.addMissing(path + "Priority", 5);
        }

        ItemStack item = this.getItem(path + "Item");
        int[] slots = getMenuSlots(path + "Slots");
        int priority = this.getInt(path + "Priority");

        Map<ClickType, List<String>> clickCommands = new HashMap<>();
        for (String sType : this.getSection(path + "Click_Actions")) {
            ClickType clickType = StringUtil.getEnum(sType, ClickType.class).orElse(null);
            if (clickType == null) continue;

            clickCommands.put(clickType, this.getStringList(path + "Click_Actions." + sType));
        }

        this.saveChanges();
        return new MenuItem(id, type, slots, priority, item, clickCommands);
    }

    public void setItem(@NotNull String path, @Nullable ItemStack item) {
        if (item == null) {
            this.set(path, null);
            return;
        }

        if (!path.endsWith(".")) path = path + ".";
        this.set(path.substring(0, path.length() - 1), null);

        Material material = item.getType();
        this.set(path + "Material", material.name());
        this.set(path + "Amount", item.getAmount() <= 1 ? null : item.getAmount());
        this.set(path + "Head_Texture", ItemUtil.getSkullTexture(item));

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

//        boolean hasNbt = !meta.getPersistentDataContainer().isEmpty();
//        if (hasNbt) {
//            this.set(path + "Encoded.Use", true);
//            this.setItemEncoded(path + "Encoded.Value", item);
//        } else {
//            if (this.contains(path + "Encoded.Value")) {
//                this.set(path + "Encoded.Use", false);
//            } else {
//                this.remove(path + "Encoded");
//            }
//        }

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            this.set(path + "Durability", damageable.getDamage() <= 0 ? null : damageable.getDamage());
        }

        this.set(path + "Name", meta.getDisplayName().isEmpty() ? null : meta.getDisplayName());
        this.set(path + "Lore", meta.getLore());

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            this.set(path + "Enchants." + entry.getKey().getName(), entry.getValue());
        }
        //this.set(path + "Custom_Model_Data", meta.hasCustomModelData() ? meta.getCustomModelData() : null);

        Color color = null;
        String colorRaw = null;
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            color = potionMeta.getColor();
        } else if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            color = armorMeta.getColor();
        }
        if (color != null) {
            colorRaw = color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ",";
        }
        this.set(path + "Color", colorRaw);

        List<String> itemFlags = new ArrayList<>(meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
        this.set(path + "Item_Flags", itemFlags.isEmpty() ? null : itemFlags);
        this.set(path + "Unbreakable", meta.isUnbreakable() ? true : null);
    }

    public int[] getMenuSlots(String path) {
        if (Objects.requireNonNull(getString(path)).contains("-")) {
            String[] split = Objects.requireNonNull(getString(path)).split("-");
            if (split.length == 2) {
                int start = StringUtil.getInteger(split[0], -1);
                int end = StringUtil.getInteger(split[1], -1);
                List<Integer> current = new ArrayList<>();
                if (start >= 0 && start < 54 && end > 0 && end <= 54) {
                    for (int i = start; i <= end; i++) {
                        current.add(i);
                    }
                    return current.stream().mapToInt(i -> i).toArray();
                }
            }
        }
        return getIntArray(path);
    }


}