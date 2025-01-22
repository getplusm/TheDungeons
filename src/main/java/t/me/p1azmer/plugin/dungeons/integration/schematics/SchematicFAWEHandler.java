package t.me.p1azmer.plugin.dungeons.integration.schematics;

import com.sk89q.worldedit.*;
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
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.utils.SessionConsole;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchematicFAWEHandler implements SchematicHandler {
    Map<Location, EditSession> editSessionMap = new ConcurrentHashMap<>();
    Map<Dungeon, Location> placedMap = new ConcurrentHashMap<>();
    WorldEdit worldEdit = WorldEdit.getInstance();

    SessionConsole sessionConsole;

    @Override
    public void setup() {
    }

    @Override
    public void shutdown() {
        this.placedMap.forEach((dungeon, location) -> this.undo(dungeon));

        this.editSessionMap.clear();
        this.placedMap.clear();
    }

    @Override
    public boolean paste(@NotNull Dungeon dungeon, @NotNull File schematicFile) {
        Location location = dungeon.getLocation().orElse(null);
        if (location == null) {
            DungeonPlugin.getLog().severe("Dungeon '" + dungeon.getId() + "' has no location!");
            return false;
        }
        if (!this.undo(dungeon)) {
            DungeonPlugin.getLog().severe("Dungeon '" + dungeon.getId() + "' cannot undo");
            return false;
        }

        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World at location '" + Placeholders.forLocation(location).apply("%location_x%, %location_y%, %location_z%, %location_world%") + "' is null");
        }

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        if (clipboardFormat == null) {
            throw new IllegalArgumentException("Schematic '" + schematicFile + "' not found!");
        }

        SchematicLoadTask loadTask = new SchematicLoadTask(schematicFile, clipboardFormat);
        LocalSession session = sessionConsole;
        session.clearHistory();

        ClipboardHolder holder;
        try {
            holder = loadTask.call();
        } catch (Exception e) {
            throw new RuntimeException("Dungeon '" + dungeon.getId() + "' cannot spawn but schematic '" + schematicFile.getName() + "' not loaded!", e.getCause());
        }

        BlockVector3 toVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        try (EditSession editSession = this.worldEdit.newEditSession(weWorld)) {
            editSession.setReorderMode(EditSession.ReorderMode.MULTI_STAGE);

            Operation operation = holder.createPaste(editSession)
                    .to(toVector)
                    .ignoreAirBlocks(dungeon.getSchematicSettings().isIgnoreAirBlocks())
                    .copyEntities(true)
                    .build();
            Operations.complete(operation);
            Clipboard clipboard = holder.getClipboard();
            if (clipboard == null) return false;

            Region region = clipboard.getRegion();

            BlockVector3 minimumPoint = region.getMinimumPoint();
            BlockVector3 maximumPoint = region.getMaximumPoint();

            BlockVector3 clipboardOffset = minimumPoint.subtract(clipboard.getOrigin());
            Vector3 realTo = toVector.toVector3().add(holder.getTransform().apply(clipboardOffset.toVector3()));
            Vector3 max = realTo.add(holder.getTransform().apply(maximumPoint.subtract(region.getMinimumPoint()).toVector3()));
            RegionSelector selector = new CuboidRegionSelector(weWorld, realTo.toBlockPoint(), max.toBlockPoint());

            session.setRegionSelector(weWorld, selector);
            selector.learnChanges();
            selector.explainRegionAdjust(sessionConsole, session);

            this.extracted(dungeon, location, editSession);
            return true;
        } catch (WorldEditException e) {
            throw new RuntimeException("Reach limit of block change when paste the schematic at '" + dungeon.getId() + "' dungeon!");
        }
    }

    private void extracted(@NotNull Dungeon dungeon, @NotNull Location location, @NotNull EditSession editSession) {
        editSessionMap.put(location, editSession);
        placedMap.put(dungeon, location);
    }

    @Override
    public boolean undo(@NotNull Dungeon dungeon) {
        if (!this.placedMap.containsKey(dungeon)) return true;

        Location location = this.placedMap.get(dungeon);
        if (location == null) return true;

        boolean contained = editSessionMap.containsKey(location);
        if (!contained) return true;

        try {
            Actor actor = sessionConsole;
            EditSession editSession = editSessionMap.get(location);
            if (editSession == null) return false;

            BlockBag blockBag = editSession.getBlockBag();

            sessionConsole.setWorldOverride(editSession.getWorld());
            EditSessionBuilder sessionBuilder = WorldEdit.getInstance().newEditSessionBuilder();

            try (EditSession newEditSession = sessionBuilder.blockBag(blockBag).actor(actor).world(editSession.getWorld()).build()) {
                editSession.undo(newEditSession);
            }

            this.worldEdit.flushBlockBag(actor, editSession);
            this.placedMap.remove(dungeon, location);
            return true;
        } catch (NullPointerException ex) {
            throw new RuntimeException("Error when restore the region!", ex);
        }
    }

    @Override
    public boolean containsChestBlock(@NotNull Dungeon dungeon, @NotNull File schematicFile) {
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        if (clipboardFormat == null) return false;

        SchematicLoadTask task = new SchematicLoadTask(schematicFile, clipboardFormat);
        ClipboardHolder holder;
        try {
            holder = task.call();
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while trying to check chest block", exception);
            return false;
        }
        Clipboard clipboard = holder.getClipboard();
        if (clipboard == null) {
            DungeonPlugin.getLog().severe("The schematic module returned the following error: Clipboard for file not found or invalid");
            return false;
        }
        BlockVector3 minPoint = clipboard.getMinimumPoint();
        BlockVector3 maxPoint = clipboard.getMaximumPoint();

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
                for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                    BlockVector3 blockLocation = BlockVector3.at(x, y, z);
                    BlockState block = clipboard.getBlock(blockLocation);
                    if (block == null) continue;
                    if (block.getBlockType() == null) continue;
                    if (block.getBlockType().getMaterial().isAir()) continue;

                    Material material = dungeon.getChestSettings().getMaterial();
                    String materialName = material.name();
                    String id = "minecraft:" + materialName.toLowerCase(Locale.ROOT);

                    BlockType chestBlock = BlockTypes.get(id);
                    if (block.getBlockType().equals(chestBlock)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int getAmountOfChestBlocks(@NotNull Dungeon dungeon, @NotNull File schematicFile) {
        if (!this.containsChestBlock(dungeon, schematicFile)) return 0;

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        if (clipboardFormat == null) return 0;

        SchematicLoadTask task = new SchematicLoadTask(schematicFile, clipboardFormat);
        ClipboardHolder holder;
        try {
            holder = task.call();
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Got exception while trying to get amount of chest blocks", exception);
            return 0;
        }
        Clipboard clipboard = holder.getClipboard();
        BlockVector3 minPoint = clipboard.getMinimumPoint();
        BlockVector3 maxPoint = clipboard.getMaximumPoint();

        int amount = 0;

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
                for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                    BlockVector3 blockLocation = BlockVector3.at(x, y, z);
                    BlockState block = clipboard.getBlock(blockLocation);
                    if (block == null) continue;
                    if (block.getBlockType() == null) continue;
                    if (block.getBlockType().getMaterial().isAir()) continue;

                    Material material = dungeon.getChestSettings().getMaterial();
                    String materialName = material.name();
                    String id = "minecraft:" + materialName.toLowerCase(Locale.ROOT);

                    BlockType chestBlock = BlockTypes.get(id);
                    if (block.getBlockType().equals(chestBlock)) {
                        amount++;
                    }
                }
            }
        }
        return amount;
    }


    private record SchematicLoadTask(@NotNull File file,
                                     @NotNull ClipboardFormat format) implements Callable<ClipboardHolder> {

        static Map<File, ClipboardHolder> holderCache = new WeakHashMap<>();

        @Override
        public ClipboardHolder call() throws Exception {
            ClipboardHolder holder = holderCache.get(this.file());
            if (holder == null)
                try (Closer closer = Closer.create()) {
                    FileInputStream fis = closer.register(new FileInputStream(file));
                    BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                    ClipboardReader reader = closer.register(format.getReader(bis));

                    Clipboard clipboard = reader.read();
                    holder = new ClipboardHolder(clipboard);
                }
            holderCache.put(this.file(), holder);
            return holder;
        }
    }
}
