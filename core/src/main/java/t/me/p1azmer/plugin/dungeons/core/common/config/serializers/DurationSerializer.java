package t.me.p1azmer.plugin.dungeons.core.common.config.serializers;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class DurationSerializer implements TypeSerializer<Duration> {

    private static final String FORMAT = "HH:mm:ss";

    @Override
    public Duration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.empty()) {
            throw new SerializationException("Node is empty");
        }

        String raw = node.getString();
        if (raw == null)
            return null;

        String[] args = raw.split(":");
        if (args.length != 3)
            return null;

        long millis = 0L;
        if (NumberUtils.isDigits(args[0]))
            millis += TimeUnit.HOURS.toMillis(Long.parseLong(args[0]));

        if (NumberUtils.isDigits(args[1]))
            millis += TimeUnit.MINUTES.toMillis(Long.parseLong(args[1]));

        if (NumberUtils.isDigits(args[2]))
            millis += TimeUnit.SECONDS.toMillis(Long.parseLong(args[2]));

        return Duration.ofMillis(millis);
    }

    @Override
    public void serialize(Type type, @Nullable Duration duration, ConfigurationNode node) throws SerializationException {
        if (duration == null) {
            node.raw(null);
            return;
        }
        node.set(DurationFormatUtils.formatDuration(duration.toMillis(), FORMAT));
    }

    @Override
    public @Nullable Duration emptyValue(Type specificType, ConfigurationOptions options) {
        return Duration.of(1, ChronoUnit.DAYS);
    }
}
