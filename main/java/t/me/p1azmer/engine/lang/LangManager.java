package t.me.p1azmer.engine.lang;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.editor.EditorButtonType;
import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Reflex;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.lang.reflect.Field;
import java.util.*;

public class LangManager<P extends NexPlugin<P>> extends AbstractManager<P> {

    protected JYML config;
    protected final Map<String, LangMessage> messages;
    protected final Map<String, String> placeholders;

    public static final String DIR_LANG = "/lang/";

    public LangManager(@NotNull P plugin) {
        super(plugin);
        this.messages = new HashMap<>();
        this.placeholders = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getConfigManager().extractResources(DIR_LANG);
        this.config = JYML.loadOrExtract(plugin, DIR_LANG + "messages_" + plugin.getConfigManager().languageCode + ".yml");
        this.plugin.info("Using '" + plugin.getConfigManager().languageCode + "' language.");

        this.getConfig().getSection("Placeholders").forEach(placeholder -> {
            this.placeholders.put(placeholder, this.getConfig().getString("Placeholders." + placeholder, ""));
        });

        if (this.plugin.isEngine()) {
            this.setupEnum(EntityType.class);
            this.setupEnum(Material.class);
            this.setupEnum(GameMode.class);

            for (PotionEffectType type : PotionEffectType.values()) {
                this.getConfig().addMissing("PotionEffectType." + type.getName(), StringUtil.capitalizeUnderscored(type.getName()));
            }
            for (Enchantment enchantment : Enchantment.values()) {
                getEnchantment(enchantment);
            }
            for (World world : this.plugin.getServer().getWorlds()) {
                getWorld(world);
            }
            this.getConfig().saveChanges();
        }
        else {
            EngineCore.get().getLangManager().getMessages().forEach((key, message) -> {
                this.getMessages().put(key, new LangMessage(this.plugin, message.getRaw()));
            });
        }
    }

    @Override
    protected void onShutdown() {
        this.messages.clear();
        this.placeholders.clear();
    }

    @NotNull
    public JYML getConfig() {
        return config;
    }

    @NotNull
    public Map<String, LangMessage> getMessages() {
        return messages;
    }

    @NotNull
    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    @NotNull
    public LangMessage getMessage(@NotNull LangKey key) {
        LangMessage message = this.getMessages().get(key.getPath());
        if (message == null) {
            message = this.loadMessage(key);
        }
        return message;
    }

    @NotNull
    public Optional<String> getMessage(@NotNull String path) {
        return Optional.ofNullable(this.getConfig().getString(path)).map(Colorizer::apply);
    }

    public void loadEditor(@NotNull Class<?> clazz) {
        for (Field field : Reflex.getFields(clazz)) {
            if (!EditorLocale.class.isAssignableFrom(field.getType())) {
                continue;
            }

            EditorLocale locale;
            try {
                locale = (EditorLocale) field.get(this);
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // Do not load/set messages of super class(es) or if they are already present in the lang file.
            if (!field.getDeclaringClass().equals(clazz)) {
                continue;
            }

            this.read(locale);
        }
        this.getConfig().saveChanges();
    }

    @NotNull
    private LangMessage loadMessage(@NotNull LangKey key) {
        if (this.write(key)) {
            this.getConfig().saveChanges();
        }

        List<String> list = this.getConfig().getStringList(key.getPath());
        String text = !list.isEmpty() ? String.join("\\n", list) : this.getConfig().getString(key.getPath(), "<Missing Message [" + key.getPath() + "]>");
        LangMessage message = new LangMessage(plugin, text);
        this.getMessages().put(key.getPath(), message);

        return message;
    }

    /**
     * Loads and sets missing messages from the specified class.
     * This method is used to generate the default language file or add new messages to it.
     * @param clazz A class to load LangKey messages from.
     */
    public void loadMissing(@NotNull Class<?> clazz) {
        for (Field field : Reflex.getFields(clazz)) {
            if (!LangKey.class.isAssignableFrom(field.getType())) {
                continue;
            }

            LangKey langKey;
            try {
                langKey = (LangKey) field.get(this);
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // Do not load/set messages of super class(es) or if they are already present in the lang file.
            if (!field.getDeclaringClass().equals(clazz)) {
                continue;
            }

            // Clear old loaded messages.
            this.getMessages().remove(langKey.getPath());

            // For engine, we have to preload the message, so it can be added to child plugin's messages.
            if (this.plugin.isEngine()) {
                this.loadMessage(langKey);
            }
            else { // For child plugins, we can only write it to the config and not precache it.
                this.write(langKey);
            }
        }
        this.getConfig().saveChanges();
    }

    private boolean write(@NotNull LangKey key) {
        if (!this.getConfig().contains(key.getPath())) {
            String textDefault = key.getDefaultText();
            String[] textSplit = textDefault.split("\n");
            this.getConfig().set(key.getPath(), textSplit.length > 1 ? Arrays.asList(textSplit) : textDefault);
            return true;
        }
        return false;
    }

    private void read(@NotNull EditorLocale locale) {
        if (!this.getConfig().contains(locale.getKey())) {
            this.getConfig().set(locale.getKey() + ".Name", locale.getName());
            this.getConfig().set(locale.getKey() + ".Lore", locale.getLore());
        }
        locale.setLocalizedName(this.getConfig().getString(locale.getKey() + ".Name", locale.getName()));
        locale.setLocalizedLore(this.getConfig().getStringList(locale.getKey() + ".Lore"));
    }

    public void setupEnum(@NotNull Class<? extends Enum<?>> clazz) {
        if (!clazz.isEnum()) return;
        for (Object eName : clazz.getEnumConstants()) {
            String name = eName.toString();
            if (clazz == Material.class && name.startsWith("LEGACY")) continue;

            String path = clazz.getSimpleName() + "." + name;
            String val = StringUtil.capitalizeUnderscored(name);
            this.getConfig().addMissing(path, val);
        }
    }

    @NotNull
    public String getEnum(@NotNull Enum<?> e) {
        String path = e.getDeclaringClass().getSimpleName() + "." + e.name();
        String locEnum = this.getMessage(path).orElse(null);
        if (locEnum == null && !this.plugin.isEngine()) {
            return NexPlugin.getEngine().getLangManager().getEnum(e);
        }
        return locEnum == null ? "null" : locEnum;
    }

    @Deprecated
    public void setupEditorEnum(@NotNull Class<? extends Enum<? extends EditorButtonType>> clazz) {
        if (!clazz.isEnum()) return;
        for (Object eName : clazz.getEnumConstants()) {
            if (!(eName instanceof EditorButtonType)) continue;
            EditorButtonType buttonType = (EditorButtonType) eName;
            if (buttonType.getMaterial().equals(Material.AIR)) continue;

            String nameRaw = buttonType.name();
            String path = "Editor." + clazz.getSimpleName() + "." + nameRaw + ".";

            this.getConfig().addMissing(path + "Name", buttonType.getName());
            this.getConfig().addMissing(path + "Lore", buttonType.getLore());

            buttonType.setName(this.getConfig().getString(path + "Name", nameRaw));
            buttonType.setLore(this.getConfig().getStringList(path + "Lore"));
        }
    }

    @NotNull
    public static String getPotionType(@NotNull PotionEffectType type) {
        return EngineCore.get().getLangManager().getMessage("PotionEffectType." + type.getName()).orElse(type.getName());
    }

    @NotNull
    public static String getEntityType(@NotNull EntityType type) {
        return EngineCore.get().getLangManager().getEnum(type);
    }

    @NotNull
    public static String getMaterial(@NotNull Material type) {
        return EngineCore.get().getLangManager().getEnum(type);
    }

    @NotNull
    public static String getWorld(@NotNull World world) {
        return getByObject(world.getName(), "World");
    }

    @NotNull
    public static String getEnchantment(@NotNull Enchantment enchantment) {
        return getByObject(enchantment.getName(), "Enchantment");
    }

    @NotNull
    private static String getByObject(@NotNull String nameRaw, @NotNull String path) {
        LangManager<DungeonPlugin> manager = EngineCore.get().getLangManager();

        manager.getConfig().addMissing(path + "." + nameRaw, StringUtil.capitalizeUnderscored(nameRaw));
        manager.getConfig().saveChanges();

        return manager.getMessage(path + "." + nameRaw).orElse(nameRaw);
    }

    @NotNull
    public static String getBoolean(boolean b) {
        return EngineCore.get().getLangManager().getMessage(b ? CoreLang.OTHER_YES : CoreLang.OTHER_NO).getLocalized();
    }

    @NotNull
    public static String getPlain(@NotNull LangKey key) {
        return EngineCore.get().getLangManager().getMessage(key).getLocalized();
    }

    @NotNull
    public static String getMaterial(@NotNull String materialName) {
        Material material = CollectionsUtil.getEnum(materialName, Material.class);
        return getMaterial(material == null ? Material.AIR : material);
    }
}
