package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectSettings implements IPlaceholderMap {
    private boolean enabled;
    private List<DungeonEffect> effects;

    private final PlaceholderMap placeholderMap;

    public EffectSettings(boolean enabled, @NotNull List<DungeonEffect> effects) {
        this.enabled = enabled;
        this.effects = effects;
        this.placeholderMap = new PlaceholderMap();
    }

    public static EffectSettings read(@NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled");
        List<DungeonEffect> effects = new ArrayList<>();
        for (String effectName : cfg.getSection(path + ".List")) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
            if (potionEffectType == null) continue;

            int duration = cfg.getInt(path + ".List." + effectName + ".Duration", 25);
            int amplifier = cfg.getInt(path + ".List." + effectName + ".Amplifier", 1);
            effects.add(new DungeonEffect(potionEffectType, duration, amplifier));
        }
        return new EffectSettings(enabled, effects);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        for (DungeonEffect effect : this.getEffects()) {
            cfg.set(path + ".List." + effect.getPotionEffectType().getName() + ".Duration", effect.getDuration());
            cfg.set(path + ".List." + effect.getPotionEffectType().getName() + ".Amplifier", effect.getAmplifier());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public List<DungeonEffect> getEffects() {
        return effects;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEffects(@NotNull List<DungeonEffect> effects) {
        this.effects = effects;
    }
}
