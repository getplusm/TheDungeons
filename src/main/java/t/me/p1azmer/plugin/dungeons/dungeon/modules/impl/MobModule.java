package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.function.Predicate;

public class MobModule extends AbstractModule {
    private MobList mobList;

    public MobModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, false);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.mobList = new MobList();
        return aBoolean -> dungeon().getStage().isOpened();
    }

    @Override
    protected void onShutdown() {
        if (this.mobList != null) {
            this.killMobs();
            this.mobList = null;
        }
    }

    @Override
    protected boolean onActivate(boolean force) {
        if (!dungeon().getSettings().getMobMap().isEmpty()) {
            dungeon().getSettings().getMobMap().forEach((mobId, amount) -> {
                for (int i = 0; i < amount; i++) {
                    plugin().getMobManager().spawnMob(dungeon(), MobFaction.ENEMY, mobId, this.mobList);
                }
            });
        }
        return true;
    }

    @Override
    protected boolean onDeactivate() {
        this.killMobs();
        return true;
    }

    public void killMobs() {
        for (MobFaction faction : MobFaction.values()) {
            this.killMobs(faction);
        }
    }

    public void killMobs(@NotNull MobFaction faction) {
        this.getMobs().removeAll(faction);
    }

    @NotNull
    public MobList getMobs() {
        if (this.mobList == null)
            this.mobList = new MobList();

        this.mobList.getEnemies().removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }
}
