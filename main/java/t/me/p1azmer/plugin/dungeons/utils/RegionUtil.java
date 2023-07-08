package t.me.p1azmer.plugin.dungeons.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class RegionUtil {
    public static void createRegion(Location start, Location end, String name) {
        ProtectedRegion region = new ProtectedCuboidRegion(name, convertToSk89qBV(start), convertToSk89qBV(end));
        Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Objects.requireNonNull(end.getWorld())))).addRegion(region);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region flag -w " + end.getWorld().getName() + " " + name + " pistons deny");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region flag -w " + end.getWorld().getName() + " " + name + " pvp allow");
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region flag -w " + end.getWorld().getName() + " " + name + " sixtrap-unjob deny");
    }

    public static BlockVector3 convertToSk89qBV(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    public static void removeRegion(String name, World w) {
        Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))).removeRegion(name);
    }
}
