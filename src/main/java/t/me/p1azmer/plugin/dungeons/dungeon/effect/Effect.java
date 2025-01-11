package t.me.p1azmer.plugin.dungeons.dungeon.effect;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Effect implements Placeholder {
    PotionEffectType potionEffectType;
    int duration;
    int amplifier;

    final PlaceholderMap placeholders;

    public Effect(@NotNull PotionEffectType potionEffectType, int duration, int amplifier) {
        this.potionEffectType = potionEffectType;
        this.duration = duration;
        this.amplifier = amplifier;

        this.placeholders = new PlaceholderMap()
                .add(Placeholders.EFFECT_NAME, () -> this.potionEffectType.getName())
                .add(Placeholders.EFFECT_DURATION, () -> String.valueOf(this.duration))
                .add(Placeholders.EFFECT_AMPLIFIER, () -> String.valueOf(this.amplifier));
    }

    public @NotNull PotionEffect build() {
        return potionEffectType.createEffect(this.duration, this.amplifier);
    }
}
