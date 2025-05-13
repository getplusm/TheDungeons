package t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SimplePotionSerializer implements TypeSerializer<PotionEffect> {
    @Override
    public PotionEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if(node.isNull()) return null;
        return new PotionEffect(PotionEffectType.getByName(node.node("type").getString()),
                node.node("duration").getInt(),
                node.node("amplifier").getInt());
    }

    @Override
    public void serialize(Type type, @Nullable PotionEffect obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) node.raw(null);
        else {
            node.node("type").set(obj.getType().getName());
            node.node("duration").set(obj.getDuration());
            node.node("amplifier").set(obj.getAmplifier());
        }
    }
}
