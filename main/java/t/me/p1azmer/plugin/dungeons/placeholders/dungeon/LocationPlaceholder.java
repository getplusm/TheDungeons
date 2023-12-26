package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.AbstractPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.regex.Matcher;

public class LocationPlaceholder extends AbstractPlaceholder<DungeonPlugin> {

    public LocationPlaceholder(@NotNull PlaceholderExpansion<DungeonPlugin> expansion) {
        super(expansion);
    }

    @Override
    public String parse(@NotNull Matcher matcher, @NotNull OfflinePlayer player) {
        String dungeonId = matcher.group(1);
        Dungeon dungeon = plugin.getDungeonManager().getDungeonById(dungeonId);
        if (dungeon == null) return "";
        if (dungeon.getLocation() == null) return "";

        return Placeholders.forLocation(dungeon.getLocation()).apply("%location_world% ⚊ %location_x%, %location_y%, %location_z%");
    }

    @Override
    public @NotNull String getRegex() {
        return "location_(.*)";
    }
}

