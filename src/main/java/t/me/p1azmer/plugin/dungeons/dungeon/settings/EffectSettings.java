package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Effect;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.ArrayList;
import java.util.List;

public class EffectSettings extends AbstractSettings implements IPlaceholderMap {
    private boolean enabled;
    private List<Effect> effects;

    private final PlaceholderMap placeholderMap;

    public EffectSettings(@NotNull Dungeon dungeon, boolean enabled, @NotNull List<Effect> effects) {
        super(dungeon);
        this.enabled = enabled;
        this.effects = effects;
        this.placeholderMap = new PlaceholderMap();
    }

    public static EffectSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled");
        List<Effect> effects = new ArrayList<>();
        for (String effectName : cfg.getSection(path + ".List")) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
            if (potionEffectType == null) continue;

            int duration = cfg.getInt(path + ".List." + effectName + ".Duration", 25);
            int amplifier = cfg.getInt(path + ".List." + effectName + ".Amplifier", 1);
            effects.add(new Effect(potionEffectType, duration, amplifier));
        }
        return new EffectSettings(dungeon, enabled, effects);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Enabled", this.isEnabled());
        for (Effect effect : this.getEffects()) {
            cfg.set(path + ".List." + effect.getPotionEffectType().getName() + ".Duration", effect.getDuration());
            cfg.set(path + ".List." + effect.getPotionEffectType().getName() + ".Amplifier", effect.getAmplifier());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public List<Effect> getEffects() {
        return effects;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEffects(@NotNull List<Effect> effects) {
        this.effects = effects;
    }
}
