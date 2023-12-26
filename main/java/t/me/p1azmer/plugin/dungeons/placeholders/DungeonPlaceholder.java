package t.me.p1azmer.plugin.dungeons.placeholders;

import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.placeholders.dungeon.*;

public class DungeonPlaceholder extends PlaceholderExpansion<DungeonPlugin> {
    public DungeonPlaceholder(DungeonPlugin plugin) {
        super(plugin);
        this.addPlaceholder(
                new LocationPlaceholder(this)
                , new WaitTimePlaceholder(this)
                , new CloseTimePlaceholder(this)
                , new OpenTimePlaceholder(this)
                , new NearLocationPlaceholder(this)
                , new NearWaitTimePlaceholder(this)
                , new NearCloseTimePlaceholder(this)
                , new NearOpenTimePlaceholder(this)
        );
    }
}
