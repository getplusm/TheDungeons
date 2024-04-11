package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.ChestMenu;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.state.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.ChestSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.utils.Cuboid;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class ChestModule extends AbstractModule {
    private Map<Block, ChestBlock> chestMap;
    private List<Block> blocks;

    public ChestModule(
            @NotNull Dungeon dungeon,
            @NotNull String id
    ) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.chestMap = new LinkedHashMap<>();
        this.blocks = new LinkedList<>();

        return aBoolean -> {
            DungeonStage dungeonStage = this.getDungeon().getStage();
            SpawnModule spawnModule = this.getManager().getModule(SpawnModule.class).orElse(null);
            boolean spawnModuleAllowed = spawnModule != null && spawnModule.isSpawned();
            return dungeonStage.isOpening() && spawnModuleAllowed;
        };
    }

    @Override
    protected void onShutdown() {}

    @Override
    public CompletableFuture<Boolean> onActivate(boolean force) {
        Location location = getDungeon().getLocation().orElse(null);
        if (location == null) {
            this.warn("Dungeon location is null");
            return CompletableFuture.completedFuture(false);
        }

        ChestSettings chestSettings = this.getDungeon().getChestSettings();
        Material material = chestSettings.getMaterial();
        if (material.isAir()) {
            this.warn("Chest material is air");
            return CompletableFuture.completedFuture(false);
        }

        Cuboid cuboid = getDungeon().getDungeonCuboid().orElse(null);
        if (cuboid == null) {
            this.warn("Dungeon cuboid is null");
            return CompletableFuture.completedFuture(false);
        }

        FixedMetadataValue metadataValue = new FixedMetadataValue(plugin(), this.getDungeon().getId());
        this.blocks.addAll(
                cuboid.getBlocks()
                        .stream()
                        .filter(f -> f.getType().equals(material))
                        .peek(block -> block.setMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey(), metadataValue))
                        .toList()
        );

        if (this.blocks.isEmpty()) {
            this.warn("No chest blocks found");
            return CompletableFuture.completedFuture(false);
        }

        Map<Reward, Double> rewards = this.getDungeon()
                .getRewards()
                .stream()
                .collect(Collectors.toMap(reward -> reward, Reward::getChance));


        for (Block block : this.blocks) this.setupMenu(block, rewards);

        RegionHandler regionHandler = plugin().getRegionHandler();
        if (regionHandler != null) regionHandler.create(getDungeon());

        return CompletableFuture.completedFuture(true);
    }

    @Override
    public boolean onDeactivate(boolean force) {
        if (!force && this.getChests().stream().anyMatch(chest -> chest.getState().isOpen() || chest.getState().isCooldown()))
            return false;

        this.getChests()
                .stream()
                .filter(Objects::nonNull)
                .forEach(ChestBlock::clear);

        this.getBlocks()
                .stream().filter(block -> block.hasMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey()))
                .forEach(block -> block.removeMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey(), plugin()));

        this.getBlocks().clear();
        this.getChestMap().clear();
        return true;
    }

    @Override
    public void update() {
        super.update();
        for (ChestBlock chest : new HashSet<>(this.getChests())) {
            if (chest.getState().isDeleted()) {
                chest.clear();
                this.getChestMap().remove(chest.getBlock(), chest);
                continue;
            }
            int time = chest.getCurrentTick();
            ChestState state = chest.getState();

            if (state.isWaiting() && chest.getNextStateTime() == 0 || state.isCooldown() && chest.getNextStateTime() == 0) {
                chest.setState(ChestState.CLOSED);
                time = -1;
            } else if (state.isClosed() && chest.getNextStateTime() == 0) {
                chest.setState(ChestState.OPENED);
                time = -1;
            } else if (state.isOpen() && chest.getNextStateTime() == 0) {
                chest.setState(ChestState.DELETED);
                time = -1;
                this.getChestMap().remove(chest.getBlock(), chest);
                chest.clear();
            }
            chest.tick(this.plugin(), ++time);
        }
    }

    @NotNull
    public Map<Block, ChestBlock> getChestMap() {
        return this.chestMap;
    }

    @NotNull
    public Collection<ChestBlock> getChests() {
        return this.getChestMap().values();
    }

    @NotNull
    public Collection<ChestBlock> getActiveChests() {
        return this.getChests()
                .stream()
                .filter(f -> f.getState().isOpen() || f.getState().isClosed())
                .collect(Collectors.toList());
    }

    public Optional<ChestBlock> getChestByBlock(@NotNull Block block) {
        return Optional.ofNullable(this.getChestMap().get(block));
    }

    public Optional<Block> getBlock(@NotNull Location location) {
        return this.blocks
                .stream()
                .filter(f -> f.getLocation().equals(location))
                .findFirst();
    }

    public void setupMenu(@NotNull Block block, @NotNull Map<Reward, Double> rewards) {
        ChestMenu menu = new ChestMenu(block, this.getDungeon(), rewards);
        ChestBlock chestBlock = new ChestBlock(this.getDungeon(), block, block.getLocation(), menu);
        this.getChestMap().put(block, chestBlock);
        this.debug("GUI for '" + block.getType() + "' installed");
    }

    // TODO: Change to a separate class to handle more actions
    public enum OpenType {
        CLICK,
        TIMER;

        public boolean isClick() {
            return this.equals(CLICK);
        }
    }
}
