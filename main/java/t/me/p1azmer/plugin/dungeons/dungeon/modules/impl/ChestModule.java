package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.api.schematic.SchematicHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestBlock;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestMenu;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.MainSettings;
import t.me.p1azmer.plugin.dungeons.utils.DungeonCuboid;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class ChestModule extends AbstractModule {
    private Map<Block, DungeonChestBlock> chestMap;
    private Map<String, Integer> chestBlocksMap;
    private List<Block> blocks;

    public ChestModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, false, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.chestMap = new LinkedHashMap<>();
        this.chestBlocksMap = new LinkedHashMap<>();
        this.blocks = new LinkedList<>();

        return aBoolean -> {
            SchematicModule schematicModule = dungeon().getModuleManager().getModule(SchematicModule.class).orElse(null);
            if (schematicModule == null) {
                this.plugin().error("The '" + this.getId() + "' module cannot be loaded because the '" + ModuleId.SCHEMATIC + "' module is disabled or not loaded!");
                return false;
            }

            SchematicHandler handler = plugin().getSchematicHandler();
            if (handler == null) {
                this.plugin().error("The '" + this.getId() + "' module cannot be loaded because the Schematic Handler not installed!");
                return false;
            }

            for (File schematicFile : schematicModule.getSchematicFiles()) {
                String schematicName = schematicFile.getName().replace(Config.DIR_SCHEMATICS, "");

                if (!handler.containsChestBlock(dungeon(), schematicFile)) {
                    plugin().error("Schematic '" + schematicName + "' not contains Chest Material (" + dungeon().getSettings().getChestMaterial().name() + "). Removed from list");
                    continue;
                }
                int chestBlocks = handler.getAmountOfChestBlocks(dungeon(), schematicFile);
                if (chestBlocks == 0) {
                    plugin().warn("Attention! Schematics '" + schematicName + "' does not contain the blocks you specified as a chest!");
                    continue;
                }
                this.chestBlocksMap.put(schematicName, chestBlocks);
            }
            return true;
        };
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        Location location = dungeon().getLocation();
        if (location == null) {
            this.plugin().error("location null");
            return false;
        }

        Material material = dungeon().getSettings().getChestMaterial();
        if (material.isAir()) {
            throw new IllegalArgumentException("In the dungeon '" + dungeon().getId() + "' chest-block is set as air, change the settings!");
        }

        DungeonCuboid cuboid = dungeon().getDungeonCuboid();
        if (cuboid == null) {
            plugin().error("Dungeon cuboid is null!");
            return false;
        }
        long ms = System.currentTimeMillis();
        this.blocks.addAll(cuboid.getBlocks().stream().filter(f -> f.getType().equals(material)).toList());
        plugin().error("block search took" + (System.currentTimeMillis() - ms) + "ms");

        if (this.blocks.isEmpty()) return false;

        List<DungeonReward> rewards = new ArrayList<>();
        Collection<DungeonReward> rewardList = dungeon().getRewards();
        if (dungeon().getSettings().isSeparateChestBlockGui()) {
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
        RegionHandler regionHandler = plugin().getRegionHandler();
        if (regionHandler != null) {
            regionHandler.create(dungeon());
        }
        return true;
    }

    @Override
    public boolean onDeactivate() {
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
        MainSettings settings = dungeon().getSettings();
        for (DungeonChestBlock chest : new HashSet<>(this.getChests())) {
            if (chest.getState().isDeleted()) {
                chest.clear();
                this.getChestMap().remove(chest.getBlock(), chest);
                continue;
            }
            int time = chest.getCurrentTick();

            if (chest.getState().isCooldown() && !settings.getChestOpenType().isClick() && time == settings.getChestWaitTime()) {
                chest.setChestState(DungeonChestState.OPENED);
                time = -1;
            } else if (time == settings.getChestOpenTime()) {
                chest.setChestState(DungeonChestState.OPENED);
                time = -1;
            } else if (chest.getState().isOpen() && time == settings.getChestCloseTime()) {
                chest.setChestState(DungeonChestState.CLOSED);
                chest.clear();
                this.getChestMap().remove(chest.getBlock(), chest);
            }
            chest.updateHologram(++time, this.plugin());
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
}
