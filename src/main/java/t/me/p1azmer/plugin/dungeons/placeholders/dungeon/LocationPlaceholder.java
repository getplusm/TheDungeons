package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public String parse(@NotNull Matcher matcher, @Nullable Player player) {
        String dungeonId = matcher.group(1);
        Dungeon dungeon = plugin.getDungeonManager().getDungeonById(dungeonId);
        if (dungeon == null) return "";

        Location location = dungeon.getLocation().orElse(null);
        if (location == null) return "";

        return Placeholders.forLocation(location).apply("%location_world% ⚊  %location_x%, %location_y%, %location_z%");
    }

    @Override
    public @NotNull String getRegex() {
        return "location_(.*)";
    }
}