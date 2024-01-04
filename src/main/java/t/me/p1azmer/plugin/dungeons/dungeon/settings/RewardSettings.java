package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.values.UniInt;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public class RewardSettings extends AbstractSettings implements IPlaceholderMap {
    private UniInt limit;
    private final PlaceholderMap placeholderMap;

    public RewardSettings(@NotNull Dungeon dungeon, UniInt limit) {
        super(dungeon);
        this.limit = limit;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REWARD_LIMIT_MAX, () -> String.valueOf(this.getLimit().getMaxValue()))
                .add(Placeholders.REWARD_LIMIT_MIN, () -> String.valueOf(this.getLimit().getMinValue()))
        ;
    }

    public static RewardSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        UniInt limit = UniInt.read(cfg, path + ".Limit");
        return new RewardSettings(dungeon, limit);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.getLimit().write(cfg, path + ".Limit");
    }

    @NotNull
    public UniInt getLimit() {
        return limit;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setLimit(@NotNull UniInt limit) {
        this.limit = limit;
    }
}
