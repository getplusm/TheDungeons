package t.me.p1azmer.plugin.dungeons.dungeon.chest.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.MobModule;

import java.util.Map;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum OpenType {
    CLICK((chest, player) -> chest.setState(ChestState.OPENED)),
    TIMER((chest, player) -> chest.setState(ChestState.COOLDOWN)),
    KILL_MOBS((chest, player) -> {
        Map<String, Integer> mobMap = chest.getDungeon()
                .getMobsSettings()
                .getMobMap();
        if (mobMap.isEmpty()) {
            chest.setState(ChestState.OPENED);
            return;
        }
        chest.getDungeon()
                .getModuleManager()
                .getModule(MobModule.class)
                .ifPresent(mobModule -> {
                    if (mobModule.getMobs()
                            .getAll()
                            .stream()
                            .allMatch(Entity::isDead)) {
                        chest.setState(ChestState.OPENED);
                    }
                });

    });

    final BiConsumer<ChestBlock, Player> openAction;

    public boolean isClick() {
        return this.equals(CLICK);
    }

    public boolean isTimer() {
        return this.equals(TIMER);
    }

    public boolean isKillMobs() {
        return this.equals(KILL_MOBS);
    }
}
