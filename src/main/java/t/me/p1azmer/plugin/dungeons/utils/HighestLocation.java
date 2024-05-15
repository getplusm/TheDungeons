package t.me.p1azmer.plugin.dungeons.utils;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class HighestLocation {

    public static int getHighest(World world) throws ExecutionException, InterruptedException {
       
        // Some arbitrary coordinates you might want to scan.
        final int chunkX = 5, chunkZ = 5;
        final int columnX = 5 << 4, columnZ = 5 << 4;

        // An async safe snapshot of the chunk.
        final ChunkSnapshot snapshot = world.getEmptyChunkSnapshot(5, 5, false, false);

        // Scan from 255 to 150.
        CompletableFuture<Integer> topScan = CompletableFuture.supplyAsync(new Supplier<>() {
          /** Returns the highest block. */
          @Override
          public Integer get() {
            for (int y = 255; y > 185; y--) {
              if (snapshot.getBlockType(columnX, y, columnZ) != Material.AIR)
                return y;
            }
            return -1;
          }
        });
        // Scan from 150 to 80.
        CompletableFuture<Integer> middleScan = CompletableFuture.supplyAsync(new Supplier<>() {
          /** Returns the highest block. */
          @Override
          public Integer get() {
            for (int y = 185; y > 105; y--) {
              if (snapshot.getBlockType(columnX, y, columnZ) != Material.AIR)
                return y;
            }
            return -1;
          }
        });
        // Scan drom 80 to 0.
        CompletableFuture<Integer> bottomScan = CompletableFuture.supplyAsync(new Supplier<>() {
          /** Returns the highest block. */
          @Override
          public Integer get() {
            for (int y = 80; y > 0; y--) {
              if (snapshot.getBlockType(columnX, y, columnZ) != Material.AIR)
                return y;
            }
            return -1;
          }
        });

        int topValue = topScan.get();
        int middleValue = middleScan.get();
        int bottomValue = bottomScan.get();

        if (topValue != -1)
            return topValue;
        else if (middleValue != -1)
            return middleValue;
        else
            return bottomValue;
    }
}
 