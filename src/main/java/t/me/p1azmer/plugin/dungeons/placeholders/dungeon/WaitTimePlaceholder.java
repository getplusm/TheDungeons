package t.me.p1azmer.plugin.dungeons.placeholders.dungeon;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.placeholder.AbstractPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderExpansion;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.regex.Matcher;

public class WaitTimePlaceholder extends AbstractPlaceholder<DungeonPlugin> {


    public WaitTimePlaceholder(@NotNull PlaceholderExpansion<DungeonPlugin> expansion) {
        super(expansion);
    }

    @Override
    public String parse(@NotNull Matcher matcher, @Nullable Player player) {
        String dungeonId = matcher.group(1);
        Dungeon dungeon = plugin.getDungeonManager().getDungeonById(dungeonId);
        if (dungeon == null) return "";
        if (dungeon.getStage().isWaitingPlayers())
            return NumberUtil.format(dungeon.getStageSettings().getTime(DungeonStage.WAITING_PLAYERS) - dungeon.getTimer().getTimeToNextStageInSeconds());
        return "";
    }

    @Override
    public @NotNull String getRegex() {
        return "wait_time_(.*)";
    }
}