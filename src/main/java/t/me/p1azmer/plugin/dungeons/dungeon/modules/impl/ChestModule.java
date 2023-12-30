package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestMenu;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;
import t.me.p1azmer.plugin.dungeons.utils.DungeonCuboid;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChestModule extends AbstractModule {
    private Map<Block, DungeonChestBlock> chestMap;
    private Map<String, Integer> chestBlocksMap;
    private List<Block> blocks;

    public ChestModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.chestMap = new LinkedHashMap<>();
        this.chestBlocksMap = new LinkedHashMap<>();
        this.blocks = new LinkedList<>();

        return aBoolean -> {
            SchematicModule schematicModule = dungeon().getModuleManager().getModule(SchematicModule.class).orElse(null);
            if (schematicModule == null) {
                this.error("The '" + this.getId() + "' module cannot be loaded because the '" + ModuleId.SCHEMATIC + "' module is disabled or not loaded!");
                return false;
            }

            SchematicHandler handler = plugin().getSchematicHandler();
            if (handler == null) {
                this.error("The '" + this.getId() + "' module cannot be loaded because the Schematic Handler not installed!");
                return false;
            }

            for (File schematicFile : schematicModule.getSchematicFiles()) {
                String schematicName = schematicFile.getName().replace(Config.DIR_SCHEMATICS, "");

                if (!handler.containsChestBlock(dungeon(), schematicFile)) {
                    plugin().error("Schematic '" + schematicName + "' not contains Chest Material (" + dungeon().getChestSettings().getMaterial().name() + "). Removed from list");
                    continue;
                }
                int chestBlocks = handler.getAmountOfChestBlocks(dungeon(), schematicFile);
                if (chestBlocks == 0) {
                    plugin().warn("Attention! Schematics '" + schematicName + "' does not contain the blocks you specified as a chest!");
                    continue;
                }
                this.chestBlocksMap.put(schematicName, chestBlocks);
            }
            return this.dungeon().getStage().isOpening() || this.dungeon().getStage().isOpened() || this.dungeon().getStage().isWaitingPlayers();
        };
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        Location location = dungeon().getLocation();
        if (location == null) {
            return false;
        }

        Material material = dungeon().getChestSettings().getMaterial();
        if (material.isAir()) {
            this.error("In the dungeon '" + dungeon().getId() + "' chest-block is set as air, change the settings!");
            return false;
        }

        DungeonCuboid cuboid = dungeon().getDungeonCuboid();
        if (cuboid == null) {
            plugin().error("Dungeon cuboid is null!");
            return false;
        }

        this.blocks.addAll(cuboid.getBlocks().stream().filter(f -> f.getType().equals(material)).peek(block -> block.setMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey(), new FixedMetadataValue(plugin(), this.dungeon().getId()))).toList());

        if (this.blocks.isEmpty()) {
            plugin().error("Chest Blocks is empty on generated schematic!");
            return false;
        }

        List<DungeonReward> rewards = new ArrayList<>();
        Collection<DungeonReward> rewardList = this.dungeon().getRewards();
        if (!rewardList.isEmpty()) {
            if (this.dungeon().getChestSettings().isSeparateMenu()) {
                for (Block block : blocks) {
                    for (DungeonReward dungeonReward : rewardList) {
                        if (Rnd.chance(dungeonReward.getChance())) {
                            rewards.add(dungeonReward);
                        }
                    }
                    if (rewards.isEmpty()) {
                        rewards.add(Rnd.get(rewardList.stream().toList()));
                    }
                    this.setupMenu(block, rewards);
                }
            } else {
                for (DungeonReward dungeonReward : rewardList) {
                    if (Rnd.chance(dungeonReward.getChance())) {
                        rewards.add(dungeonReward);
                    }
                }
                if (rewards.isEmpty()) {
                    rewards.add(Rnd.get(rewardList.stream().toList()));
                }
                blocks.forEach(block -> this.setupMenu(block, rewards));
            }
        }
        RegionHandler regionHandler = plugin().getRegionHandler();
        if (regionHandler != null) {
            regionHandler.create(dungeon());
        }
        return true;
    }

    @Override
    public boolean onDeactivate() {
        if (this.getChests().stream().anyMatch(chest -> chest.getState().isOpen() || chest.getState().isCooldown()))
            return false;

        this.getChests()
                .stream()
                .filter(Objects::nonNull)
                .forEach(DungeonChestBlock::clear);
        this.getChestMap().clear();
        if (!this.getBlocks().isEmpty()) {
            this.getBlocks()
                    .stream().filter(block -> block.hasMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey()))
                    .forEach(block -> block.removeMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey(), plugin()));
            this.getBlocks().clear();
        }

        return true;
    }

    @Override
    public void update() {
        for (DungeonChestBlock chest : new HashSet<>(this.getChests())) {
            if (chest.getState().isDeleted()) {
                chest.clear();
                this.getChestMap().remove(chest.getBlock(), chest);
                continue;
            }
            int time = chest.getCurrentTick();
            DungeonChestState state = chest.getState();

            if (state.isWaiting() && chest.getNextStateTime() == 0 || state.isCooldown() && chest.getNextStateTime() == 0) {
                chest.setChestState(DungeonChestState.CLOSED);
                time = -1;
            } else if (state.isClosed() && chest.getNextStateTime() == 0) {
                chest.setChestState(DungeonChestState.OPENED);
                time = -1;
            } else if (state.isOpen() && chest.getNextStateTime() == 0) {
                chest.setChestState(DungeonChestState.DELETED);
                time = -1;
                this.getChestMap().remove(chest.getBlock(), chest);
                chest.clear();
            }
            chest.tick(this.plugin(), ++time);
        }
        super.update();
    }

    @NotNull
    public Map<String, Integer> getChestBlocksMap() {
        return chestBlocksMap;
    }

    @NotNull
    public Map<Block, DungeonChestBlock> getChestMap() {
        return this.chestMap;
    }

    @NotNull
    public Collection<DungeonChestBlock> getChests() {
        return this.getChestMap().values();
    }

    @NotNull
    public Collection<DungeonChestBlock> getActiveChests() {
        return this.getChests().stream().filter(f -> f.getState().isOpen() || f.getState().isClosed()).collect(Collectors.toList());
    }

    @NotNull
    public List<Block> getBlocks() {
        return blocks;
    }

    @Nullable
    public DungeonChestBlock getChestByBlock(@NotNull Block block) {
        return this.getChestMap().get(block);
    }

    @Nullable
    public Block getBlock(@NotNull Location location) {
        return this.blocks.stream().filter(f -> f.getLocation().equals(location)).findFirst().orElse(null);
    }

    public void setupMenu(@NotNull Block block, @NotNull List<DungeonReward> rewards) {
        DungeonChestMenu menu = new DungeonChestMenu(block, this.dungeon(), rewards);
        DungeonChestBlock dungeonChestBlock = new DungeonChestBlock(this.dungeon(), block, block.getLocation(), menu);
        this.getChestMap().put(block, dungeonChestBlock);
    }

    public enum OpenType {
        CLICK,
        TIMER;

        public boolean isClick() {
            return this.equals(CLICK);
        }
    }
}
