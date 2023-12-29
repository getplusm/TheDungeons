package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.api.Announce;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnnounceSettings extends AbstractSettings implements IPlaceholderMap {
    private final Map<DungeonStage, Announce> announceMap;
    private final PlaceholderMap placeholderMap;

    public AnnounceSettings(@NotNull Dungeon dungeon, @NotNull Map<DungeonStage, Announce> announceMap) {
        super(dungeon);
        this.announceMap = announceMap;

        this.placeholderMap = new PlaceholderMap();

    }

    public static AnnounceSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        Map<DungeonStage, Announce> map = new HashMap<>();
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonStage stage = StringUtil.getEnum(sId, DungeonStage.class).orElse(null);
            if (stage == null) continue;
            Announce announce = Announce.read(dungeon.plugin(), cfg, path + ".Map." + sId);
            map.put(stage, announce);
        }
        return new AnnounceSettings(dungeon, map);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        for (Map.Entry<DungeonStage, Announce> entry : this.getAnnounceMap().entrySet()) {
            entry.getValue().write(cfg, path + ".Map." + entry.getKey().name());
        }
    }

    @NotNull
    public Map<DungeonStage, Announce> getAnnounceMap() {
        return announceMap;
    }

    @NotNull
    public Announce getAnnounce(@NotNull DungeonStage stage) {
        return this.getAnnounceMap().getOrDefault(stage, new Announce(Collections.emptyList(), false, new int[0]));
    }

    public void setAnnounce(@NotNull DungeonStage stage, @NotNull Announce announce) {
        this.getAnnounceMap().put(stage, announce);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
