package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.function.Predicate;

public class MobModule extends AbstractModule {
    public MobModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, false);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        return aBoolean -> dungeon().getStage().isOpened();
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    protected boolean onActivate() {
        if (!dungeon().getSettings().getMobMap().isEmpty()) {
            dungeon().getSettings().getMobMap().forEach((mobId, amount) -> {
                for (int i = 0; i < amount; i++) {
                    plugin().getMobManager().spawnMob(dungeon(), MobFaction.ENEMY, mobId, dungeon().getMobs());
                }
            });
        }
        return true;
    }

    @Override
    protected boolean onDeactivate() {
        return true;
    }
}
