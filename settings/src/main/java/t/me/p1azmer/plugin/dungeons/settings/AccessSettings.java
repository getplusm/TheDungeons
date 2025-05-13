package t.me.p1azmer.plugin.dungeons.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.AbstractSetting;

import java.util.Set;

@Getter
@NoArgsConstructor
@ConfigSerializable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class AccessSettings extends AbstractSetting<AccessSettings> {

    boolean enabled = false;
    Set<String> proSkillClasses = Set.of("Warrior", "Paladin", "Ranger", "Mage", "Bard", "Priest", "Monk", "Druid", "Assassin");
    Component cantAccessMessage = Component.text("You cannot access this dungeon!", NamedTextColor.RED);

    public AccessSettings(String baseFilePath) {
        super(baseFilePath, "access");
    }
}
