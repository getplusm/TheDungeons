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
    private Map<Boolean, List<String>> commands;

    public CommandModule(@NotNull Dungeon dungeon, @NotNull Map<Boolean, List<String>> commands, @NotNull String id) {
        super(dungeon, id, false);
    }

    @NotNull
    public Map<Boolean, List<String>> getCommands() {
        return commands;
    }

    @NotNull
    public Collection<String> getActivateCommands() {
        return new ArrayList<>(this.commands.get(true));
    }

    @NotNull
    public Collection<String> getDeactivateCommands() {
        return new ArrayList<>(this.commands.get(false));
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.commands = commands;
        return aBoolean -> dungeon().getStage().isOpened();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        CompletableFuture.runAsync(() -> this.getActivateCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)));
        return true;
    }

    @Override
    public boolean onDeactivate() {
        CompletableFuture.runAsync(() -> this.getDeactivateCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)));
        return true;
    }
}
