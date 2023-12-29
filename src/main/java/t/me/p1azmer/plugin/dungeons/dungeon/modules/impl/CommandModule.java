package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class CommandModule extends AbstractModule {

    public CommandModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        return aBoolean -> true;
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        CompletableFuture.runAsync(() -> this.dungeon().getCommandsSettings().getCommands(this.dungeon().getStage()).forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)));
        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }
}
