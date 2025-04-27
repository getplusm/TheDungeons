package t.me.p1azmer.plugin.dungeons.dungeon.module;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.module.modules.*;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleManager extends AbstractManager<DungeonPlugin> {
    Dungeon dungeon;
    LocationGenerator locationGenerator;

    LinkedHashMap<String, AbstractModule> modules = new LinkedHashMap<>();
    Map<Class<?>, AbstractModule> cachedModuleRequest = new HashMap<>();

    public ModuleManager(@NotNull Dungeon dungeon, @NotNull LocationGenerator locationGenerator) {
        super(dungeon.plugin());
        this.dungeon = dungeon;
        this.locationGenerator = locationGenerator;
    }

    @Override
    protected void onLoad() {
        try {
            plugin.info("Loading modules for " + this.getDungeon().getId() + "..");
            register(ModuleId.SPAWN, id -> new SpawnModule(this.getDungeon(), id, locationGenerator));
            register(ModuleId.SCHEMATIC, id -> new SchematicModule(this.getDungeon(), id));
            register(ModuleId.CHEST, id -> new ChestModule(this.getDungeon(), id));
            if (plugin.getHologramHandler() != null) {
                this.register(ModuleId.HOLOGRAM, id -> new HologramModule(this.getDungeon(), id));
            }
            register(ModuleId.ANNOUNCE, id -> new AnnounceModule(this.getDungeon(), id));
            register(ModuleId.COMMAND, id -> new CommandModule(this.getDungeon(), id));
            register(ModuleId.MOBS, id -> new MobModule(this.getDungeon(), id));
            plugin.info("Loaded " + this.getModules().size() + " modules for " + this.getDungeon().getId() + " dungeon.");
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while loading modules for " + this.getDungeon().getId(), exception);
        }
    }

    @Override
    protected void onShutdown() {
        this.getModules().forEach(AbstractModule::shutdown);
        this.modules.clear();
        cachedModuleRequest.clear();
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

    public <T extends AbstractModule> Optional<T> getModule(@NotNull Class<T> clazz) {
        return Optional.ofNullable((T) cachedModuleRequest.computeIfAbsent(clazz, k -> {
            for (AbstractModule module : this.getModules()) {
                if (clazz.isAssignableFrom(module.getClass())) {
                    return clazz.cast(module);
                }
            }
            return null;
        }));
    }

    @Nullable
    public AbstractModule getModule(@NotNull String id) {
        return this.modules.get(id.toLowerCase());
    }

    @NotNull
    public Collection<AbstractModule> getModules() {
        return this.modules.values();
    }

    public @NotNull Collection<AbstractModule> getActiveModules() {
        return getModules().stream().filter(AbstractModule::isActivated).collect(Collectors.toList());
    }
}
