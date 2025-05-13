package t.me.p1azmer.plugin.dungeons.modules;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.dungeons.api.integrations.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.core.common.file.FileUtils;
import t.me.p1azmer.plugin.dungeons.settings.SchematicSettings;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchematicModule extends AbstractModule {

    private static final String SCHEMATIC_PATH = "/schematics/";
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    Dungeon dungeon;
    SchematicHandler schematicHandler;
    List<File> schematicFiles;

    public SchematicModule(Dungeon dungeon, @Nullable SchematicHandler schematicHandler) {
        super(false);
        this.dungeon = dungeon;
        this.schematicHandler = schematicHandler;

        SchematicSettings settings = dungeon.getSettingsContainer().getOrThrow(SchematicSettings.class);
        this.schematicFiles = settings.getSchematics().stream().map(file -> getFileByName(dungeon, file)).collect(Collectors.toList());
        for (File schematicFile : schematicFiles) {
            if (schematicFile != null && !schematicFile.exists()) {
                try {
                    String filePath = SCHEMATIC_PATH + schematicFile.getName();
                    File file = new File(dungeon.getDataFolder() + filePath);
                    if (FileUtils.create(file)) {
                        InputStream input = dungeon.getDataFolder().getClass().getResourceAsStream(filePath);
                        if (input != null) FileUtils.copy(input, file);

                    }
                } catch (Exception exception) {
                    getLog().log(Level.SEVERE, "Got an exception while loading schematic file", exception);
                }
            }
        }
    }

    @Override
    public Predicate<Boolean> isCanEnabled() {
        return aBoolean -> {
            if (schematicHandler == null) return false;

            boolean equals = dungeon.getState().equals(State.GENERATION) && dungeon.getLocation() != null;
            Logger.getGlobal().severe(getClass().getSimpleName() + " can enabled:" + equals);
            return equals;
        };
    }

    @Override
    public Predicate<Boolean> isCanDisabled() {
        return aBoolean -> dungeon.getLocation() == null
                || dungeon.getState().equals(State.DELETING)
                || dungeon.getState().equals(State.CANCELLED)
                || dungeon.getState().equals(State.REBOOTED);
    }

    @Override
    public void initialize() {
        for (File schematicFile : schematicFiles) {
            if (schematicFile != null && !schematicFile.exists()) {
                try {
                    String filePath = SCHEMATIC_PATH + schematicFile.getName();
                    File file = new File(dungeon.getDataFolder() + filePath);
                    if (FileUtils.create(file)) {
                        InputStream input = dungeon.getDataFolder().getClass().getResourceAsStream(filePath);
                        if (input != null) FileUtils.copy(input, file);

                    }
                } catch (Exception exception) {
                    getLog().log(Level.SEVERE, "Got an exception while loading schematic file", exception);
                }
            }
        }
    }

    @Override
    public void startup() {
        try {
            File schematicFile = schematicFiles.get(RANDOM.nextInt(schematicFiles.size()));
            schematicHandler.paste(dungeon, schematicFile);
            getLog().severe("pasted");
        } catch (Exception exception) {
            dungeon.reboot();
            getLog().log(Level.SEVERE, "Got an exception while paste schematic file, rebooted dungeon", exception);
        }
    }

    @Override
    public void shutdown() {
        schematicHandler.undo(dungeon);
        getLog().info("shutdown");
    }

    private static @NotNull File getFileByName(Dungeon dungeon, String name) {
        if (!name.endsWith(".schem")) name = name + ".schem";

        return new File(dungeon.getDataFolder() + SCHEMATIC_PATH + name);
    }
}
