package t.me.p1azmer.plugin.dungeons.modules;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.api.models.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;

import java.util.function.Predicate;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpawnModule extends AbstractModule {

    Dungeon dungeon;
    LocationGenerator locationGenerator;

    public SpawnModule(Dungeon dungeon, LocationGenerator locationGenerator) {
        super(false);
        this.dungeon = dungeon;
        this.locationGenerator = locationGenerator;
    }

    @Override
    public Predicate<Boolean> isCanEnabled() {
        return aBoolean -> {
            boolean equals = dungeon.getState().equals(State.GENERATION);
            Logger.getGlobal().severe(getClass().getSimpleName() + " can enabled:" + equals);
            return equals;
        };
    }

    @Override
    public Predicate<Boolean> isCanDisabled() {
        return aBoolean -> dungeon.getState().equals(State.DELETING)
                || dungeon.getState().equals(State.CANCELLED)
                || dungeon.getState().equals(State.REBOOTED);
    }

    @Override
    public void initialize() {
        World world = dungeon.getWorld();
        Location location = locationGenerator.getRandomLocation(world);
        dungeon.setLocation(location);
        Bukkit.broadcast(Component.text(dungeon.getId() + " set location: " + location));
        Logger.getGlobal().severe("SpawnModule initialize:" + dungeon.getLocation());
    }

    @Override
    public void startup() {
        Logger.getGlobal().severe("SpawnModule startup:" + dungeon.getLocation());
    }

    @Override
    public void shutdown() {
        dungeon.setLocation(null);
        Logger.getGlobal().severe("SpawnModule shutdown:" + dungeon.getLocation());
    }
}
