package t.me.p1azmer.plugin.dungeons.integration.party;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;

public class PartyHandlerParties implements PartyHandler {
    private PartiesAPI partyManager;

    @Override
    public void setup() {
        this.partyManager = Parties.getApi();
    }

    @Override
    public void shutdown() {
        if (this.partyManager != null) this.partyManager = null;
    }

    @Override
    public int getPartySize(@NotNull Player player) {
        Party party = partyManager.getPartyOfPlayer(player.getUniqueId());

        if (party == null) return 0;
        return party.getMembers().size();
    }

    @Override
    public boolean isInParty(@NotNull Player player) {
        return partyManager.isPlayerInParty(player.getUniqueId());
    }
}
