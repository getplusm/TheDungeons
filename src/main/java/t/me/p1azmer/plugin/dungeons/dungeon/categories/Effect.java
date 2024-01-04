package t.me.p1azmer.plugin.dungeons.dungeon.categories;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.Placeholders;

public class Effect implements IPlaceholderMap {

    private PotionEffectType potionEffectType;
    private int duration;
    private int amplifier;

    private final PlaceholderMap placeholderMap;

    public Effect(@NotNull PotionEffectType potionEffectType, int duration, int amplifier) {
        this.potionEffectType = potionEffectType;
        this.duration = duration;
        this.amplifier = amplifier;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.EFFECT_NAME, () -> this.potionEffectType.getName())
                .add(Placeholders.EFFECT_DURATION, () -> String.valueOf(this.duration))
                .add(Placeholders.EFFECT_AMPLIFIER, () -> String.valueOf(this.amplifier))
        ;
    }

    @NotNull
    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setPotionEffectType(@NotNull PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public PotionEffect build() {
        return this.potionEffectType.createEffect(this.duration, this.amplifier);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
