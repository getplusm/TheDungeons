package t.me.p1azmer.plugin.dungeons.core.common.config.serializers;

import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class NamespacedKeySerializer implements TypeSerializer<NamespacedKey> {

    @Override
    public NamespacedKey deserialize(Type type, ConfigurationNode node) {
        if (node.isNull()) return null;

        String key = node.getString();
        if (key == null) return null;

        return NamespacedKey.minecraft(key);
    }

    @Override
    public void serialize(Type type, @Nullable NamespacedKey obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.raw(null);
        else {
            node.set(obj.getKey());
        }
    }
}
