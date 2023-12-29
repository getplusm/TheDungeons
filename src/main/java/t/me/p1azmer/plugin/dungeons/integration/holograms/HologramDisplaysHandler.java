package t.me.p1azmer.plugin.dungeons.integration.holograms;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

import java.util.*;

public class HologramDisplaysHandler implements HologramHandler {

    private HolographicDisplaysAPI hologramAPI;
    private Map<String, Set<Pair<DungeonChestBlock, Hologram>>> holoMap;

    private final DungeonPlugin plugin;

    public HologramDisplaysHandler(@NotNull DungeonPlugin plugin) {
        this.plugin = plugin;
        this.hologramAPI = HolographicDisplaysAPI.get(plugin);
    }

    @Override
    public void setup() {
        this.holoMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.holoMap != null) {
            this.holoMap.values().forEach(set -> set.forEach(pair -> pair.getSecond().delete()));
            this.holoMap = null;
        }
        if (this.hologramAPI != null) {
            this.hologramAPI = null;
        }
    }

    @Override
    public void create(@NotNull Dungeon dungeon, @Nullable ChestModule module) {
        if (module == null) return;
        plugin.runTask(sync -> {
            List<String> messages;
            Set<Pair<DungeonChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());
            for (DungeonChestBlock dungeonChestBlock : module.getChests()) {
                Block block = dungeonChestBlock.getBlock();

                messages = new ArrayList<>(dungeonChestBlock.getDungeon().getHologramSettings().getMessages(dungeonChestBlock.getState()));
                messages.replaceAll(dungeonChestBlock.replacePlaceholders());
                Hologram hologram = this.hologramAPI.createHologram(this.fineLocation(dungeonChestBlock, block.getLocation()));
                for (String line : messages) {
                    hologram.getLines().appendText(Colorizer.apply(line));
                }
                holograms.add(Pair.of(dungeonChestBlock, hologram));
            }
        });
    }

    @NotNull
    private Location fineLocation(@NotNull DungeonChestBlock dungeonChestBlock, @NotNull Location location) {
        return LocationUtil.getCenter(location.clone()).add(0D, dungeonChestBlock.getDungeon().getHologramSettings().getOffsetY(), 0D);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Set<Pair<DungeonChestBlock, Hologram>> set = this.holoMap.remove(dungeon.getId());
        if (set == null) return;

        set.forEach(pair -> pair.getSecond().delete());
    }

    @Override
    public void update(@NotNull DungeonChestBlock dungeonChestBlock) {
        plugin.runTask(sync -> {
            Set<Pair<DungeonChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeonChestBlock.getDungeon().getId(), set -> new HashSet<>());
            holograms.stream().filter(f -> f.getFirst().equals(dungeonChestBlock)).map(Pair::getSecond).toList().forEach(hologram -> {
                List<String> messages = new ArrayList<>(dungeonChestBlock.getDungeon().getHologramSettings().getMessages(dungeonChestBlock.getState()));
                messages.replaceAll(dungeonChestBlock.replacePlaceholders());
                updateHologramLines(dungeonChestBlock, hologram, messages);
            });
        });
    }

    private void updateHologramLines(@NotNull DungeonChestBlock dungeonChestBlock, @NotNull Hologram hologram, @NotNull List<String> message) {
        HologramLines lines = hologram.getLines();
        int lineCount = lines.size();
        for (int i = 0; i < Math.min(lineCount, message.size()); i++) {
            if (lines.get(i) instanceof TextHologramLine line) {
                String originalText = line.getText();
                String newText = message.get(i);
                newText = dungeonChestBlock.replacePlaceholders().apply(newText);
                newText = dungeonChestBlock.getDungeon().getSettings().replacePlaceholders().apply(newText);
                newText = dungeonChestBlock.getDungeon().replacePlaceholders().apply(newText);

                if (originalText == null || originalText.isEmpty() || !originalText.equals(newText)) {
                    line.setText(Colorizer.apply(newText));
                }
            }
        }

        if (message.size() < lineCount) {
            for (int i = lineCount - 1; i >= message.size(); i--) {
                HologramLine line = lines.get(i);
                hologram.getLines().remove(line);
            }
        } else if (message.size() > lineCount) {
            for (int i = lineCount; i < message.size(); i++) {
                String newText = message.get(i);
                newText = dungeonChestBlock.replacePlaceholders().apply(newText);
                newText = dungeonChestBlock.getDungeon().getSettings().replacePlaceholders().apply(newText);
                newText = dungeonChestBlock.getDungeon().replacePlaceholders().apply(newText);
                hologram.getLines().appendText(Colorizer.apply(newText));
            }
        }
    }
}