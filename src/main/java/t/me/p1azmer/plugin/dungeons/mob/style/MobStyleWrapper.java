package t.me.p1azmer.plugin.dungeons.mob.style;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.NumberUtil;

import java.util.stream.IntStream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MobStyleWrapper<E, T> {

    Class<E> entityClass;
    MobStyleWriter<E, T> writer;

    public boolean canBeApplied(@NotNull LivingEntity entity) {
        return this.entityClass.isInstance(entity);
    }

    @SuppressWarnings("unchecked")
    public final void apply(@NotNull LivingEntity entity, @NotNull String value) {
        if (!this.canBeApplied(entity)) return;

        T type = this.writer.parse(value);
        if (type == null) return;

        this.writer.apply((E) entity, type);
    }

    @SuppressWarnings("unchecked")
    public final boolean alreadyHas(@NotNull LivingEntity entity, @NotNull String value) {
        if (!this.canBeApplied(entity)) return false;

        T type = this.writer.parse(value);
        if (type == null) return false;

        return this.writer.alreadyHas((E) entity, type);
    }

    public static final MobStyleWrapper<Ageable, Boolean> BABY = new MobStyleWrapper<>(Ageable.class, new MobStyleWriter.WriterBoolean<>() {
        @Override
        public void apply(@NotNull Ageable entity, @NotNull Boolean value) {
            if (value) entity.setBaby();
            else entity.setAdult();
        }

        @Override
        public boolean alreadyHas(@NotNull Ageable entity, @NotNull Boolean value) {
            return !entity.isAdult() == value;
        }
    });

    public static final MobStyleWrapper<Sheep, DyeColor> SHEEP_COLOR = new MobStyleWrapper<>(Sheep.class, new MobStyleWriter.WriterEnum<Sheep, DyeColor>() {
        @NotNull
        @Override
        public Class<DyeColor> getEnumClass() {
            return DyeColor.class;
        }

        @Override
        public void apply(@NotNull Sheep entity, @NotNull DyeColor value) {
            entity.setColor(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Sheep entity, @NotNull DyeColor value) {
            return entity.getColor() == value;
        }
    });

    public static final MobStyleWrapper<Horse, Horse.Color> HORSE_COLOR = new MobStyleWrapper<>(Horse.class, new MobStyleWriter.WriterEnum<Horse, Horse.Color>() {
        @NotNull
        @Override
        public Class<Horse.Color> getEnumClass() {
            return Horse.Color.class;
        }

        @Override
        public void apply(@NotNull Horse entity, @NotNull Horse.Color value) {
            entity.setColor(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Horse entity, @NotNull Horse.Color value) {
            return entity.getColor() == value;
        }
    });

    public static final MobStyleWrapper<Horse, Horse.Style> HORSE_STYLE = new MobStyleWrapper<>(Horse.class, new MobStyleWriter.WriterEnum<Horse, Horse.Style>() {
        @NotNull
        @Override
        public Class<Horse.Style> getEnumClass() {
            return Horse.Style.class;
        }

        public void apply(@NotNull Horse entity, @NotNull Horse.Style value) {
            entity.setStyle(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Horse entity, @NotNull Horse.Style value) {
            return entity.getStyle() == value;
        }
    });

    public static final MobStyleWrapper<Fox, Fox.Type> FOX_TYPE = new MobStyleWrapper<>(Fox.class, new MobStyleWriter.WriterEnum<Fox, Fox.Type>() {
        @NotNull
        @Override
        public Class<Fox.Type> getEnumClass() {
            return Fox.Type.class;
        }

        @Override
        public void apply(@NotNull Fox entity, @NotNull Fox.Type value) {
            entity.setFoxType(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Fox entity, @NotNull Fox.Type value) {
            return entity.getFoxType() == value;
        }
    });

    public static final MobStyleWrapper<Creeper, Boolean> CREEPER_CHARGE = new MobStyleWrapper<>(Creeper.class, new MobStyleWriter.WriterBoolean<>() {
        @Override
        public void apply(@NotNull Creeper entity, @NotNull Boolean value) {
            entity.setPowered(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Creeper entity, @NotNull Boolean value) {
            return entity.isPowered() == value;
        }
    });

    public static final MobStyleWrapper<Slime, Integer> SLIME_SIZE = new MobStyleWrapper<>(Slime.class, new MobStyleWriter<>() {
        @NotNull
        @Override
        public Integer parse(@NotNull String value) {
            return NumberUtil.getInteger(value, 1);
        }

        @NotNull
        @Override
        public Integer[] values() {
            return IntStream.range(1, 16).boxed().toArray(Integer[]::new);
        }

        @Override
        public void apply(@NotNull Slime entity, @NotNull Integer value) {
            entity.setSize(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Slime entity, @NotNull Integer value) {
            return entity.getSize() == value;
        }
    });

    public static final MobStyleWrapper<Phantom, Integer> PHANTOM_SIZE = new MobStyleWrapper<>(Phantom.class, new MobStyleWriter<>() {
        @NotNull
        @Override
        public Integer parse(@NotNull String value) {
            return NumberUtil.getInteger(value, 1);
        }

        @NotNull
        @Override
        public Integer[] values() {
            return IntStream.range(1, 16).boxed().toArray(Integer[]::new);
        }

        @Override
        public void apply(@NotNull Phantom entity, @NotNull Integer value) {
            entity.setSize(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Phantom entity, @NotNull Integer value) {
            return entity.getSize() == value;
        }
    });

    public static final MobStyleWrapper<Llama, Llama.Color> LLAMA_COLOR = new MobStyleWrapper<>(Llama.class, new MobStyleWriter.WriterEnum<Llama, Llama.Color>() {
        @NotNull
        @Override
        public Class<Llama.Color> getEnumClass() {
            return Llama.Color.class;
        }

        @Override
        public void apply(@NotNull Llama entity, @NotNull Llama.Color value) {
            entity.setColor(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Llama entity, @NotNull Llama.Color value) {
            return entity.getColor() == value;
        }
    });

    public static final MobStyleWrapper<Parrot, Parrot.Variant> PARROT_VARIANT = new MobStyleWrapper<>(Parrot.class, new MobStyleWriter.WriterEnum<Parrot, Parrot.Variant>() {
        @NotNull
        @Override
        public Class<Parrot.Variant> getEnumClass() {
            return Parrot.Variant.class;
        }

        @Override
        public void apply(@NotNull Parrot entity, @NotNull Parrot.Variant value) {
            entity.setVariant(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Parrot entity, @NotNull Parrot.Variant value) {
            return entity.getVariant() == value;
        }
    });

    public static final MobStyleWrapper<Rabbit, Rabbit.Type> RABBIT_TYPE = new MobStyleWrapper<>(Rabbit.class, new MobStyleWriter.WriterEnum<Rabbit, Rabbit.Type>() {
        @NotNull
        @Override
        public Class<Rabbit.Type> getEnumClass() {
            return Rabbit.Type.class;
        }

        @Override
        public void apply(@NotNull Rabbit entity, @NotNull Rabbit.Type value) {
            entity.setRabbitType(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Rabbit entity, @NotNull Rabbit.Type value) {
            return entity.getRabbitType() == value;
        }
    });

    public static final MobStyleWrapper<Cat, Cat.Type> CAT_TYPE = new MobStyleWrapper<>(Cat.class, new MobStyleWriter.WriteCat<>() {

        @Override
        public void apply(@NotNull Cat entity, @NotNull Cat.Type value) {
            entity.setCatType(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Cat entity, @NotNull Cat.Type value) {
            return entity.getCatType() == value;
        }
    });

    public static final MobStyleWrapper<MushroomCow, MushroomCow.Variant> MUSHROOM_VARIANT = new MobStyleWrapper<>(MushroomCow.class, new MobStyleWriter.WriterEnum<MushroomCow, MushroomCow.Variant>() {
        @NotNull
        @Override
        public Class<MushroomCow.Variant> getEnumClass() {
            return MushroomCow.Variant.class;
        }

        @Override
        public void apply(@NotNull MushroomCow entity, @NotNull MushroomCow.Variant value) {
            entity.setVariant(value);
        }

        @Override
        public boolean alreadyHas(@NotNull MushroomCow entity, @NotNull MushroomCow.Variant value) {
            return entity.getVariant() == value;
        }
    });

    public static final MobStyleWrapper<Villager, Villager.Profession> VILLAGER = new MobStyleWrapper<>(Villager.class, new MobStyleWriter.WriteVillagerProfession<>() {

        @Override
        public void apply(@NotNull Villager entity, @NotNull Villager.Profession value) {
            entity.setProfession(value);
        }

        @Override
        public boolean alreadyHas(@NotNull Villager entity, @NotNull Villager.Profession value) {
            return entity.getProfession() == value;
        }
    });

    public static final MobStyleWrapper<ZombieVillager, Villager.Profession> VILLAGER_ZOMBIE = new MobStyleWrapper<>(ZombieVillager.class, new MobStyleWriter.WriteVillagerProfession<>() {

        @Override
        public void apply(@NotNull ZombieVillager entity, @NotNull Villager.Profession value) {
            entity.setVillagerProfession(value);
        }

        @Override
        public boolean alreadyHas(@NotNull ZombieVillager entity, @NotNull Villager.Profession value) {
            return false;
        }
    });
}