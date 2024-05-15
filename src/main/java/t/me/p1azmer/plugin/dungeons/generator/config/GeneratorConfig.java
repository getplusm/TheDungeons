package t.me.p1azmer.plugin.dungeons.generator.config;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.plugin.dungeons.generator.RangeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorConfig {

  public static final JOption<Map<String, RangeInfo>> LOCATION_SEARCH_RANGES = new JOption<>("Settings.Generator.Location.Search.Ranges",
    (cfg, path, def) -> {
      Map<String, RangeInfo> map = new HashMap<>();
      for (String world : cfg.getSection(path)) {
        RangeInfo rangeInfo = RangeInfo.read(cfg, path + "." + world, world);
        map.put(world, rangeInfo);
      }
      return map;
    },
    Map.of("world", new RangeInfo("world", 0, 0, -5000, 5000, List.of(Material.WATER, Material.LAVA), List.of(Biome.WARM_OCEAN), true, true, true)),
    "List of per-world range values to search location in.",
    "Put your ACTUAL worlds here, especially if your dungeon uses a different world than the default 'world'.",
    "Note:",
    "The Use_As_Blacklist setting is designed for your convenience.",
    "You can choose whether to use the list as a whitelist or as a blacklist"
  ).setWriter((cfg, path, map) -> map.forEach((world, rangeInfo) -> rangeInfo.write(cfg, path + "." + world)));
}