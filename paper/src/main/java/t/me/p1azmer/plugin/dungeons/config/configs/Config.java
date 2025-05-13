package t.me.p1azmer.plugin.dungeons.config.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import t.me.p1azmer.plugin.dungeons.api.models.generator.WorldGenerationInfo;
import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.item.SimpleSerializer;

import java.io.File;
import java.util.List;
import java.util.Set;

@Getter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class Config extends ConfigHolder<Config> {

    List<WorldGenerationInfo> worldGenerationInfoList = List.of(
            new WorldGenerationInfo("world",
                    0, 0, 5000, 5000,
                    Set.of(Material.WATER), Set.of("OCEAN"),
                    true, true, false)
    );

    public List<WorldGenerationInfo> getWorldGenerationInfoByWorld(World world) {
        return worldGenerationInfoList.stream()
                .filter(wgInfo -> wgInfo.getWorldName().equalsIgnoreCase(world.getName()))
                .toList();
    }

    public Config(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .registerAll(SimpleSerializer.defaultSerializer().toSerializers())
                .build());
    }

    public Config() {
        this(null);
    }
}
