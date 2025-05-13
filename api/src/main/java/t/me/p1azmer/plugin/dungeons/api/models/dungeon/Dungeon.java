package t.me.p1azmer.plugin.dungeons.api.models.dungeon;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings.container.SettingsContainer;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.types.GenerationType;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.api.models.module.container.ModuleContainer;

import java.io.File;
import java.time.Duration;
import java.util.Optional;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon {

    String id;
    Component name;
    String worldName;
    GenerationType generationType;

    // TODO setup file
    @Setter @NonFinal transient SettingsContainer settingsContainer;
    @Setter @NonFinal transient ModuleContainer moduleContainer;
    @Setter @NonFinal transient State state = State.FREEZE;
    @Setter @NonFinal transient Duration stateSwitchTime = Duration.ZERO;
    @Setter @NonFinal transient Location location;
    @Setter @NonFinal transient File dataFolder;

    public Dungeon(String id, Component name, String worldName, GenerationType generationType) {
        this.id = id;
        this.name = name;
        this.worldName = worldName;
        this.generationType = generationType;
    }

    public void reload() {
        state = State.FREEZE;
        stateSwitchTime = Duration.ZERO;
        settingsContainer.reloadAll();
        moduleContainer.getAll().forEach(AbstractModule::shutdown);

    }

    public World getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldName)).orElseThrow(() -> {
            return new RuntimeException("World" + worldName + " not found");
        });
    }

    public void reboot() {
        state = State.REBOOTED;
    }
}
