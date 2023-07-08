package t.me.p1azmer.engine.hooks.external;

//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.Entity;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import t.me.p1azmer.plugin.regioncommand.api.Region;
//import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
//import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
//
//import static t.me.hooks.engine.t.me.p1azmer.engine.Hooks.hasRegionCommand;
//
//public class RegionCommandHook {
//
//    static RegionManager regionManager = RegionAPI.PLUGIN.getManager();
//
//    public static boolean canFights(@NotNull Entity damager, @NotNull Entity victim) {
//        return hasRegionCommand(); //&& regionManager.inRegion(damager, victim)
//    }
//
//    public static boolean isInRegion(@NotNull Entity entity, @NotNull String region) {
//        return hasRegionCommand() && getRegion(entity).equalsIgnoreCase(region);
//    }
//
//    @NotNull
//    public static String getRegion(@NotNull Entity entity) {
//        return hasRegionCommand() ? getRegion(entity.getLocation()) : "";
//    }
//
//    @NotNull
//    public static String getRegion(@NotNull Location loc) {
//        Region region = getProtectedRegion(loc);
//        return hasRegionCommand() ? region == null ? "" : region.getName() : "";
//    }
//
//    @Nullable
//    public static Region getProtectedRegion(@NotNull Entity entity) {
//        return hasRegionCommand() ? getProtectedRegion(entity.getLocation()) : null;
//    }
//
//    @Nullable
//    public static Region getProtectedRegion(@NotNull Location location) {
//        if (!hasRegionCommand()) return null;
//        World world = location.getWorld();
//        if (world == null) return null;
//        return regionManager.getRegion(location);
//    }
//}