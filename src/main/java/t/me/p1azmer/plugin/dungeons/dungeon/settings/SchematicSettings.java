package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.List;
import java.util.stream.Collectors;

public class SchematicSettings extends AbstractSettings implements IPlaceholderMap {
    private List<String> schematics;
    private boolean ignoreAirBlocks;
    private boolean underground;
    private final PlaceholderMap placeholderMap;

    public SchematicSettings(@NotNull Dungeon dungeon, @NotNull List<String> schematics, boolean ignoreAirBlocks, boolean underground) {
        super(dungeon);
        this.setSchematics(schematics);
        this.ignoreAirBlocks = ignoreAirBlocks;
        this.underground = underground;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.SCHEMATICS_LIST, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join("\n" + Colors.LIGHT_PURPLE, this.getSchematics())))
                .add(Placeholders.SCHEMATICS_IGNORE_AIR, () -> LangManager.getBoolean(this.isIgnoreAirBlocks()))
                .add(Placeholders.SCHEMATICS_UNDERGROUND, () -> LangManager.getBoolean(this.isUnderground()))
        ;

    }

    public static SchematicSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        List<String> schematic = cfg.getStringList(path + ".List");
        boolean ignoreAirBlocks = cfg.getBoolean(path + ".Ignore_Air_Blocks");
        boolean underground = cfg.getBoolean(path + ".Underground");
        return new SchematicSettings(dungeon, schematic, ignoreAirBlocks, underground);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".List", this.getSchematics());
        cfg.set(path + ".Ignore_Air_Blocks", this.isIgnoreAirBlocks());
        cfg.set(path + ".Underground", this.isUnderground());
    }

    @NotNull
    public List<String> getSchematics() {
        return schematics;
    }

    public boolean isIgnoreAirBlocks() {
        return ignoreAirBlocks;
    }

    public boolean isUnderground() {
        return underground;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setSchematics(@NotNull List<String> schematics) {
        schematics = schematics.stream().map(founder -> !founder.endsWith(".schem") ? founder + ".schem" : founder).collect(Collectors.toList());
        this.schematics = schematics;
    }

    public void setIgnoreAirBlocks(boolean ignoreAirBlocks) {
        this.ignoreAirBlocks = ignoreAirBlocks;
    }

    public void setUnderground(boolean underground) {
        this.underground = underground;
    }
}
