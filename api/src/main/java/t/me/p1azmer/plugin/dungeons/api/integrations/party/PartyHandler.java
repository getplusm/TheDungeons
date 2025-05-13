package t.me.p1azmer.plugin.dungeons.api.integrations.party;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PartyHandler {

    int partySize(@NotNull Player player);

    boolean inParty(@NotNull Player player);
}