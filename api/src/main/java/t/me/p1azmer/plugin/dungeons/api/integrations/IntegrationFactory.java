package t.me.p1azmer.plugin.dungeons.api.integrations;

import t.me.p1azmer.plugin.dungeons.api.integrations.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.party.PartyHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.integrations.schematic.SchematicHandler;

import javax.annotation.Nullable;

public interface IntegrationFactory {
    @Nullable AccessHandler getAccessHandler();

    @Nullable HologramHandler getHologramHandler();

    @Nullable PartyHandler getPartyHandler();

    @Nullable RegionHandler getRegionHandler();

    @Nullable SchematicHandler getSchematicHandler();
}
