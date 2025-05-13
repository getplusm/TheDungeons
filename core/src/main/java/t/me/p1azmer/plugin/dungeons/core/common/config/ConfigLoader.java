package t.me.p1azmer.plugin.dungeons.core.common.config;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@SuppressWarnings("rawtypes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigLoader {

    final File baseFilePath;
    final TypeSerializerCollection[] serializers;
    @Setter ConfigHolder config;

    public ConfigLoader(File baseFilePath, ConfigHolder config, TypeSerializerCollection... serializers) {
        this.baseFilePath = baseFilePath;
        this.serializers = serializers;
        this.config = config;
    }

    public <T extends ConfigHolder> T load(Class<T> type) {
        return load(type, true);
    }

    public <T extends ConfigHolder> T load(Class<T> type, boolean save) {
        return load(baseFilePath, type, save, serializers);
    }

    public <T extends ConfigHolder> T load(String path, Class<T> type) {
        File file = new File(baseFilePath, path);
        return load(file, type, true, serializers);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public <T extends ConfigHolder> T load(File file, Class<T> type, boolean save, TypeSerializerCollection... serializers) {
        try {
            YamlConfigurationLoader loader = getLoader(file, serializers);
            CommentedConfigurationNode node = loader.load();
            T config = node.get(type);
            if (config == null) throw new NullPointerException("Config cannot be null!");

            config.setRootNode(node);
            config.onPostLoad();

            if (save) {
                node.set(type, config);

                try {
                    loader.save(node);
                } catch (ConfigurateException ex) {
                    ex.printStackTrace();
                }
            }

            return config;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save() {
        save(baseFilePath, config, serializers);
    }

    public void save(String path) {
        File file = new File(baseFilePath, path);
        save(file, config, serializers);
    }

    public void save(File file, Object obj, TypeSerializerCollection... serializers) {
        try {
            YamlConfigurationLoader loader = getLoader(file, serializers);
            CommentedConfigurationNode node = loader.load();
            node.set(obj.getClass(), obj);
            loader.save(node);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private YamlConfigurationLoader getLoader(File file, TypeSerializerCollection... serializers) {
        YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder()
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .file(file);

        if (serializers.length > 0) {
            builder.defaultOptions(opts -> opts
                    .serializers(b -> {
                        for (TypeSerializerCollection collection : serializers)
                            b.registerAll(collection);
                    }));
        }

        return builder.build();
    }
}
