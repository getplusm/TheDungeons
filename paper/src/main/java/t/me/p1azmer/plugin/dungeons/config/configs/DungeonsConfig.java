package t.me.p1azmer.plugin.dungeons.config.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.types.GenerationType;
import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item.SimpleSerializer;

import java.io.File;
import java.util.Map;

@Getter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class DungeonsConfig extends ConfigHolder<DungeonsConfig> {

    Map<String, Dungeon> dungeons = Map.of(
            "preview", new Dungeon(
                    "preview", Component.text("Preview Dungeon", NamedTextColor.AQUA),
                    "world",
                    GenerationType.GROUND
            )
    );

    public DungeonsConfig(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .registerAll(SimpleSerializer.defaultSerializer().toSerializers())
                .build());
    }

    public DungeonsConfig() {
        this(null);
    }
}
