package t.me.p1azmer.plugin.dungeons.api.handler.access;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public interface AccessHandler extends Loadable {
    boolean allowedToEnterDungeon(@NotNull Dungeon dungeon, @NotNull Player player);
}
