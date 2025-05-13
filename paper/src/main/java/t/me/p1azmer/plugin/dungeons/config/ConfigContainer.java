package t.me.p1azmer.plugin.dungeons.config;

import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.config.configs.CommandConfig;
import t.me.p1azmer.plugin.dungeons.config.configs.Config;
import t.me.p1azmer.plugin.dungeons.config.configs.DungeonsConfig;
import t.me.p1azmer.plugin.dungeons.config.configs.Lang;
import t.me.p1azmer.plugin.dungeons.core.common.config.RawConfigContainer;

import java.io.File;

public class ConfigContainer extends RawConfigContainer {
    public ConfigContainer(File path) {
        super(path);
        loadConfigs();
    }

    private void loadConfigs() {
        loadConfigs(
                new Config(toFile("config.yml")),
                new DungeonsConfig(toFile("dungeons.yml")),
                new Lang(toFile("lang.yml")),
                new CommandConfig(toFile("commands.yml"))
        );
    }

    public Config getConfig() {
        return get(Config.class);
    }

    public Lang getLang() {
        return get(Lang.class);
    }

    public CommandConfig getCommandConfig() {
        return get(CommandConfig.class);
    }

    public DungeonsConfig getDungeonsConfig() {
        return get(DungeonsConfig.class);
    }
}
