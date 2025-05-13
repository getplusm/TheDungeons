package t.me.p1azmer.plugin.dungeons.core.common.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class ConfigHolder<T extends ConfigHolder<T>> {

    transient ConfigLoader loader;
    transient File baseFilePath;
    transient CommentedConfigurationNode rootNode;

    public ConfigHolder(File baseFilePath) {
        this(baseFilePath, TypeSerializerCollection.builder().build());
    }

    public ConfigHolder(File baseFilePath, TypeSerializerCollection... serializers) {
        loader = new ConfigLoader(baseFilePath, this, serializers);
        this.baseFilePath = baseFilePath;
    }

    public void onPostLoad() {
    }

    public T loadAndSave() {
        return load(true);
    }

    public T load(boolean save) {
        validateEmptyConstructor();
        validateAnnotation();
        T config = loader.load(getGenericClass(), save);
        loader.setConfig(config);
        config.setLoader(loader);
        config.baseFilePath = baseFilePath;
        return config;
    }

    private void validateAnnotation() {
        Class<T> clazz = getGenericClass();
        Class<?> annotationClass = ConfigSerializable.class;
        boolean containsConfigSerializableAnnotation = Arrays.stream(clazz.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType() == annotationClass);
        if (!containsConfigSerializableAnnotation)
            throw new RuntimeException("class " + clazz.getName() + " don`t have " + annotationClass.getName() + " annotation");
    }

    private void validateEmptyConstructor() {
        Class<T> clazz = getGenericClass();
        boolean emptyConstructorExist = Arrays.stream(clazz.getConstructors())
                .anyMatch(constructor -> constructor.getParameterCount() == 0);
        if (!emptyConstructorExist) emptyConstructorExist = Arrays.stream(clazz.getDeclaredConstructors())
                .anyMatch(constructor -> constructor.getParameterCount() == 0);
        if (!emptyConstructorExist)
            throw new RuntimeException("class " + clazz.getName() + " don`t have empty constructor");
    }

    @SuppressWarnings("unchecked")
    public Class<T> getGenericClass() {
        Class<?> actual = getClass();
        ParameterizedType type = (ParameterizedType) actual.getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    public void setFields(T other, boolean ignoreTransient, boolean onlyPublic) {
        Class<T> clazz = getGenericClass();
        Set<Field> fields = new HashSet<>(Arrays.stream(clazz.getFields()).toList());
        if (!onlyPublic)
            fields.addAll(Arrays.stream(clazz.getDeclaredFields()).peek(field -> field.setAccessible(true)).toList());
        fields.stream()
                .filter(field -> {
                    if (!ignoreTransient) return true;
                    return !Modifier.isTransient(field.getModifiers());
                }).forEach(field -> {
                    try {
                        Object otherObject = field.get(other);
                        field.set(this, otherObject);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void reload() {
        setFields(load(false), false, false);
    }

    public void save() {
        loader.save();
    }

    public T loadOrCreateConfig() {
        if (baseFilePath.exists()) return load(false);
        return loadAndSave();
    }
}
