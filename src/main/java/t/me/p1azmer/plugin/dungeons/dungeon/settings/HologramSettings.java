package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramSettings extends AbstractSettings implements IPlaceholderMap {
    private final PlaceholderMap placeholderMap;

    private double offsetY;
    private final Map<DungeonChestState, List<String>> stateMessagesMap;

    public HologramSettings(@NotNull Dungeon dungeon, double offsetY,
                            @NotNull Map<DungeonChestState, List<String>> stateMessagesMap
    ) {
        super(dungeon);
        this.dungeon = dungeon;
        this.offsetY = offsetY;
        this.stateMessagesMap = stateMessagesMap;
        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_HOLOGRAM_CHEST_OFFSET_Y, () -> NumberUtil.format(this.getOffsetY()))
        ;
        stateMessagesMap.forEach((state, strings) -> this.placeholderMap.add(Placeholders.DUNGEON_HOLOGRAM_MESSAGES.apply(state), () -> String.join("\n", Colorizer.apply(strings))));
    }

    @NotNull
    public static HologramSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        Map<DungeonChestState, List<String>> map = new HashMap<>();
        for (String stateId : cfg.getSection(path + ".Chest_Block.Messages.Map")) {
            DungeonChestState state = StringUtil.getEnum(stateId, DungeonChestState.class).orElse(null);
            if (state == null) continue;
            List<String> messages = cfg.getStringList(path + ".Chest_Block.Messages.Map." + stateId);
            map.put(state, messages);
        }
        List<String> waitMessage = cfg.getStringList(path + ".Messages.Wait");
        cfg.remove(path + ".Messages.Wait");
        List<String> openMessage = cfg.getStringList(path + ".Messages.Open");
        cfg.remove(path + ".Messages.Open");
        List<String> closeMessage = cfg.getStringList(path + ".Messages.Close");
        cfg.remove(path + ".Messages.Close");
        if (!waitMessage.isEmpty())
            map.put(DungeonChestState.WAITING, waitMessage);
        if (!openMessage.isEmpty())
            map.put(DungeonChestState.CLOSED, openMessage);
        if (!closeMessage.isEmpty())
            map.put(DungeonChestState.OPENED, closeMessage);

        double oldOffsetY;
        if (cfg.contains(path + ".Offset.Y")) {
            oldOffsetY = cfg.getDouble(path + ".Offset.Y");
            cfg.remove(".Offset.Y");
            cfg.set(path + ".Offset.Y", oldOffsetY);
        }
        double offSetY = cfg.getDouble(path + ".Y_Offset", 1.8);
        return new HologramSettings(dungeon, offSetY, map);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Y_Offset", this.getOffsetY());
        for (Map.Entry<DungeonChestState, List<String>> entry : this.getStateMessagesMap().entrySet()) {
            cfg.set(path + ".Chest_Block.Messages.Map." + entry.getKey(), entry.getValue());
        }
    }

    public double getOffsetY() {
        return offsetY;
    }

    @NotNull
    public Map<DungeonChestState, List<String>> getStateMessagesMap() {
        return stateMessagesMap;
    }

    @NotNull
    public List<String> getMessages(@NotNull DungeonChestState state) {
        return this.getStateMessagesMap().getOrDefault(state, new ArrayList<>());
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public void setStateMessages(@NotNull DungeonChestState state, @NotNull List<String> messages) {
        this.getStateMessagesMap().put(state, messages);
    }
}
