package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.FileUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchematicModule extends AbstractModule {
    final SchematicHandler handler;

    boolean generated;
    List<File> schematicFiles;
    List<String> schematics;
    File cachedSchematicFile;

    public SchematicModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
        this.handler = plugin().getSchematicHandler();
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        generated = false;
        schematicFiles = new ArrayList<>();
        schematics = getDungeon().getSchematicSettings().getSchematics();
        setSchematicFiles(schematics.stream().map(this::getFileByName).collect(Collectors.toList()));

        for (File schematicFile : new ArrayList<>(getSchematicFiles())) {
            if (schematicFile != null && !schematicFile.exists()) {
                try {
                    String filePath = Config.DIR_SCHEMATICS + schematicFile.getName();
                    File file = new File(plugin().getDataFolder() + filePath);
                    if (FileUtil.create(file)) {
                        InputStream input = plugin().getClass().getResourceAsStream(filePath);
                        if (input != null) FileUtil.copy(input, file);

                    }
                } catch (Exception exception) {
                    DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while loading schematic file", exception);
                }
            }
        }

        return aBoolean -> {
            Optional<SpawnModule> spawnModule = getManager().getModule(SpawnModule.class);
            DungeonStage dungeonStage = getDungeon().getStage();
            GenerationSettings generationSettings = getDungeon().getGenerationSettings();
            GenerationType generationType = generationSettings.getGenerationType();
            Optional<Location> spawnLocation = generationSettings.getSpawnLocation();

            boolean allowedWithGeneration = !generationType.isDynamic() && spawnLocation.isPresent();
            boolean hasSchematic = handler != null && !getSchematicFiles().isEmpty() && !schematics.isEmpty();
            boolean stageAllowed = dungeonStage.isCheck() || dungeonStage.isPrepare();
            boolean spawnModuleIsReady = spawnModule.isPresent() && spawnModule.get().isSpawned();

            if (allowedWithGeneration && hasSchematic) return true;
            return hasSchematic && stageAllowed && spawnModuleIsReady && !isGenerated();

        };
    }

    @Override
    protected void onShutdown() {
        generated = false;
        getDungeon().getThreadSync().sync(() -> handler.undo(getDungeon())).exceptionally(throwable -> {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while trying undo schematic", throwable);
            return null;
        });
        cachedSchematicFile = null;
    }

    @Override
    public boolean onActivate(boolean force) {
        Location location = getDungeon().getLocation().orElse(null);
        if (location == null) {
            error("Cannot found Dungeon Location!");
            return false;
        }

        cachedSchematicFile = Rnd.get(getSchematicFiles());
        return getDungeon().getThreadSync().syncApply(() -> {
            return generated = handler.paste(getDungeon(), cachedSchematicFile);
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got an exception while trying paste schematic", throwable);
            return false;
        }).join();
    }

    @Override
    public boolean onDeactivate(boolean force) {
        GenerationSettings generationSettings = getDungeon().getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();
        Optional<Location> spawnLocation = generationSettings.getSpawnLocation();
        Optional<ChestModule> module = getManager().getModule(ChestModule.class);

        if (!generationType.isDynamic() && spawnLocation.isPresent()) return false;
        if (module.isPresent() && !module.get().tryDeactivate(ActionType.FORCE)) return false;

        if (!generated) return true;

        generated = false;
        cachedSchematicFile = null;

        getDungeon().getThreadSync().sync(() -> handler.undo(getDungeon()));
        return true;
    }


    public void setSchematicFiles(@NotNull List<File> schematicFiles) {
        this.schematicFiles = schematicFiles;
    }

    @NotNull
    public File getFileByName(@NotNull String name) {
        if (!name.endsWith(".schem")) name = name + ".schem";

        return new File(plugin().getDataFolder() + Config.DIR_SCHEMATICS + name);
    }
}
