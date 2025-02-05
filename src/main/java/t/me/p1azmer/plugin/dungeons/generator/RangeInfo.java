package t.me.p1azmer.plugin.dungeons.generator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RangeInfo {
    String world;
    int startX;
    int startZ;
    int distanceMin;
    int distanceMax;
    Set<Material> materials;
    Set<Biome> biomes;
    boolean biomesAsBlack, materialsAsBlack, onlyGeneratedChunks;


    @NotNull
    public static RangeInfo read(@NotNull JYML cfg, @NotNull String path, @NotNull String world) {
        int startX = cfg.getInt(path + ".Start_X");
        int startZ = cfg.getInt(path + ".Start_Z");
        int distanceMin = cfg.getInt(path + ".Distance_Min");
        int distanceMax = cfg.getInt(path + ".Distance_Max");

        if (distanceMin < 0) {
            distanceMin = Math.abs(distanceMin);
            DungeonPlugin.getLog().warning("Distance_Min was negative in configuration. Converted to positive: " + distanceMin);
        }

        if (distanceMax < distanceMin) {
            DungeonPlugin.getLog().warning("Invalid value for Distance_Max in configuration: " + distanceMax +
                    ". It must be greater than or equal to Distance_Min (" + distanceMin + ").");
            distanceMax = distanceMin;
        }

        boolean materialsAsBlack = cfg.getBoolean(path + ".Material.Use_As_Blacklist", true);
        boolean biomesAsBlack = cfg.getBoolean(path + ".Biome.Use_As_Blacklist", true); // Исправлено: было Material.Use_As_Blacklist
        boolean onlyGeneratedChunks = cfg.getBoolean(path + ".Only_Generated_Chunks", false);

        Set<String> materialList = cfg.getStringSet(path + ".Material.List");
        AtomicInteger nullMaterial = new AtomicInteger();

        Set<Material> materials = materialList.stream()
                .map(sId -> StringUtil.getEnum(sId, Material.class).orElse(null))
                .filter(material -> {
                    if (material == null) {
                        nullMaterial.incrementAndGet();
                        return false;
                    }
                    return true;
                }).collect(Collectors.toSet());

        if (nullMaterial.get() > 0) {
            DungeonAPI.PLUGIN.warn("Attention! The location generator has wrong materials in the configuration. Please note this and check it!");
        }

        Set<String> biomeList = cfg.getStringSet(path + ".Biome.List");
        Set<Biome> biomes = biomeList.stream().map(biomeName -> {
            try {
                if (Version.isBehind(Version.V1_19_R1)) {
                    return Biome.valueOf(biomeName);
                } else {
                    return Registry.BIOME.get(NamespacedKey.minecraft(biomeName.toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalArgumentException e) {
                DungeonAPI.PLUGIN.warn("Invalid biome name in configuration: " + biomeName);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        return new RangeInfo(
                world, startX, startZ, distanceMin, distanceMax,
                materials, biomes, biomesAsBlack, materialsAsBlack, onlyGeneratedChunks
        );
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Start_X", this.getStartX());
        cfg.set(path + ".Start_Z", this.getStartZ());
        cfg.set(path + ".Distance_Min", this.getDistanceMin());
        cfg.set(path + ".Distance_Max", this.getDistanceMax());

        cfg.set(path + ".Material.Use_As_Blacklist", this.isMaterialsAsBlack());
        cfg.set(path + ".Biome.Use_As_Blacklist", this.isBiomesAsBlack());
        cfg.set(path + ".Only_Generated_Chunks", this.isOnlyGeneratedChunks());

        cfg.set(path + ".Material.List", this.getMaterials().stream().map(Enum::name).collect(Collectors.toList()));
        if (Version.isBehind(Version.V1_19_R1)) {
            cfg.set(path + ".Biome.List", this.getBiomes().stream().map(Biome::name).collect(Collectors.toList()));
        } else {
            cfg.set(path + ".Biome.List", this.getBiomes().stream().map(biome -> biome.getKey().getKey()).collect(Collectors.toList()));
        }
    }

    @NotNull
    public String getWorldRaw() {
        return world;
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(this.getWorldRaw()));
    }

    @NotNull
    public Set<String> getBiomesString() {
        return this.getBiomes().stream().map(Biome::translationKey).collect(Collectors.toSet());
    }

    @NotNull
    public Set<String> getMaterialsString() {
        return this.getMaterials().stream().map(Enum::name).collect(Collectors.toSet());
    }
}
