package t.me.p1azmer.plugin.dungeons.dungeon.region;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.List;

@Getter
@Setter
public class Region extends AbstractSettings {

    private final PlaceholderMap placeholderMap;

    private boolean enabled;
    private String name;
    private int radius;
    private List<String> flags;

    // cache
    private boolean created = false;

    public Region(
            @NotNull Dungeon dungeon,
            boolean enabled,
            @NotNull String name,
            int radius,
            @NotNull List<String> flags
    ) {
        super(dungeon);
        this.enabled = enabled;
        this.name = name;
        this.radius = radius;
        this.flags = flags;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REGION_NAME, this::getName)
                .add(Placeholders.REGION_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.REGION_FLAGS, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join("\n", this.getFlags())))
                .add(Placeholders.REGION_RADIUS, () -> String.valueOf(this.getRadius()))
        ;
    }

    @NotNull
    public static Region read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        String name = cfg.getString(path + ".Name", "plazmer");
        boolean enabled = cfg.getBoolean(path + ".Enabled", true);
        int radius = cfg.getInt(path + ".Radius", 15);
        List<String> flags = cfg.getStringList(path + ".Flags");
        return new Region(dungeon, enabled, name, radius, flags);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        cfg.set(path + ".Name", this.getName());
        cfg.set(path + ".Radius", this.getRadius());
        cfg.set(path + ".Flags", this.getFlags());
    }

    @NotNull
    public String getNameRaw() {
        return this.dungeon().getId() + "_" + this.getName() + "_" + this.dungeon().plugin().getName();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
