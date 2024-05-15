package t.me.p1azmer.plugin.dungeons.dungeon.modules;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.ModuleSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Getter
public abstract class AbstractModule implements Placeholder {
    private final Dungeon dungeon;
    private final String id;
    private final String name;
    private final boolean updatable, importantly;
    private final DungeonManager dungeonManager;
    private final ModuleManager manager;
    private final ModuleSettings settings;
    private Predicate<Boolean> canEnable;
    protected final JYML cfg;
    protected PlaceholderMap placeholderMap;

    // cache
    private ActionType actionType = ActionType.NATURAL;
    private boolean activated;

    public AbstractModule(
            @NotNull Dungeon dungeon,
            @NotNull String id,
            boolean updatable,
            boolean importantly
    ) {
        this.dungeon = dungeon;
        this.id = id;
        this.name = StringUtil.capitalizeUnderscored(id);
        this.updatable = updatable;
        this.importantly = importantly;
        this.dungeonManager = dungeon.getManager();
        this.manager = dungeon.getModuleManager();
        this.settings = dungeon.getModuleSettings();
        this.cfg = getDungeon().getConfig();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.MODULE_ID, this::getId)
        ;
    }

    public AbstractModule(
            @NotNull Dungeon dungeon,
            @NotNull String id,
            boolean updatable
    ) {
        this(dungeon, id, updatable, false);
    }

    public void setup() {
        this.canEnable = this.onLoad();
    }

    public void shutdown() {
        CompletableFuture.runAsync(() -> this.tryDeactivate(ActionType.FORCE));
        this.onShutdown();
    }

    protected abstract Predicate<Boolean> onLoad();

    protected abstract CompletableFuture<Boolean> onActivate(boolean force);

    protected abstract boolean onDeactivate(boolean force);

    protected abstract void onShutdown();


    public CompletableFuture<Boolean> tryActive(@NotNull AbstractModule.ActionType actionType) {
        if (!this.getSettings().isEnabled(this.getId())) return CompletableFuture.completedFuture(false);

        return switch (actionType) {
            case FORCE -> {
                this.actionType = actionType;
                this.activated = true;
                yield this.onActivate(true);
            }
            case NATURAL -> {
                boolean active = this.activated = this.canEnable.test(true) && this.onActivate(false).join();
                this.actionType = actionType;
                yield CompletableFuture.completedFuture(active);
            }
            case SHUTDOWN -> CompletableFuture.completedFuture(false);
        };
    }

    public boolean tryDeactivate(@NotNull AbstractModule.ActionType actionType) {
        if (!this.isActivated()) return true;

        GenerationSettings generationSettings = this.getDungeon().getGenerationSettings();
        GenerationType generationType = generationSettings.getGenerationType();
        List<String> moduleWhitelist = generationType.getModuleWhitelist();

        if (generationType.isStatic() && moduleWhitelist.contains(this.getId())) return false;

        return switch (actionType) {
            case FORCE -> {
                this.activated = false;
                this.onDeactivate(true);
                yield true;
            }
            case NATURAL -> {
                this.activated = !this.onDeactivate(false);
                yield !activated;
            }
            case SHUTDOWN -> true;
        };
    }

    public void update() {
        if (isActivated() && !isUpdatable()) return;
        boolean canActivate = this.getActionType().isForce() || canEnable.test(true);

        if (canActivate && !isActivated()) {
            this.tryActive(this.getActionType())
                    .thenAcceptAsync(aBoolean -> {
                        if (!aBoolean && isImportantly()) this.getDungeon().cancel(false);
                    });
        } else {
            DungeonStage stage = this.getDungeon().getStage();
            boolean cancelled = stage.isDeleting() || stage.isCancelled();
            boolean deactivate = !canActivate && isActivated() && cancelled;

            if (deactivate) tryDeactivate(this.getActionType());
        }
    }

    @NotNull
    public DungeonPlugin plugin() {
        return getDungeon().plugin();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    protected void error(@NotNull String message) {
        this.plugin().error("[" + this.getName() + " Module of '" + this.getDungeon().getId() + "']: " + message);
    }

    protected void warn(@NotNull String message) {
        this.plugin().warn("[" + this.getName() + " Module of '" + this.getDungeon().getId() + "']: " + message);
    }

    public void debug(@NotNull String message) {
        if (Config.MODULE_DEBUG.get())
            this.plugin().debug("[" + this.getName() + " Module of '" + this.getDungeon().getId() + "']: " + message);
    }

    @Getter
    public enum ActionType {
        NATURAL,
        FORCE,
        SHUTDOWN;

        public boolean isForce() {
            return this.equals(FORCE);
        }

        @NotNull
        public static ActionType of(boolean shutdown) {
            return shutdown ? SHUTDOWN : FORCE;
        }
    }
}
