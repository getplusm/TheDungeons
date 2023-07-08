package t.me.p1azmer.engine.config;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.api.module.AbstractModule;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.engine.utils.ResourceExtractor;
import t.me.p1azmer.engine.utils.StringUtil;

import java.io.File;
import java.io.IOException;

public class ConfigManager<P extends NexPlugin<P>> extends AbstractManager<P> {

    private JYML config;

    public String   pluginName;
    public String   pluginPrefix;
    public String[] commandAliases;
    public String   languageCode;

    public ConfigManager(@NotNull P plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.config = JYML.loadOrExtract(this.plugin, "config.yml");
        //JYML config = this.getConfig();

        this.pluginName = JOption.create("Plugin.Name", plugin.getName(),
                        "Локализованное название плагина. Он используется в сообщениях и с внутренними заполнителями.")
                .read(this.config);
        this.pluginPrefix = JOption.create("Plugin.Prefix", "&6" + Placeholders.Plugin.NAME + " &8» &7",
                        "Префикс плагина. Используется в сообщениях.",
                        "Вы можете использовать " + Placeholders.Plugin.NAME_LOCALIZED + " плейсхолдер для имени плагина.")
                .read(this.config).replace(Placeholders.Plugin.NAME, this.pluginName);
        this.commandAliases = JOption.create("Plugin.Command_Aliases", plugin.getName().toLowerCase(),
                        "Имена команд, которые будут зарегистрированы в качестве основных команд плагина.",
                        "Не оставляйте это пустым. Разделите несколько имен запятой.")
                .read(this.config).split(",");
        this.languageCode = JOption.create("Plugin.Language", "en",
                        "Задает язык плагина.",
                        "Он будет использовать языковую конфигурацию из подпапки "+ LangManager.DIR_LANG +" для указанного языкового кода.",
                        "По умолчанию это 'en', поэтому будет использоваться 'messages_en.yml'.")
                .read(this.config).toLowerCase();

        this.config.saveChanges();
    }

    @Override
    protected void onShutdown() {

    }

    @NotNull
    public JYML getConfig() {
        /*if (this.config == null) {
            this.config = JYML.loadOrExtract(this.plugin, "config.yml");
        }*/
        return this.config;
    }

    @Deprecated
    public final boolean isModuleEnabled(@NotNull AbstractModule<?> module) {
        return this.isModuleEnabled(module.getId());
    }

    @Deprecated
    public final boolean isModuleEnabled(@NotNull String module) {
        this.getConfig().addMissing("Modules." + module + ".Enabled", true);
        this.getConfig().saveChanges();
        return this.getConfig().getBoolean("Modules." + module + ".Enabled");
    }

    @Deprecated
    public final void disableModule(@NotNull AbstractModule<?> module) {
        this.getConfig().set("Modules." + module.getId() + ".Enabled", false);
        this.getConfig().saveChanges();
    }

    @NotNull
    @Deprecated
    public final String getModuleName(@NotNull AbstractModule<?> module) {
        this.getConfig().addMissing("Modules." + module.getId() + ".Name", StringUtil.capitalizeFully(module.getId().replace("_", " ")));
        this.getConfig().saveChanges();
        return this.getConfig().getString("Modules." + module.getId() + ".Name", module.getId());
    }

    public void extractResources(@NotNull String folder) {
        this.extractResources(folder,plugin.getDataFolder() + folder, false);
    }

    public void extractResources(@NotNull String jarPath, @NotNull String toPath) {
        this.extractResources(jarPath, toPath, false);
    }

    public void extractResources(@NotNull String jarPath, @NotNull String toPath, boolean override) {
        File destination = new File(toPath);
        if (destination.exists() && !override) return;

        if (jarPath.startsWith("/")) {
            jarPath = jarPath.substring(1);
        }
        if (jarPath.endsWith("/")) {
            jarPath = jarPath.substring(0, jarPath.length() - 1);
        }

        ResourceExtractor extract = ResourceExtractor.create(plugin, jarPath, destination);
        try {
            extract.extract(override);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}