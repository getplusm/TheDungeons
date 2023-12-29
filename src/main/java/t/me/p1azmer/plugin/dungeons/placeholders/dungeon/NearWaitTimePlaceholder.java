package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.AbstractPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.regex.Matcher;

public class NearWaitTimePlaceholder extends AbstractPlaceholder<DungeonPlugin> {

    public NearWaitTimePlaceholder(@NotNull PlaceholderExpansion<DungeonPlugin> expansion) {
        super(expansion);
    }

    @Override
    public String parse(@NotNull Matcher matcher, @NotNull OfflinePlayer player) {
        Dungeon dungeon = plugin.getDungeonManager().getNearestDungeon();
        if (dungeon == null) return "";
        if (dungeon.getStage().isWaitingPlayers())
            return NumberUtil.format(dungeon.getStageSettings().getTime(DungeonStage.WAITING_PLAYERS) - dungeon.getNextStageTime());
        return "";
    }

    @Override
    public @NotNull String getRegex() {
        return "nearest_wait_time";
    }
}
