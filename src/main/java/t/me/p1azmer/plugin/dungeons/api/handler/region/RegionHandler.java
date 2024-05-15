package t.me.p1azmer.plugin.dungeons.api.handler.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;

public interface RegionHandler extends Loadable {

  void create(@NotNull Dungeon dungeon);

  void delete(@NotNull Dungeon dungeon);

  boolean isValidLocation(@NotNull Location location);

  boolean isDungeonRegion(@NotNull Location location, @NotNull Region region);
}