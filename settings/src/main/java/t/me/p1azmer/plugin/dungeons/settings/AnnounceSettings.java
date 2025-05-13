package t.me.p1azmer.plugin.dungeons.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.core.models.Announce;

import java.util.List;

@Getter
@NoArgsConstructor
@ConfigSerializable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class AnnounceSettings extends AbstractSetting<AnnounceSettings> {

    List<AnnounceConfig> announces = List.of(new AnnounceConfig(
            new Announce(Component.text("Dungeon %dungeon_name% generated and spawn at %location_world% %location_x%:%location_y%:%location_z%"), true),
            0, State.GENERATION)
    );

    public AnnounceSettings(String baseFilePath) {
        super(baseFilePath, "announce");
    }

    @Getter
    @NoArgsConstructor
    @ConfigSerializable
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static final class AnnounceConfig {
        Announce announce;
        int notifyTimeInSeconds;
        State state;
    }
}
