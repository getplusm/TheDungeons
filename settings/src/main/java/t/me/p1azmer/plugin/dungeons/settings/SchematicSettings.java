package t.me.p1azmer.plugin.dungeons.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@ConfigSerializable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class SchematicSettings extends AbstractSetting<SchematicSettings> {

    List<String> schematics = List.of("dungeon_rotten_mushroom.schem");
    boolean ignoreAirBlocks = true;
    boolean underground = false;

    public SchematicSettings(String baseFilePath) {
        super(baseFilePath, "schematic");
    }
}
