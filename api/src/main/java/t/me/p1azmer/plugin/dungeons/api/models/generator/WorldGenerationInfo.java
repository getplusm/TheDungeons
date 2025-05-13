package t.me.p1azmer.plugin.dungeons.api.models.generator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Optional;
import java.util.Set;

@Getter
@ConfigSerializable
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("FieldMayBeFinal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldGenerationInfo {
    String worldName;

    int startX;
    int startZ;
    int distanceMin;
    int distanceMax;

    Set<Material> materials;
    Set<String> biomes;

    boolean biomesAsBlack;
    boolean materialsAsBlack;
    boolean onlyGeneratedChunks;

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldName));
    }

    public @NotNull World getWorldOrThrow() {
        return getWorld().orElseThrow(() -> new IllegalArgumentException("World '" + worldName + "' not found!"));
    }
}
