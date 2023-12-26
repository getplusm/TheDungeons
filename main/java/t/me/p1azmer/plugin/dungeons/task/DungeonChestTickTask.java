package t.me.p1azmer.plugin.dungeons.task;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.server.AbstractTask;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.MainSettings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonChestTickTask extends AbstractTask<DungeonPlugin> {
    private final DungeonManager manager;
    private Map<DungeonChestBlock, AtomicInteger> tickCache;

    public DungeonChestTickTask(@NotNull DungeonManager manager) {
        super(manager.plugin(), 1, false);
        this.manager = manager;
    }

    @Override
    public boolean start() {
        this.tickCache = new HashMap<>();
        return super.start();
    }

    @Override
    public boolean stop() {
        if (this.tickCache != null) {
            this.tickCache.clear();
            this.tickCache = null;
        }
        return super.stop();
    }

    @Override
    public void action() {
        for (Dungeon dungeon : this.manager.getDungeons()) {
            MainSettings settings = dungeon.getSettings();
            dungeon.getModuleManager().getModule(ChestModule.class).ifPresent(module -> {
                plugin.warn("Tick the chest module");
                for (DungeonChestBlock chest : new HashSet<>(module.getChests())) {
                    if (chest.getState().isDeleted()) {
                        chest.clear();
                        module.getChestMap().remove(chest.getBlock(), chest);
                        continue;
                    }
                    this.tickCache.putIfAbsent(chest, new AtomicInteger(0));
                    AtomicInteger time = this.tickCache.computeIfAbsent(chest, dungeonChestBlock -> new AtomicInteger(0));

                    chest.updateHologram(time.get(), plugin);
                    if (chest.getState().isCooldown() && !settings.getChestOpenType().isClick() && time.incrementAndGet() == settings.getChestWaitTime()) {
                        chest.setChestState(DungeonChestState.OPENED);
                        time.set(0);
                    } else if (time.incrementAndGet() == settings.getChestOpenTime()) {
                        chest.setChestState(DungeonChestState.OPENED);
                        time.set(0);
                    } else if (chest.getState().isOpen() && time.incrementAndGet() == settings.getChestCloseTime()) {
                        chest.setChestState(DungeonChestState.CLOSED);
                        chest.clear();
                        module.getChestMap().remove(chest.getBlock(), chest);
                    }
                }
            });
        }
    }
}
