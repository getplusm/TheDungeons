package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class StageSettings implements IPlaceholderMap {
    private final Map<DungeonStage, Integer> stageMap;
    private final PlaceholderMap placeholderMap;

    public StageSettings(@NotNull Map<DungeonStage, Integer> stageMap) {
        this.stageMap = stageMap;

        this.placeholderMap = new PlaceholderMap();
    }

    public static StageSettings read(@NotNull JYML cfg, @NotNull String path) {
        Map<DungeonStage, Integer> map = new HashMap<>();
        if (!cfg.contains(path+".Map")){
            map.put(DungeonStage.FREEZE, 10);
            map.putAll(Map.of(
                    DungeonStage.FREEZE, 10,
                    DungeonStage.CHECK, 10,
                    DungeonStage.PREPARE, 10,
                    DungeonStage.WAITING_PLAYERS, 10,
                    DungeonStage.OPENING, 10,
                    DungeonStage.OPENED, 10,
                    DungeonStage.CLOSED, 10,
                    DungeonStage.DELETING, 10,
                    DungeonStage.CANCELLED, 10,
                    DungeonStage.REMOVED, 10
                    ));
        }
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonStage stage = StringUtil.getEnum(sId, DungeonStage.class).orElse(null);
            if (stage == null) continue;
            int time = cfg.getInt(path + ".Map." + sId);
            map.put(stage, time);
        }
        return new StageSettings(map);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        for (Map.Entry<DungeonStage, Integer> entry : this.getStageMap().entrySet()) {
            cfg.set(path + ".Map." + entry.getKey().name(), entry.getValue());
        }
    }

    @NotNull
    public Map<DungeonStage, Integer> getStageMap() {
        return stageMap;
    }

    public int getTime(@NotNull DungeonStage stage) {
        return this.getStageMap().getOrDefault(stage, -1);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
