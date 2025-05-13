package t.me.p1azmer.plugin.dungeons.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;

import java.util.List;

@Getter
@NoArgsConstructor
@ConfigSerializable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class RegionSettings extends AbstractSetting<RegionSettings> {

    boolean enabled = true;
    String regionIdPrefix = "dungeon_region_";
    int radius = 15;
    List<String> flags = List.of(
            "pistons deny",
            "pvp allow",
            "use allow",
            "chest-access allow"
    );

    public RegionSettings(String baseFilePath) {
        super(baseFilePath, "region");
    }
}
