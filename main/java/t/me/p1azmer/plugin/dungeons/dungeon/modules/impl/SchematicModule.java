package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.FileUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SchematicModule extends AbstractModule {
    private boolean generated;
    private List<File> schematicFiles;
    private List<String> schematics;

    // cache
    private File cachedSchematicFile;

    public SchematicModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
        this.placeholderMap.add(Placeholders.DUNGEON_SCHEMATICS, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join("\n", this.schematics)));
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.generated = false;
        this.schematicFiles = new ArrayList<>();
        this.schematics = new ArrayList<>();
        this.schematics = cfg.getStringList("Settings.Modules." + this.getId() + ".List");

        if (cfg.contains("Schematic.List")) {
            this.schematics.addAll(cfg.getStringList("Schematic.List"));
            cfg.remove("Schematic.List");
            cfg.remove("Schematic.Random");
        }
        if (this.schematics.isEmpty()) {
            plugin().error("The '" + dungeon().getId() + "' dungeon schematics list is empty. It cannot spawn in a world without schematics. If you don't need the module, disable it!");
        }
        this.setSchematics(this.schematics);
        for (File schematicFile : new ArrayList<>(this.getSchematicFiles())) {
            if (schematicFile != null && !schematicFile.exists()) {
                try {
                    String filePath = Config.DIR_SCHEMATICS + schematicFile.getName();
                    File file = new File(plugin().getDataFolder() + filePath);
                    if (FileUtil.create(file)) {
                        try {
                            InputStream input = plugin().getClass().getResourceAsStream(filePath);
                            if (input != null) FileUtil.copy(input, file);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return aBoolean -> !this.schematics.isEmpty() && dungeon().getStage().isCheck() && dungeon().getModuleManager().getModule(SpawnModule.class).isPresent()
                && dungeon().getModuleManager().getModule(SpawnModule.class).get().isSpawned() && !isGenerated();
    }

    @Override
    protected void onShutdown() {
        cfg.set("Settings.Modules." + this.getId() + ".List", this.schematics);
        cfg.saveChanges();
    }

    @Override
    public boolean onActivate() {
        Location location = dungeon().getLocation();
        if (location == null) {
            plugin().error("schem return false but location is null");
            return false;
        }

        this.cachedSchematicFile = Rnd.get(this.getSchematicFiles());
        return this.generated = this.dungeonManager().plugin().getSchematicHandler().paste(this.dungeon(), this.cachedSchematicFile);
    }

    @Override
    public boolean onDeactivate() {
        if (!this.generated) {
            return true; // return true btw
        }
        this.generated = false;
        this.dungeonManager().plugin().getSchematicHandler().undo(this.dungeon());
        this.cachedSchematicFile = null;
        return true;
    }

    public boolean isGenerated() {
        return generated;
    }

    @NotNull
    public List<File> getSchematicFiles() {
        return schematicFiles;
    }

    @NotNull
    public List<String> getSchematics() {
        return schematics;
    }

    public void setSchematicFiles(@NotNull List<File> schematicFiles) {
        this.schematicFiles = schematicFiles;
    }

    @NotNull
    public File getFileByName(@NotNull String name) {
        if (!name.endsWith(".schem")) {
            name = name + ".schem";
        }
        return new File(plugin().getDataFolder() + Config.DIR_SCHEMATICS + name);
    }

    @Nullable
    public File getCachedSchematicFile() {
        return cachedSchematicFile;
    }

    public void setSchematics(@NotNull List<String> schematics) {
        this.schematics = schematics;
        this.setSchematicFiles(schematics.stream().map(this::getFileByName).collect(Collectors.toList()));
        cfg.set("Settings.Modules." + this.getId() + ".List", this.schematics);
        cfg.saveChanges();
    }

    public void addSchematic(@NotNull String schematic) {
        if (!schematic.endsWith(".schem")) schematic = schematic + ".schem";

        File file = this.getFileByName(schematic);
        this.getSchematicFiles().add(file);

    }
}
