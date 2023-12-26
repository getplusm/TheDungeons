package t.me.p1azmer.plugin.dungeons.api.party;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;

public interface PartyHandler extends Loadable {
    int getPartySize(@NotNull Player player);

    boolean isInParty(@NotNull Player player);
}