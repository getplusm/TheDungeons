package t.me.p1azmer.plugin.dungeons.dungeon.modules;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.*;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleManager extends AbstractManager<DungeonPlugin> {
    Dungeon dungeon;
    LocationGenerator locationGenerator;
    LinkedHashMap<String, AbstractModule> modules = new LinkedHashMap<>();

    public ModuleManager(@NotNull Dungeon dungeon, @NotNull LocationGenerator locationGenerator) {
        super(dungeon.plugin());
        this.dungeon = dungeon;
        this.locationGenerator = locationGenerator;
    }

    @Override
    protected void onLoad() {
        this.plugin.info("Loading modules for " + this.getDungeon().getId() + "..");
//        this.register(ModuleId.BOSSBAR, id -> new BossBarModule(this.getDungeon(), id));
        this.register(ModuleId.SPAWN, id -> new SpawnModule(this.getDungeon(), id, locationGenerator));
        if (plugin.getSchematicHandler() != null)
            this.register(ModuleId.SCHEMATIC, id -> new SchematicModule(this.getDungeon(), id));
        this.register(ModuleId.CHEST, id -> new ChestModule(this.getDungeon(), id));
        if (plugin.getHologramHandler() != null)
            this.register(ModuleId.HOLOGRAM, id -> new HologramModule(this.getDungeon(), id));
        this.register(ModuleId.ANNOUNCE, id -> new AnnounceModule(this.getDungeon(), id));
        this.register(ModuleId.COMMAND, id -> new CommandModule(this.getDungeon(), id));
        this.register(ModuleId.MOBS, id -> new MobModule(this.getDungeon(), id));
        this.plugin.info("Loaded " + this.getModules().size() + " modules for " + this.getDungeon().getId() + " dungeon.");
    }

    @Override
    protected void onShutdown() {
        this.getModules().forEach(AbstractModule::shutdown);
        this.modules.clear();
    }

    private <T extends AbstractModule> void register(@NotNull String id, @NotNull Function<String, T> supplier) {
        id = id.toLowerCase();

        if (this.getModule(id) != null) {
            this.plugin.error("Could not register " + id + " module! Module with such id is already registered!");
            return;
        }

        // Init module.
        T module = supplier.apply(id);

        if (!this.getDungeon().getModuleSettings().isEnabled(id)) return;

        module.setup();
        this.modules.put(module.getId(), module);
    }

    @NotNull
    public Dungeon getDungeon() {
        return dungeon;
    }

    @NotNull
    public <T extends AbstractModule> Optional<T> getModule(@NotNull Class<T> clazz) {
        for (AbstractModule module : this.getModules()) {
            if (clazz.isAssignableFrom(module.getClass())) {
                return Optional.of(clazz.cast(module));
            }
        }
        return Optional.empty();
    }

    @Nullable
    public AbstractModule getModule(@NotNull String id) {
        return this.modules.get(id.toLowerCase());
    }

    @NotNull
    public Collection<AbstractModule> getModules() {
        return this.modules.values();
    }

    @NotNull
    public Collection<AbstractModule> getActive() {
        return this.modules
                .values()
                .stream()
                .filter(AbstractModule::isActivated)
                .collect(Collectors.toList());
    }

    public long getImportantActiveCount() {
        return this.getActive()
                .stream()
                .filter(AbstractModule::isImportantly)
                .count();
    }

    public boolean allImportantActive() {
        return this.getImportantActiveCount() == this.getActive().size();
    }
}
