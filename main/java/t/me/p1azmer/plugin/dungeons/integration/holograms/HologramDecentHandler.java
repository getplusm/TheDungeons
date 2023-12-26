package t.me.p1azmer.plugin.dungeons.integration.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.api.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

import java.util.*;

public class HologramDecentHandler implements HologramHandler {

    private Map<String, Set<Pair<DungeonChestBlock, Hologram>>> holoMap;

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
    }

    @Override
    public void create(@NotNull Dungeon dungeon, @Nullable ChestModule module) {
        if (module == null) return;

        List<String> messages;
        Set<Pair<DungeonChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());
        for (DungeonChestBlock dungeonChestBlock : module.getChests()) {
            Block block = dungeonChestBlock.getBlock();

            messages = new ArrayList<>(dungeonChestBlock.getDungeon().getHologramSettings().getMessages(dungeonChestBlock.getState()));
            messages.replaceAll(dungeonChestBlock.replacePlaceholders());

            Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), fineLocation(dungeonChestBlock, block.getLocation()), Colorizer.apply(messages));
            hologram.showAll();
            holograms.add(Pair.of(dungeonChestBlock, hologram));
        }
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
        Set<Pair<DungeonChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeonChestBlock.getDungeon().getId(), set -> new HashSet<>());
        holograms.stream().filter(f -> f.getFirst().equals(dungeonChestBlock)).map(Pair::getSecond).toList().forEach(hologram -> {
            List<String> messages = new ArrayList<>(dungeonChestBlock.getDungeon().getHologramSettings().getMessages(dungeonChestBlock.getState()));
            messages.replaceAll(dungeonChestBlock.replacePlaceholders());
            updateHologramLines(dungeonChestBlock, hologram, messages);
        });
    }

    private void updateHologramLines(@NotNull DungeonChestBlock dungeonChestBlock, @NotNull Hologram hologram, @NotNull List<String> message) {
        List<HologramLine> lines = hologram.getPage(0).getLines();
        int lineCount = lines.size();

        if (message.size() < lineCount) {
            for (int i = lineCount - 1; i >= message.size(); i--) {
                DHAPI.removeHologramLine(hologram, 0, i);
            }
        } else if (message.size() > lineCount) {
            for (int i = lineCount; i < message.size(); i++) {
                String newText = message.get(i);
                newText = dungeonChestBlock.getDungeon().getSettings().replacePlaceholders().apply(newText);
                newText = dungeonChestBlock.getDungeon().replacePlaceholders().apply(newText);
                DHAPI.addHologramLine(hologram, 0, Colorizer.apply(newText));
            }
        }

        for (int i = 0; i < lineCount; i++) {
            HologramLine line = lines.get(i);
            String originalText = line.getText();
            String newText = message.get(i);
            newText = dungeonChestBlock.getDungeon().getSettings().replacePlaceholders().apply(newText);
            newText = dungeonChestBlock.replacePlaceholders().apply(newText);
            newText = dungeonChestBlock.getDungeon().replacePlaceholders().apply(newText);

            if (originalText == null || originalText.isEmpty() || !originalText.equals(newText)) {
                line.setText(Colorizer.apply(newText));
            }
        }
        hologram.updateAll();
        hologram.updateAnimationsAll();
    }
}