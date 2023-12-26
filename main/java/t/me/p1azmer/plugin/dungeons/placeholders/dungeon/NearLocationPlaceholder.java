package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.AbstractPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.regex.Matcher;

public class NearLocationPlaceholder extends AbstractPlaceholder<DungeonPlugin> {

    public NearLocationPlaceholder(@NotNull PlaceholderExpansion<DungeonPlugin> expansion) {
        super(expansion);
    }

    @Override
    public String parse(@NotNull Matcher matcher, @NotNull OfflinePlayer player) {
        Dungeon dungeon = plugin.getDungeonManager().getNearestDungeon();
        if (dungeon == null) return "";
        if (dungeon.getLocation() == null) return "";

        return Placeholders.forLocation(dungeon.getLocation()).apply("%location_world% ⚊ %location_x%, %location_y%, %location_z%");
    }

    @Override
    public @NotNull String getRegex() {
        return "nearest_location";
    }
}

