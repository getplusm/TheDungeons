package t.me.p1azmer.plugin.dungeons.dungeon.settings.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobsSettings extends AbstractSettings {
    Map<String, Integer> mobMap;
    boolean spawnWhenOpening;

    public MobsSettings(@NotNull Dungeon dungeon, boolean spawnWhenOpening, @NotNull Map<String, Integer> mobMap) {
        super(dungeon);
        this.mobMap = mobMap;
        this.spawnWhenOpening = spawnWhenOpening;
        this.placeholders = new PlaceholderMap().add(Placeholders.DUNGEON_SETTINGS_MOBS, () -> {
            return this.getMobMap().entrySet().stream()
                    .map(entry -> Colorizer.apply(Colors2.LIGHT_YELLOW + entry.getKey() + ": " + entry.getValue()))
                    .collect(Collectors.joining("\n"));
        });
    }

    @NotNull
    public static MobsSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean spawnWhenOpening = cfg.getBoolean(path + ".Spawn_When_Opening");
        Map<String, Integer> mobs = new HashMap<>();
        for (String mobId : cfg.getSection(path + ".Mobs")) {
            int amount = cfg.getInt(path + ".Mobs." + mobId + ".Amount");
            mobs.put(mobId, amount);
        }
        return new MobsSettings(dungeon, spawnWhenOpening, mobs);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Spawn_When_Opening", this.spawnWhenOpening);
        this.getMobMap().forEach((mobId, amount) -> cfg.set(path + ".Mobs." + mobId + ".Amount", amount));
    }
}
