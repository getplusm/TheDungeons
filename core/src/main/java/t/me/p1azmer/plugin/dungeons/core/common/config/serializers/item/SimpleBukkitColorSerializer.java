package t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item;

import org.bukkit.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SimpleBukkitColorSerializer implements TypeSerializer<Color> {
    @Override
    public Color deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if(node.isNull()) return null;
        return Color.fromRGB(node.node("red").get(Integer.class),
                node.node("green").get(Integer.class),
                node.node("blue").get(Integer.class));
    }

    @Override
    public void serialize(Type type, @Nullable Color obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) node.raw(null);
        else {
            node.node("red").set(obj.getRed());
            node.node("green").set(obj.getGreen());
            node.node("blue").set(obj.getBlue());
        }
    }


}
