package t.me.p1azmer.plugin.dungeons.api.integrations.access;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;

public interface AccessHandler {

    boolean canAccess(@NotNull Dungeon dungeon, @NotNull Player player);
}
