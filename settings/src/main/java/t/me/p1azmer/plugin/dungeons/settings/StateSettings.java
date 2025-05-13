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
import java.util.Map;

@Getter
@NoArgsConstructor
@ConfigSerializable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class StateSettings extends AbstractSetting<StateSettings> {

    Map<String, Duration> stateTimeMap = Map.of(
            "FREEZE", Duration.ofSeconds(3),
            "CHECK_LOCATION", Duration.ofSeconds(3),
            "GENERATION", Duration.ofSeconds(5),
            "WAITING_OPEN", Duration.ofMinutes(2),
            "OPENED", Duration.ofMinutes(5),
            "DELETING", Duration.ofSeconds(5)
    );

    public StateSettings(String baseFilePath) {
        super(baseFilePath, "state");
    }

    public @NotNull Duration getStateDuration(String stateName) {
        return stateTimeMap.getOrDefault(stateName, Duration.ofSeconds(1));
    }
}
