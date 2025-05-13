package t.me.p1azmer.plugin.dungeons.api.models.dungeon.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.DurationSerializer;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item.SimpleSerializer;

import java.io.File;
import java.time.Duration;

@Getter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public abstract class AbstractSetting<T extends ConfigHolder<T>> extends ConfigHolder<T> implements Setting {

    protected AbstractSetting(String baseFilePath, String key) {
        super(new File(baseFilePath + "/settings/" + key + ".yml"), TypeSerializerCollection.builder()
                .register(Duration.class, new DurationSerializer())
                .registerAll(SimpleSerializer.defaultSerializer().toSerializers())
                .build());
    }

    protected AbstractSetting() {
        this(null, null);
    }
}
