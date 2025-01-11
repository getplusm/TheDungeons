package t.me.p1azmer.plugin.dungeons.api.mob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.random.Rnd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MobList {

    Map<MobFaction, Set<LivingEntity>> map = new HashMap<>();

    public boolean hasAliveEnemies() {
        return !this.getEnemies().isEmpty();
    }

    public boolean hasAliveAllies() {
        return !this.getAllies().isEmpty();
    }

    public boolean isAlly(@NotNull LivingEntity entity) {
        return this.getAllies().contains(entity);
    }

    public boolean isEnemy(@NotNull LivingEntity entity) {
        return this.getEnemies().contains(entity);
    }

    @Nullable
    public MobFaction getFaction(@NotNull LivingEntity entity) {
        if (this.isAlly(entity)) return MobFaction.ALLY;
        if (this.isEnemy(entity)) return MobFaction.ENEMY;
        return null;
    }

    public void remove(@NotNull LivingEntity entity) {
        for (MobFaction faction : MobFaction.values()) {
            if (this.getAll(faction).remove(entity)) {
                break;
            }
        }
    }

    public void removeInvalid() {
        for (MobFaction faction : MobFaction.values()) {
            this.removeInvalid(faction);
        }
    }

    public void removeInvalid(@NotNull MobFaction faction) {
        this.getAll(faction).removeIf(e -> e.isDead() || !e.isValid());
    }

    public void removeAll() {
        for (MobFaction faction : MobFaction.values()) {
            this.removeAll(faction);
        }
    }

    public void removeAll(@NotNull MobFaction faction) {
        getAll(faction).forEach(Entity::remove);
        getAll(faction).clear();
    }

    @Nullable
    public LivingEntity getRandom(@NotNull MobFaction faction) {
        Set<LivingEntity> set = this.getAll(faction);
        return set.isEmpty() ? null : Rnd.get(this.getAll(faction));
    }

    @NotNull
    public Set<LivingEntity> getEnemies() {
        return this.getAll(MobFaction.ENEMY);
    }

    @NotNull
    public Set<LivingEntity> getAllies() {
        return this.getAll(MobFaction.ALLY);
    }

    @NotNull
    public Set<LivingEntity> getAll() {
        return this.getMap().entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toSet());
    }

    @NotNull
    public Set<LivingEntity> getAll(@NotNull MobFaction faction) {
        return this.getMap().computeIfAbsent(faction, k -> new HashSet<>());
    }
}