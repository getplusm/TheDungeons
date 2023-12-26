package t.me.p1azmer.plugin.dungeons.dungeon.modules;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.function.Predicate;

public abstract class AbstractModule implements IPlaceholderMap {
    private final Dungeon dungeon;
    private final String id;
    private final String name;
    private final boolean updatable, importantly;
    private final DungeonManager manager;
    private Predicate<Boolean> canEnable;
    protected final JYML cfg;
    protected PlaceholderMap placeholderMap;

    // cache
    private boolean active;

    public AbstractModule(@NotNull Dungeon dungeon, @NotNull String id, @NotNull Predicate<Boolean> canEnable, boolean updatable, boolean importantly) {
        this.dungeon = dungeon;
        this.id = id;
        this.name = StringUtil.capitalizeUnderscored(id);
        this.updatable = updatable;
        this.importantly = importantly;
        this.manager = dungeon.getManager();
        this.canEnable = canEnable;
        this.cfg = dungeon().getConfig();
        this.placeholderMap = new PlaceholderMap();
    }

    public AbstractModule(@NotNull Dungeon dungeon, @NotNull String id, boolean updatable, boolean importantly) {
        this(dungeon, id, aBoolean -> true, updatable, importantly);
    }

    public AbstractModule(@NotNull Dungeon dungeon, @NotNull String id, boolean updatable, @NotNull Predicate<Boolean> canEnable) {
        this(dungeon, id, canEnable, updatable, false);
    }

    public AbstractModule(@NotNull Dungeon dungeon, @NotNull String id, boolean updatable) {
        this(dungeon, id, aBoolean -> true, updatable, false);
    }

    public void setup() {
        this.canEnable = this.onLoad();
    }

    public void shutdown() {
        this.deactivate();
        this.onShutdown();
    }

    protected abstract Predicate<Boolean> onLoad();

    protected abstract void onShutdown();

    public boolean activate() {
        this.active = this.canEnable.test(true);
        return this.active && this.onActivate();
    }

    public boolean deactivate() {
        this.active = false;
        return this.onDeactivate();
    }

    protected abstract boolean onActivate();

    protected abstract boolean onDeactivate();

    public void update() {
        if (isActive() && !isUpdatable()) return;
        boolean canActivate = canEnable.test(true);
        if (canActivate && !isActive()) {
            if (!activate() && isImportantly()) {
                this.plugin().error("cannot continue this '" + this.dungeon().getId() + "' dungeon because an important module '"+this.getName()+"' failed to start!");
                this.dungeon().cancel(false); // TODO add cancelled all modules to cancel method
            }
        } else if (!canActivate && isActive()) {
            this.plugin().error("Deactivate dungeon '" + this.dungeon().getId() + "' with module '" + this.getId() + "'");
            deactivate();
        }
    }

    @NotNull
    public Dungeon dungeon() {
        return dungeon;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public JYML getConfig() {
        return cfg;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public boolean isImportantly() {
        return importantly;
    }

    public boolean isActive() {
        return active;
    }

    @NotNull
    public DungeonManager dungeonManager() {
        return manager;
    }

    @NotNull
    public DungeonPlugin plugin() {
        return dungeon().plugin();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
