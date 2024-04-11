package t.me.p1azmer.plugin.dungeons.dungeon.effect;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;

@Getter
@Setter
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

    public PotionEffect build() {
        return this.potionEffectType.createEffect(this.duration, this.amplifier);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
