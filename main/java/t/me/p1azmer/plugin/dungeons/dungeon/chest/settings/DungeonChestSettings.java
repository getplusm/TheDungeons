package t.me.p1azmer.plugin.dungeons.dungeon.chest.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;

import java.util.HashMap;
import java.util.Map;

public class DungeonChestSettings implements IPlaceholderMap {
    private final Map<DungeonChestState, Integer> stateMap;
    private final PlaceholderMap placeholderMap;

    public DungeonChestSettings(@NotNull Map<DungeonChestState, Integer> stateMap) {
        this.stateMap = stateMap;

        this.placeholderMap = new PlaceholderMap();
        stateMap.forEach((state, integer) -> this.placeholderMap.add(Placeholders.DUNGEON_CHEST_STATE_TIME.apply(state), ()-> String.valueOf(integer)));
    }

    public static DungeonChestSettings read(@NotNull JYML cfg, @NotNull String path) {
        Map<DungeonChestState, Integer> map = new HashMap<>();
        if (!cfg.contains(path+".Map")){
            map.putAll(Map.of(
                    DungeonChestState.WAITING, 10,
                    DungeonChestState.COOLDOWN, 5,
                    DungeonChestState.OPENED, 10,
                    DungeonChestState.CLOSED, 10,
                    DungeonChestState.DELETED, 1
                    ));
        }
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonChestState stage = StringUtil.getEnum(sId, DungeonChestState.class).orElse(null);
            if (stage == null) continue;
            int time = cfg.getInt(path + ".Map." + sId);
            map.put(stage, time);
        }
        return new DungeonChestSettings(map);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        for (Map.Entry<DungeonChestState, Integer> entry : this.getStateMap().entrySet()) {
            cfg.set(path + ".Map." + entry.getKey().name(), entry.getValue());
        }
    }

    @NotNull
    public Map<DungeonChestState, Integer> getStateMap() {
        return stateMap;
    }

    public int getTime(@NotNull DungeonChestState state) {
        return this.getStateMap().getOrDefault(state, -1);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
