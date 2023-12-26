package t.me.p1azmer.plugin.dungeons.dungeon.modules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ModuleManager extends AbstractManager<DungeonPlugin> {
    private final Dungeon dungeon;
    private final Map<String, AbstractModule> modules = new LinkedHashMap<>();

    public ModuleManager(@NotNull Dungeon dungeon) {
        super(dungeon.plugin());
        this.dungeon = dungeon;
    }

    @Override
    protected void onLoad() {
        this.plugin.info("Loading modules for " + this.getDungeon().getName() + "..");
//        this.register(ModuleId.BOSSBAR, id -> new BossBarModule(this.getDungeon(), id));
//        this.register(ModuleId.COMMAND, id -> new CommandModule(this.getDungeon(), new HashMap<>(), id));
        this.register(ModuleId.SPAWN, id -> new SpawnModule(this.getDungeon(), id));
        this.register(ModuleId.SCHEMATIC, id -> new SchematicModule(this.getDungeon(), id));
        this.register(ModuleId.CHEST, id -> new ChestModule(this.getDungeon(), id));
        this.register(ModuleId.HOLOGRAM, id -> new HologramModule(this.getDungeon(), id));
        //this.register(ModuleId.ANNOUNCE, id -> new AnnounceModule(this.getDungeon(), id));
        this.plugin.info("Loaded " + this.getModules().size() + " modules for " + this.getDungeon().getName() + " dungeon.");
    }

    @Override
    protected void onShutdown() {
        this.getModules().forEach(AbstractModule::shutdown);
        this.modules.clear();
    }

    private <T extends AbstractModule> boolean register(@NotNull String id, @NotNull Function<String, T> supplier) {
        id = id.toLowerCase();
        long loadTook = System.currentTimeMillis();

        if (this.getModule(id) != null) {
            this.plugin.error("Could not register " + id + " module! Module with such id is already registered!");
            return false;
        }

        // Init module.
        T module = supplier.apply(id);

        if (!this.getDungeon().getModuleSettings().isEnabled(id)) {
            return false;
        }

        module.setup();
        loadTook = System.currentTimeMillis() - loadTook;
        this.plugin.info("Loaded module: " + module.getName() + " in " + loadTook + " ms.");
        this.modules.put(module.getId(), module);
        return true;
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
}
