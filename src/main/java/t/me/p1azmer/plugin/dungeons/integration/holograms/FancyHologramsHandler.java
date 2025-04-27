package t.me.p1azmer.plugin.dungeons.integration.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
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
public class FancyHologramsHandler implements HologramHandler {

    Map<String, Set<Pair<ChestBlock, Hologram>>> holoMap = new HashMap<>();
    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public void setup() {
    }

    @Override
    public void shutdown() {
        this.holoMap.values().forEach(set -> set.forEach(pair -> {
            Hologram hologram = pair.getSecond();
            hologram.deleteHologram();
        }));
        this.holoMap.clear();
    }

    @Override
    public void create(@NotNull Dungeon dungeon, @NotNull ChestModule module) {
        Set<Pair<ChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());

        try {
            for (ChestBlock chestBlock : module.getChests()) {
                Block block = chestBlock.getBlock();
                HologramSettings hologramSettings = chestBlock.getDungeon().getHologramSettings();
                List<String> messages = new ArrayList<>(hologramSettings.getMessages(chestBlock.getState()));
                messages.replaceAll(chestBlock.replacePlaceholders());

                Location location = fineLocation(chestBlock, block.getLocation());

                TextHologramData hologramData = new TextHologramData(UUID.randomUUID().toString(), location);
                hologramData.setText(messages);
                hologramData.setPersistent(true);

                Hologram hologram = manager.create(hologramData);
                hologram.createHologram();
                Bukkit.getOnlinePlayers().forEach(hologram::updateShownStateFor);

                manager.addHologram(hologram);
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

        set.forEach(pair -> pair.getSecond().deleteHologram());
    }

    @Override
    public void update(@NotNull ChestBlock chestBlock) {
        try {
            Dungeon dungeon = chestBlock.getDungeon();
            Set<Pair<ChestBlock, Hologram>> holograms = this.holoMap.computeIfAbsent(dungeon.getId(), set -> new HashSet<>());
            HologramSettings hologramSettings = dungeon.getHologramSettings();
            holograms.stream().filter(f -> f.getFirst().equals(chestBlock)).map(Pair::getSecond).forEach(hologram -> {
                List<String> messages = new ArrayList<>(hologramSettings.getMessages(chestBlock.getState()));
                messages.replaceAll(chestBlock.replacePlaceholders());
                updateHologramLines(chestBlock, hologram, messages);
            });
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Failed to update hologram for " + chestBlock.getDungeon().getId() + " dungeon", exception);
        }
    }

    private void updateHologramLines(@NotNull ChestBlock chestBlock, @NotNull Hologram hologram, @NotNull List<String> message) {
        if (!(hologram.getData() instanceof TextHologramData textData)) {
            return;
        }
        List<String> lines = textData.getText();
        int lineCount = lines.size();
        Dungeon dungeon = chestBlock.getDungeon();
        MainSettings mainSettings = dungeon.getSettings();
        UnaryOperator<String> settingsPlaceholder = mainSettings.replacePlaceholders();
        UnaryOperator<String> chestBlockPlaceholder = chestBlock.replacePlaceholders();
        UnaryOperator<String> dungeonPlaceholder = dungeon.replacePlaceholders();

        for (int i = 0; i < Math.min(message.size(), lineCount); i++) {
            String originalText = lines.get(i);
            String newText = message.get(i);
            newText = settingsPlaceholder.apply(newText);
            newText = chestBlockPlaceholder.apply(newText);
            newText = dungeonPlaceholder.apply(newText);

            if (originalText == null || originalText.isEmpty() || !originalText.equals(newText)) {
                lines.set(i, Colorizer.apply(newText));
            }
        }
        if (message.size() < lineCount) {
            for (int i = lineCount - 1; i >= message.size(); i--) {
                textData.removeLine(i);
            }
        } else if (message.size() > lineCount) {
            for (int i = lineCount; i < message.size(); i++) {
                String newText = message.get(i);
                newText = settingsPlaceholder.apply(newText);
                newText = dungeonPlaceholder.apply(newText);
                textData.addLine(Colorizer.apply(newText));
            }
        }
        hologram.queueUpdate();
    }
}