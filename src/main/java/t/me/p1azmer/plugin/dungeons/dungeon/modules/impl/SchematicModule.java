package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.FileUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.api.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;

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
    private File cachedSchematicFile;
    private SchematicHandler handler;

    public SchematicModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
        this.handler = plugin().getSchematicHandler();
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.generated = false;
        this.schematicFiles = new ArrayList<>();
        this.schematics = this.dungeon().getSchematicSettings().getSchematics();
        this.setSchematicFiles(schematics.stream().map(this::getFileByName).collect(Collectors.toList()));

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
        return aBoolean -> handler != null && !this.getSchematicFiles().isEmpty() && !this.schematics.isEmpty()
                && (dungeon().getStage().isCheck() || dungeon().getStage().isPrepare()) && dungeon().getModuleManager().getModule(SpawnModule.class).isPresent()
                && dungeon().getModuleManager().getModule(SpawnModule.class).get().isSpawned() && !isGenerated();
    }

    @Override
    protected void onShutdown() {
    }

    @Override
    public boolean onActivate() {
        Location location = dungeon().getLocation();
        if (location == null) {
            this.error("Dungeon Location not found or generated!");
            return false;
        }

        this.cachedSchematicFile = Rnd.get(this.getSchematicFiles());
        return this.generated = handler.paste(this.dungeon(), this.cachedSchematicFile);
    }

    @Override
    public boolean onDeactivate() {
        if (dungeon().getModuleManager().getModule(ChestModule.class).isPresent() && !dungeon().getModuleManager().getModule(ChestModule.class).get().onDeactivate()) return false;

        if (!this.generated) {
            return true; // return true btw
        }
        this.generated = false;
        handler.undo(this.dungeon());
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
}
