package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.HashMap;
import java.util.Map;

public class StageSettings extends AbstractSettings implements IPlaceholderMap {
    private final Map<DungeonStage, Integer> stageMap;
    private final PlaceholderMap placeholderMap;

    public StageSettings(@NotNull Dungeon dungeon, @NotNull Map<DungeonStage, Integer> stageMap) {
        super(dungeon);
        this.stageMap = stageMap;

        this.placeholderMap = new PlaceholderMap();
    }

    public static StageSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        Map<DungeonStage, Integer> map = new HashMap<>();
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonStage stage = StringUtil.getEnum(sId, DungeonStage.class).orElse(null);
            if (stage == null) continue;
            int time = cfg.getInt(path + ".Map." + sId);
            map.put(stage, time);
        }
        return new StageSettings(dungeon, map);
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

    public void setTime(@NotNull DungeonStage stage, int time){
        this.getStageMap().put(stage, time);
    }
}
