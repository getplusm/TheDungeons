package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.wrapper.UniInt;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders;

@Getter
@Setter
public class RewardSettings extends AbstractSettings {
    private UniInt limit;

    public RewardSettings(
            @NotNull Dungeon dungeon,
            @NotNull UniInt limit
    ) {
        super(dungeon);
        this.limit = limit;

        this.placeholders = new PlaceholderMap()
                .add(Placeholders.REWARD_LIMIT_MAX, () -> String.valueOf(this.getLimit().getMaxValue()))
                .add(Placeholders.REWARD_LIMIT_MIN, () -> String.valueOf(this.getLimit().getMinValue()))
        ;
    }

    @NotNull
    public static RewardSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        UniInt limit = UniInt.read(cfg, path + ".Limit");
        return new RewardSettings(dungeon, limit);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.getLimit().write(cfg, path + ".Limit");
    }
}
