package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class StageSettings extends AbstractSettings {
    private final Map<DungeonStage, Integer> stageMap;

    public StageSettings(
            @NotNull Dungeon dungeon,
            @NotNull Map<DungeonStage, Integer> stageMap
    ) {
        super(dungeon);
        this.stageMap = stageMap;

        this.placeholders = new PlaceholderMap();
    }

    @NotNull
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

    public int getTime(@NotNull DungeonStage stage) {
        return this.getStageMap().getOrDefault(stage, -1);
    }

    public void setTime(@NotNull DungeonStage stage, int time) {
        this.getStageMap().put(stage, time);
    }
}
