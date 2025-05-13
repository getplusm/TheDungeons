package t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SimpleFireworkEffectSerializer implements TypeSerializer<FireworkEffect> {
    @Override
    public FireworkEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if(node.isNull()) return null;
        else {
            return FireworkEffect.builder()
                    .flicker(node.node("flicker").getBoolean(false))
                    .trail(node.node("trail").getBoolean(false))
                    .with(node.node("type").get(FireworkEffect.Type.class, FireworkEffect.Type.BALL))
                    .withColor(node.node("colors").getList(Color.class, new ArrayList<>()))
                    .withFade(node.node("fade").getList(Color.class, new ArrayList<>()))
                    .build();
        }
    }

    @Override
    public void serialize(Type type, @Nullable FireworkEffect obj, ConfigurationNode node) throws SerializationException {
        if(obj == null) node.raw(null);
        else {
            if(obj.hasFlicker()) node.node("flicker").set(true);
            if(obj.hasTrail()) node.node("trail").set(true);
            node.node("type").set(obj.getType());
            if(obj.getColors().size() > 0) node.node("colors").setList(Color.class, obj.getColors());
            if(obj.getFadeColors().size() > 0) node.node("fade").setList(Color.class, obj.getFadeColors());
        }
    }
}
