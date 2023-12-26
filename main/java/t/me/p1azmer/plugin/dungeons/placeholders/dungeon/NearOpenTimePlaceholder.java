package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.AbstractPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;

import java.util.regex.Matcher;

public class NearOpenTimePlaceholder extends AbstractPlaceholder<DungeonPlugin> {


    public NearOpenTimePlaceholder(@NotNull PlaceholderExpansion<DungeonPlugin> expansion) {
        super(expansion);
    }

    @Override
    public String parse(@NotNull Matcher matcher, @NotNull OfflinePlayer player) {
        Dungeon dungeon = plugin.getDungeonManager().getNearestDungeon();
        if (dungeon == null) return "";
        DungeonChestBlock chest = null;//dungeon.getNearestChest();
        if (chest != null)
            return NumberUtil.format( (int) dungeon.getSettings().getChestOpenTime() - chest.getCurrentTick());
        return "";
    }

    @Override
    public @NotNull String getRegex() {
        return "nearest_open_time";
    }
}
