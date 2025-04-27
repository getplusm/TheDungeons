package t.me.p1azmer.plugin.dungeons.integration.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.api.handler.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.module.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.module.modules.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.HologramSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.MainSettings;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HologramDecentHandler implements HologramHandler {

    Map<String, Set<Pair<ChestBlock, Hologram>>> holoMap = new HashMap<>();

    @Override
    public void setup() {
    }

    @Override
    public void shutdown() {
        this.holoMap.values()
                .forEach(set -> set.forEach(pair -> pair.getSecond().delete()));
        this.holoMap.clear();
    }

    @Override
    public void create(@NotNull Dungeon dungeon, @NotNull ChestModule module) {
        Set<Pair<ChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());
        List<String> messages;

        try {
            for (ChestBlock chestBlock : module.getChests()) {
                Block block = chestBlock.getBlock();
                HologramSettings hologramSettings = chestBlock.getDungeon().getHologramSettings();
                List<String> messageList = hologramSettings.getMessages(chestBlock.getState());
                messages = new ArrayList<>(messageList);
                messages.replaceAll(chestBlock.replacePlaceholders());

                Location location = fineLocation(chestBlock, block.getLocation());
                Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location, messages);

                hologram.showAll();
                holograms.add(Pair.of(chestBlock, hologram));
            }
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Failed to create hologram for " + module.getId() + " module", exception);
        }
        module.debug("Installed " + holograms.size() + " holograms for " + ModuleId.HOLOGRAM + " Module");
    }

    @NotNull
    private Location fineLocation(@NotNull ChestBlock chestBlock, @NotNull Location location) {
        Dungeon dungeon = chestBlock.getDungeon();
        HologramSettings hologramSettings = dungeon.getHologramSettings();
        return location.toCenterLocation().add(0D, hologramSettings.getOffsetY(), 0D);
    }

    @Override
    public void delete(@NotNull Dungeon dungeon) {
        Set<Pair<ChestBlock, Hologram>> set = this.holoMap.remove(dungeon.getId());
        if (set == null) return;

        set.forEach(pair -> pair.getSecond().delete());
    }

    @Override
    public void update(@NotNull ChestBlock chestBlock) {
        try {
            Dungeon dungeon = chestBlock.getDungeon();
            Set<Pair<ChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());
            HologramSettings hologramSettings = dungeon.getHologramSettings();
            holograms.stream()
                    .filter(f -> f.getFirst().equals(chestBlock))
                    .map(Pair::getSecond)
                    .forEach(hologram -> {
                        List<String> messages = new ArrayList<>(hologramSettings.getMessages(chestBlock.getState()));
                        messages.replaceAll(chestBlock.replacePlaceholders());
                        updateHologramLines(chestBlock, hologram, messages);
                    });
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Failed to update hologram for " + chestBlock.getDungeon().getId() + " dungeon", exception);
        }
    }

    private void updateHologramLines(@NotNull ChestBlock chestBlock, @NotNull Hologram hologram, @NotNull List<String> message) {
        List<HologramLine> lines = hologram.getPage(0).getLines();
        int lineCount = lines.size();
        Dungeon dungeon = chestBlock.getDungeon();
        MainSettings mainSettings = dungeon.getSettings();
        UnaryOperator<String> settingsPlaceholder = mainSettings.replacePlaceholders();
        UnaryOperator<String> chestBlockPlaceholder = chestBlock.replacePlaceholders();
        UnaryOperator<String> dungeonPlaceholder = dungeon.replacePlaceholders();

        for (int i = 0; i < Math.min(message.size(), lineCount); i++) {
            HologramLine line = lines.get(i);
            String originalText = line.getText();
            String newText = message.get(i);
            newText = settingsPlaceholder.apply(newText);
            newText = chestBlockPlaceholder.apply(newText);
            newText = dungeonPlaceholder.apply(newText);

            if (originalText == null || originalText.isEmpty() || !originalText.equals(newText)) {
                line.setText(Colorizer.apply(newText));
            }
        }
        if (message.size() < lineCount) {
            for (int i = lineCount - 1; i >= message.size(); i--) {
                DHAPI.removeHologramLine(hologram, 0, i);
            }
        } else if (message.size() > lineCount) {
            for (int i = lineCount; i < message.size(); i++) {
                String newText = message.get(i);
                newText = settingsPlaceholder.apply(newText);
                newText = dungeonPlaceholder.apply(newText);
                DHAPI.addHologramLine(hologram, 0, Colorizer.apply(newText));
            }
        }
        hologram.updateAll();
        hologram.updateAnimationsAll();
    }
}