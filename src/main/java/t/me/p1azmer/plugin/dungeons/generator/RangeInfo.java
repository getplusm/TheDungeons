package t.me.p1azmer.plugin.dungeons.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RangeInfo {
    private final String world;
    private final int startX;
    private final int startZ;
    private final int distanceMin;
    private final int distanceMax;
    private final List<Material> materials;
    private final List<Biome> biomes;
    private final boolean biomesAsBlack, materialsAsBlack;

    public RangeInfo(
            @NotNull String world,
            int startX, int startZ, int distanceMin, int distanceMax,
            @NotNull List<Material> materials,
            @NotNull List<Biome> biomes,
            boolean biomesAsBlack, boolean materialsAsBlack
    ) {
        this.world = world;

        this.startX = startX;
        this.startZ = startZ;

        this.distanceMin = Math.abs(distanceMin);
        this.distanceMax = Math.abs(distanceMax);

        this.materials = materials;
        this.biomes = biomes;

        this.biomesAsBlack = biomesAsBlack;
        this.materialsAsBlack = materialsAsBlack;
    }

    @NotNull
    public static RangeInfo read(@NotNull JYML cfg, @NotNull String path, @NotNull String world) {
        int startX = cfg.getInt(path + ".Start_X");
        int startZ = cfg.getInt(path + ".Start_Z");
        int distanceMin = cfg.getInt(path + ".Distance_Min");
        int distanceMax = cfg.getInt(path + ".Distance_Max");

        boolean materialsAsBlack = cfg.getBoolean(path + ".Material.Use_As_Blacklist", true);
        boolean biomesAsBlack = cfg.getBoolean(path + ".Material.Use_As_Blacklist", true);

        AtomicInteger nullMaterial = new AtomicInteger();
        AtomicInteger nullBiomes = new AtomicInteger();

        List<String> materialList = cfg.getStringList(path + ".Material.List");
        List<Material> materials = materialList
                .stream()
                .map(sId -> StringUtil.getEnum(sId, Material.class).orElse(null))
                .filter(material -> {
                    if (material == null) {
                        nullMaterial.incrementAndGet();
                        return false;
                    }
                    return true;
                })
                .toList();
        List<String> biomeList = cfg.getStringList(path + ".Biome.List");
        List<Biome> biomes = biomeList
                .stream()
                .map(sId -> StringUtil.getEnum(sId, Biome.class).orElse(null))
                .filter(biome -> {
                    if (biome == null) {
                        nullBiomes.incrementAndGet();
                        return false;
                    }
                    return true;
                })
                .toList();

        if (nullMaterial.get() > 0) {
            DungeonAPI.PLUGIN.warn("Attention! The location generator has wrong materials in the configuration. Please note this and check it!");
        }
        if (nullBiomes.get() > 0) {
            DungeonAPI.PLUGIN.warn("Attention! The location generator has wrong biomes in the configuration. Please note this and check it!");
        }

        return new RangeInfo(world, startX, startZ, distanceMin, distanceMax, materials, biomes, biomesAsBlack, materialsAsBlack);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Start_X", this.getStartX());
        cfg.set(path + ".Start_Z", this.getStartZ());
        cfg.set(path + ".Distance_Min", this.getDistanceMin());
        cfg.set(path + ".Distance_Max", this.getDistanceMax());

        cfg.set(path + ".Material.Use_As_Blacklist", this.isMaterialsAsBlack());
        cfg.set(path + ".Biome.Use_As_Blacklist", this.isBiomesAsBlack());

        cfg.set(path + ".Material.List", this.getMaterials().stream().map(Enum::name).collect(Collectors.toList()));
        cfg.set(path + ".Biome.List", this.getBiomes().stream().map(Enum::name).collect(Collectors.toList()));
    }

    @NotNull
    public String getWorldRaw() {
        return world;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(this.getWorldRaw());
    }

    public int getStartX() {
        return startX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getDistanceMin() {
        return distanceMin;
    }

    public int getDistanceMax() {
        return distanceMax;
    }

    public boolean isBiomesAsBlack() {
        return biomesAsBlack;
    }

    public boolean isMaterialsAsBlack() {
        return materialsAsBlack;
    }

    @NotNull
    public List<Biome> getBiomes() {
        return biomes;
    }

    @NotNull
    public List<String> getBiomesString() {
        return this.getBiomes()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<Material> getMaterials() {
        return materials;
    }

    @NotNull
    public List<String> getMaterialsString() {
        return this.getMaterials().stream().map(Enum::name).collect(Collectors.toList());
    }
}