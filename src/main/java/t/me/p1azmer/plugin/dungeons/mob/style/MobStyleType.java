package t.me.p1azmer.plugin.dungeons.mob.style;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public enum MobStyleType {

    BABY(MobStyleWrapper.BABY),
    SHEEP_COLOR(MobStyleWrapper.SHEEP_COLOR),
    HORSE_COLOR(MobStyleWrapper.HORSE_COLOR),
    HORSE_STYLE(MobStyleWrapper.HORSE_STYLE),
    FOX_TYPE(MobStyleWrapper.FOX_TYPE),
    CREEPER_CHARGE(MobStyleWrapper.CREEPER_CHARGE),
    SLIME_SIZE(MobStyleWrapper.SLIME_SIZE),
    PHANTOM_SIZE(MobStyleWrapper.PHANTOM_SIZE),
    LLAMA_COLOR(MobStyleWrapper.LLAMA_COLOR),
    PARROT_VARIANT(MobStyleWrapper.PARROT_VARIANT),
    RABBIT_TYPE(MobStyleWrapper.RABBIT_TYPE),
    CAT_TYPE(MobStyleWrapper.CAT_TYPE),
    MUSHROOM_VARIANT(MobStyleWrapper.MUSHROOM_VARIANT),
    VILLAGER_PROFESSION(MobStyleWrapper.VILLAGER),
    ZOMBIE_VILLAGER_PROFESSION(MobStyleWrapper.VILLAGER_ZOMBIE),
    ;

    private final MobStyleWrapper<?, ?> wrapper;

    MobStyleType(@NotNull MobStyleWrapper<?, ?> wrapper) {
        this.wrapper = wrapper;
    }

    @NotNull
    public MobStyleWrapper<?, ?> getWrapper() {
        return wrapper;
    }

    public boolean isApplicable(@NotNull EntityType type) {
        Class<?> clazz = type.getEntityClass();
        if (clazz == null) return false;

        return this.getWrapper().getEntityClass().isAssignableFrom(clazz);
    }

    @NotNull
    public static MobStyleType[] get(@NotNull EntityType type) {
        return Stream.of(values()).filter(styleType -> styleType.isApplicable(type)).toArray(MobStyleType[]::new);
    }
}