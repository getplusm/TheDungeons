package t.me.p1azmer.plugin.dungeons.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.server.AbstractTask;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AliveTask extends AbstractTask<DungeonPlugin> {

    private Map<Dungeon, AtomicInteger> tickCache;

    public AliveTask(@NotNull DungeonPlugin plugin) {
        super(plugin, 1, false);
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
        if (plugin.getServer().getOnlinePlayers().isEmpty()) return;

        for (Dungeon dungeon : plugin.getDungeonManager().getDungeons()) {
            this.tickCache.putIfAbsent(dungeon, new AtomicInteger(0));

            AtomicInteger time = this.tickCache.getOrDefault(dungeon, new AtomicInteger(0));
            dungeon.tick(time.get());

            if (dungeon.getState().isCanceled()) {
                time.set(0);
                continue;
            }

            if (dungeon.isForceChange()) {
                dungeon.setForceChange(false);
                dungeon.setState(DungeonState.PREPARE);
                time.set(0);
                continue;
            }

            if (dungeon.isEffectsEnabled()) { // apply negativity effects
                if ((dungeon.getState().isPrepare() || dungeon.getState().isOpen()) && dungeon.getLocation() != null) {
                    LocationUtil.getNearbyEntities(dungeon.getLocation(), Player.class, 15)
                            .forEach(player -> player.addPotionEffects(dungeon.getPotionEffects()));
                }
            }

            if (dungeon.getState().isFreeze() && time.incrementAndGet() == dungeon.getRefreshTime()) {
                dungeon.call(DungeonState.WAITING);
                time.set(0);
            } else if (dungeon.getState().isWaiting() && time.incrementAndGet() == dungeon.getWaitTime()) {
                dungeon.call(DungeonState.PREPARE);
                time.set(0);
            } else if (dungeon.getState().isPrepare() && dungeon.getOpenType().equals(Dungeon.OpenType.TIMER) && time.incrementAndGet() == dungeon.getOpenTime()) {
                dungeon.call(DungeonState.OPEN);
                time.set(0);
            } else if (dungeon.getState().isOpen() && time.incrementAndGet() == dungeon.getCloseTime()) {
                dungeon.call(DungeonState.CLOSED);
                time.set(0);
            }
        }
    }
}
