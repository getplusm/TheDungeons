package t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.potion.PotionEffect;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.ComponentSerializer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimpleSerializer<T> {

    TypeSerializerCollection.Builder serializerCollectionBuilder;

    public SimpleSerializer(Class<T> clazz, TypeSerializer<T> serializer) {
        serializerCollectionBuilder = TypeSerializerCollection.builder()
                .register(FireworkEffect.class, new SimpleFireworkEffectSerializer())
                .register(PotionEffect.class, new SimplePotionSerializer())
                .register(Color.class, new SimpleBukkitColorSerializer())
                .register(Component.class, new ComponentSerializer())
                .register(clazz, serializer);
    }

    public TypeSerializerCollection toSerializers() {
        return serializerCollectionBuilder.build();
    }

    public static SimpleSerializer<Component> defaultSerializer() {
        return new SimpleSerializer<>(Component.class, new ComponentSerializer());
    }
}
