package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.*;

@Getter
@Setter
public class AnnounceSettings extends AbstractSettings {
    private final Map<DungeonStage, Map<Announce, int[]>> announceMap;
    private final PlaceholderMap placeholderMap;

    public AnnounceSettings(
            @NotNull Dungeon dungeon,
            @NotNull Map<DungeonStage, Map<Announce, int[]>> announceMap
    ) {
        super(dungeon);
        this.announceMap = announceMap;
        this.placeholderMap = new PlaceholderMap();
    }

    @NotNull
    public static AnnounceSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        Map<DungeonStage, Map<Announce, int[]>> map = new HashMap<>();
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonStage stage = StringUtil.getEnum(sId, DungeonStage.class).orElse(null);
            if (stage == null) continue;
            String section = cfg.getString(path + ".Map." + sId);
            if (section == null) continue;
            if (cfg.contains(path + ".Map." + sId + ".Messages")) {
                cfg.remove(path + ".Map." + sId + ".Messages");
                cfg.remove(path + ".Map." + sId + ".Global");
                cfg.remove(path + ".Map." + sId + ".Times");
                cfg.saveChanges();
            }

            Map<Announce, int[]> announces = new HashMap<>();
            for (String announceId : cfg.getSection(path + ".Map." + sId + ".Announce")) {
                Announce announce = dungeon.plugin().getAnnounceManager().getAnnounce(announceId);
                if (announce == null) continue;

                int[] times = cfg.getIntArray(path + ".Map." + sId + ".Announce." + announceId);
                announces.put(announce, times);
            }
            map.put(stage, announces);
        }
        return new AnnounceSettings(dungeon, map);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        for (Map.Entry<DungeonStage, Map<Announce, int[]>> entry : this.getAnnounceMap().entrySet()) {
            if (entry.getValue().isEmpty()) {
                cfg.set(path + ".Map." + entry.getKey().name(), Collections.emptyMap());
            } else {
                for (Map.Entry<Announce, int[]> pair : entry.getValue().entrySet()) {
                    cfg.setIntArray(path + ".Map." + entry.getKey().name() + ".Announce." + pair.getKey().getId(), pair.getValue());
                }
            }
        }
    }

    @NotNull
    public Set<Announce> getAnnounces(@NotNull DungeonStage stage, int time) {
        return this.getAnnounceMap().entrySet().stream()
                .filter(entry -> entry.getKey().equals(stage) && entry.getValue()
                        .entrySet()
                        .stream()
                        .anyMatch(f -> Arrays.stream(f.getValue()).anyMatch(i -> i == time)))
                .flatMap(founder -> founder.getValue().keySet().stream())
                .findFirst()
                .map(Collections::singleton)
                .orElse(Collections.emptySet());
    }

    @NotNull
    public Map<Announce, int[]> getAnnounceMap(@NotNull DungeonStage stage) {
        return new HashMap<>(this.getAnnounceMap().entrySet().stream().filter(entry -> entry.getKey().equals(stage)).map(Map.Entry::getValue).findFirst().orElse(Collections.emptyMap()));
    }

    public void setAnnounce(@NotNull DungeonStage stage, @NotNull Map<Announce, int[]> map) {
        this.getAnnounceMap().put(stage, map);
    }

    public void removeAnnounce(@NotNull DungeonStage stage) {
        this.getAnnounceMap().put(stage, Collections.emptyMap());
    }
}
