package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.events.AsyncDungeonSpawnEvent;
import t.me.p1azmer.plugin.dungeons.api.events.DungeonDespawnEvent;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpawnModule extends AbstractModule {
    final LocationGenerator locationGenerator;
    boolean spawned;

    public SpawnModule(@NotNull Dungeon dungeon, @NotNull String id, @NotNull LocationGenerator locationGenerator) {
        super(dungeon, id, false, true);
        this.locationGenerator = locationGenerator;
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.spawned = false;
        return aBoolean -> {
            DungeonStage stage = getDungeon().getStage();
            GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
            GenerationType generationType = generationSettings.getGenerationType();
            Optional<Location> spawnLocation = generationSettings.getSpawnLocation();
            boolean allowedWithGeneration = !generationType.isDynamic() && spawnLocation.isPresent();

            return stage.isCheck() && !isSpawned() || allowedWithGeneration;
        };
    }

    @Override
    protected void onShutdown() {
        this.spawned = false;
        GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();
        if (generationType.isDynamic()) this.getDungeon().setLocation(null);
    }

    @Override
    public boolean onActivate(boolean force) {
        if (isSpawned()) {
            this.debug("Dungeon is already spawned on world");
            return force;
        }

        GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();
        Optional<Location> spawnLocation = generationSettings.getSpawnLocation();

        if (!generationType.isDynamic()) {
            if (spawnLocation.isPresent()) {
                return this.spawn(spawnLocation.get());
            }

            this.error("The location of the dungeon is not set in the generation settings");
            return false;
        }

        try {
            World world = getDungeon().getWorld();
            boolean underground = getDungeon().getSchematicSettings().isUnderground();

            Location location;
            if (underground) location = locationGenerator.getRandomUndergroundLocation(world);
            else location = locationGenerator.getRandomHighLocation(world);

            return spawn(location);
        } catch (RuntimeException exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while trying to spawn the '" + getId() + "' module", exception);
            return false;
        }
    }

    @Override
    public boolean onDeactivate(boolean force) {
        GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();
        Optional<Location> spawnLocation = generationSettings.getSpawnLocation();
        Optional<SchematicModule> schematicModule = this.getManager().getModule(SchematicModule.class);

        if (!generationType.isDynamic() && spawnLocation.isPresent()) return false;
        if (schematicModule.isPresent() && !schematicModule.get().tryDeactivate(ActionType.FORCE)) return false;

        boolean cancelled = getDungeon().getThreadSync().syncApply(() -> {
            DungeonDespawnEvent event = new DungeonDespawnEvent(this.getDungeon());
            Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled();
        }).join();

        if (cancelled) {
            this.debug("Unable to deactivate the '" + this.getId() + "' module due to an Event");
            return false;
        }

        this.spawned = false;
        return true;
    }

    public boolean spawn(@NotNull Location result) {
        AsyncDungeonSpawnEvent event = new AsyncDungeonSpawnEvent(this.getDungeon(), result);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.debug("Unable to spawn the '" + this.getId() + "' module due to an Event");
            return false;
        }

        return this.spawned = true;
    }
}