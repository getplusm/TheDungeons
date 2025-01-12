package t.me.p1azmer.plugin.dungeons.integration.party;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.handler.party.PartyHandler;

public class PartyHandlerPaF implements PartyHandler {
    private PartyManager partyManager;

    @Override
    public void setup() {
        this.partyManager = PartyManager.getInstance();
    }

    @Override
    public void shutdown() {
        if (this.partyManager != null) this.partyManager = null;
    }

    @Override
    public int getPartySize(@NotNull Player player) {
        PAFPlayerManager pafPlayerManager = PAFPlayerManager.getInstance();

        PAFPlayer pafPlayer = pafPlayerManager.getPlayer(player.getUniqueId());
        PlayerParty party = partyManager.getParty(pafPlayer);

        if (party == null) return 0;
        return party.getAllPlayers().size();
    }

    @Override
    public boolean isInParty(@NotNull Player player) {
        PAFPlayerManager pafPlayerManager = PAFPlayerManager.getInstance();
        PAFPlayer pafPlayer = pafPlayerManager.getPlayer(player.getUniqueId());
        PlayerParty party = partyManager.getParty(pafPlayer);

        return party != null;
    }
}
