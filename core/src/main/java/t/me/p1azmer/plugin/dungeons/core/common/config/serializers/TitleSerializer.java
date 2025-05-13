package t.me.p1azmer.plugin.dungeons.core.common.config.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;

public class TitleSerializer implements TypeSerializer<Title> {

    @Override
    public Title deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull()) return null;

        Component title = node.node("title").get(Component.class);
        if (title == null) throw new SerializationException("Title is null");
        Component subtitle = node.node("subtitle").get(Component.class);
        if (subtitle == null) throw new SerializationException("Subtitle is null");

        long fadeIn = node.node("fadeIn").getLong();
        long fadeOut = node.node("fadeOut").getLong();
        long stay = node.node("stay").getLong();

        Title.Times times = Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(fadeOut), Duration.ofSeconds(stay));
        return Title.title(title, subtitle, times);
    }

    @Override
    public void serialize(Type type, @Nullable Title obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.raw(null);
        else {
            Title.Times times = obj.times();

            node.node("title").set(obj.title());
            node.node("subtitle").set(obj.subtitle());
            if (times != null) {
                node.node("fadeIn").set(Optional.of(times.fadeIn()).map(Duration::toSeconds).orElse(0L));
                node.node("fadeOut").set(Optional.of(times.fadeOut()).map(Duration::toSeconds).orElse(0L));
                node.node("stay").set(Optional.of(times.stay()).map(Duration::toSeconds).orElse(0L));
            } else {
                node.node("fadeIn").set(0L);
                node.node("fadeOut").set(0L);
                node.node("stay").set(0L);
            }
        }
    }
}
