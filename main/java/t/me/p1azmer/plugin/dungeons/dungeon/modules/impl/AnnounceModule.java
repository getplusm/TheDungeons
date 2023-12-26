package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnounceModule extends AbstractModule {

    private List<AbstractModule> modules;
    public AnnounceModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true); // add announce enabled setting
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.modules = this.dungeon().getModuleManager().getModules().stream().filter(AbstractModule::isImportantly).collect(Collectors.toList());
        return aBoolean -> this.dungeon().getStage().isPrepare() && this.modules.stream().filter(AbstractModule::isActive).count() == this.modules.size();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        Location location = this.dungeon().getLocation();
        if (location == null) return false;
        plugin().getMessage(Lang.DUNGEON_SPAWN_NOTIFY)
                .replace(this.dungeon().replacePlaceholders())
                .replace(this.dungeon().getSettings().replacePlaceholders())
                .replace(Placeholders.forLocation(location))
                .broadcast();
        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }

    @NotNull
    public List<String> getMessage() {
        return switch (this.dungeon().getStage()) {
            case OPENED -> new ArrayList<>();
            case DELETING -> new ArrayList<>();
            case WAITING_PLAYERS -> new ArrayList<>();
            case CHECK -> new ArrayList<>();
            case CANCELLED -> new ArrayList<>();
            case REBOOTED -> new ArrayList<>();
            case PREPARE -> new ArrayList<>();
            case OPENING -> new ArrayList<>();
            case FREEZE -> new ArrayList<>();
            case CLOSED -> new ArrayList<>();
            case REMOVED -> new ArrayList<>();
        };
    }
}
