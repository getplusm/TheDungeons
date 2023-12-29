package t.me.p1azmer.plugin.dungeons.mob.style;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.StringUtil;

public interface MobStyleWriter<E, T> {

    @Nullable
    T parse(@NotNull String value);

    @NotNull
    T[] values();

    void apply(@NotNull E entity, @NotNull T value);

    boolean alreadyHas(@NotNull E entity, @NotNull T value);

    interface WriterBoolean<E> extends MobStyleWriter<E, Boolean> {

        @Nullable
        @Override
        default Boolean parse(@NotNull String value) {
            return Boolean.parseBoolean(value);
        }

        @NotNull
        @Override
        default Boolean[] values() {
            return new Boolean[]{true, false};
        }
    }

    interface WriterEnum<E, T extends Enum<T>> extends MobStyleWriter<E, T> {

        @NotNull
        Class<T> getEnumClass();

        @Nullable
        @Override
        default T parse(@NotNull String value) {
            return StringUtil.getEnum(value, this.getEnumClass()).orElse(null);
        }

        @NotNull
        @Override
        default T[] values() {
            return this.getEnumClass().getEnumConstants();
        }
    }
}