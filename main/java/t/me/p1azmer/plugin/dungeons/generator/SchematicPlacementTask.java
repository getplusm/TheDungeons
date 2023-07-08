package t.me.p1azmer.plugin.dungeons.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.Placeholders;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SchematicPlacementTask extends BukkitRunnable {
    private static final Map<Location, EditSession> editSessionMap = new HashMap<>();
    private final Location location;
    private final File schematicFile;
    private final int maxAttempts;
    private int currentAttempt;

    public SchematicPlacementTask(@NotNull Location location, @NotNull File schematicFile, int maxAttempts) {
        this.location = location;
        this.schematicFile = schematicFile;
        this.maxAttempts = maxAttempts;
        this.currentAttempt = 0;
    }

    @Override
    public void run() {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World at location '" + Placeholders.LOCATION.replacer(location) + "' is null");
        }
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        if (clipboardFormat == null) {
            System.out.println("File " + schematicFile.getName() + " not found for schematic!");
            return;
        }

        SchematicLoadTask task = new SchematicLoadTask(schematicFile, clipboardFormat);
        Actor actor = DungeonAPI.PLUGIN.getSessionConsole();
        LocalSession session = DungeonAPI.PLUGIN.getSessionConsole();
        session.clearHistory();

        ClipboardHolder holder;
        try {
            holder = task.call();
        } catch (Exception e) {
            throw new RuntimeException("Cannot load schematic!", e);
        }

        session.setClipboard(holder);
        BlockVector3 to = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        try (EditSession editSession = DungeonAPI.PLUGIN.getWorldEdit().getEditSessionFactory().getEditSession(weWorld, -1, actor)) {

            Operation operation = session.getClipboard()
                    .createPaste(editSession)
                    .to(to)
                    .ignoreAirBlocks(false)
                    .copyBiomes(false)
                    .copyEntities(true)
//                    .maskSource(sourceMask)
                    .build();
            Operations.completeLegacy(operation);
            editSessionMap.put(location, editSession);

            Clipboard clipboard = session.getClipboard().getClipboard();
            Region region = clipboard.getRegion();

            BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
            Vector3 realTo = to.toVector3().add(holder.getTransform().apply(clipboardOffset.toVector3()));
            Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
            RegionSelector selector = new CuboidRegionSelector(weWorld, realTo.toBlockPoint(), max.toBlockPoint());
            session.setRegionSelector(weWorld, selector);
            selector.learnChanges();
            selector.explainRegionAdjust(actor, session);
            cancel(); // Останавливаем выполнение задачи после успешной вставки
        } catch (NullPointerException e) {
            e.printStackTrace();

            currentAttempt++;
            if (currentAttempt >= maxAttempts) {
                cancel(); // Останавливаем выполнение задачи после достижения максимального количества попыток
            }
        } catch (WorldEditException e) {
            throw new RuntimeException("Reach limit of block change", e);
        }
    }


    public static void restore(Location location) {
        try {
            Actor actor = DungeonAPI.PLUGIN.getSessionConsole();
            EditSession editSession = editSessionMap.get(location);
            BlockBag blockBag = editSession.getBlockBag();
            LocalSession session = DungeonAPI.PLUGIN.getSessionConsole();
            session.undo(editSession.getBlockBag(), actor);

            try (EditSession newEditSession = WorldEdit.getInstance().getEditSessionFactory()
                    .getEditSession(editSession.getWorld(), -1, blockBag, actor)) {
                editSession.setFastMode(true);
                editSession.setReorderMode(EditSession.ReorderMode.FAST);
                editSession.setTickingWatchdog(true);
                editSession.undo(newEditSession);
            }

            DungeonAPI.PLUGIN.getWorldEdit().flushBlockBag(actor, editSession);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Error when restore the region!", ex);
        }
    }

    private static class SchematicLoadTask implements Callable<ClipboardHolder> {
        private final File file;
        private final ClipboardFormat format;

        SchematicLoadTask(File file, ClipboardFormat format) {
            this.file = file;
            this.format = format;
        }

        @Override
        public ClipboardHolder call() throws Exception {
            try (Closer closer = Closer.create()) {
                FileInputStream fis = closer.register(new FileInputStream(file));
                BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                ClipboardReader reader = closer.register(format.getReader(bis));

                Clipboard clipboard = reader.read();
                return new ClipboardHolder(clipboard);
            }
        }
    }
}