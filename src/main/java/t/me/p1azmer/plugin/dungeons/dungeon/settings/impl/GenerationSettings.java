package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders;

import java.util.Optional;

@Getter
@Setter
public class GenerationSettings extends AbstractSettings {
    private GenerationType generationType;
    private Location spawnLocation;

    public GenerationSettings(
            @NotNull Dungeon dungeon,
            @NotNull GenerationType generationType,
            @Nullable Location spawnLocation
    ) {
        super(dungeon);
        this.generationType = generationType;
        this.spawnLocation = spawnLocation;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_GENERATION_TYPE, () -> plugin().getLangManager().getEnum(this.getGenerationType()))
                .add(Placeholders.DUNGEON_SETTINGS_GENERATION_LOCATION, () -> {
                    if (this.spawnLocation == null) return Colorizer.apply(Colors2.RED + "X");
                    return Placeholders.forLocation(this.spawnLocation)
                            .apply("%location_world% - %location_x%, %location_y%, %location_z%");
                })
        ;
    }

    @NotNull
    public static GenerationSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        GenerationType generationType = cfg.getEnum(path + ".Type", GenerationType.class, GenerationType.DYNAMIC);
        Location location = cfg.getLocation(path + ".Location");
        return new GenerationSettings(dungeon, generationType, location);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Type", this.getGenerationType().name());
        cfg.set(path + ".Location", this.getSpawnLocation().orElse(null));
    }

    public Optional<Location> getSpawnLocation() {
        return Optional.ofNullable(this.spawnLocation);
    }
}
