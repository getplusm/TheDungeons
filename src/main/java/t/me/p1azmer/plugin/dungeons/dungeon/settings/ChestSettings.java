package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.settings.AbstractSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;

import java.util.HashMap;
import java.util.Map;

public class ChestSettings extends AbstractSettings implements IPlaceholderMap {
    private final Map<DungeonChestState, Integer> stateMap;
    private int blockLimit;
    private ChestModule.OpenType openType;
    private Material material;
    private boolean bigMenu, separateMenu, randomSlots, useOneKeyForMenu;
    private final PlaceholderMap placeholderMap;

    public ChestSettings(@NotNull Dungeon dungeon, @NotNull Map<DungeonChestState, Integer> stateMap,
                         int blockLimit,
                         boolean bigMenu, boolean separateMenu, boolean randomSlots, boolean useOneKeyForMenu,
                         @NotNull ChestModule.OpenType openType, @NotNull Material material) {
        super(dungeon);
        this.blockLimit = blockLimit;

        this.stateMap = stateMap;

        this.bigMenu = bigMenu;
        this.separateMenu = separateMenu;
        this.randomSlots = randomSlots;
        this.useOneKeyForMenu = useOneKeyForMenu;

        this.openType = openType;
        this.material = material;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_BLOCK_LIMIT, () -> String.valueOf(this.getBlockLimit()))
                .add(Placeholders.DUNGEON_SETTINGS_OPEN_TYPE, () -> this.getOpenType().name())
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL, () -> this.getMaterial().name())
                .add(Placeholders.DUNGEON_SETTINGS_BIG_CHEST, () -> LangManager.getBoolean(this.isBigMenu()))
                .add(Placeholders.DUNGEON_SETTINGS_RANDOM_SLOTS, () -> LangManager.getBoolean(this.isRandomSlots()))
                .add(Placeholders.DUNGEON_SETTINGS_SEPARATE_CHEST_BLOCK, () -> LangManager.getBoolean(this.isSeparateMenu()))
                .add(Placeholders.DUNGEON_SETTINGS_USE_ONE_KEY_FOR_CHEST, () -> LangManager.getBoolean(this.isUseOneKeyForMenu()))
        ;
        stateMap.forEach((state, time) -> this.placeholderMap.add(Placeholders.DUNGEON_CHEST_STATE_TIME.apply(state), () -> String.valueOf(time)));
    }

    public static ChestSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        Map<DungeonChestState, Integer> map = new HashMap<>();
        if (!cfg.contains(path + ".Map")) {
            map.putAll(Map.of(
                    DungeonChestState.WAITING, 10,
                    DungeonChestState.COOLDOWN, 5,
                    DungeonChestState.OPENED, 10,
                    DungeonChestState.CLOSED, 10,
                    DungeonChestState.DELETED, 1
            ));
        }
        for (String sId : cfg.getSection(path + ".Map")) {
            DungeonChestState stage = StringUtil.getEnum(sId, DungeonChestState.class).orElse(null);
            if (stage == null) continue;
            int time = cfg.getInt(path + ".Map." + sId);
            map.put(stage, time);
        }
        int blockLimit = cfg.getInt(path + ".Block.Limit");

        boolean useOneKey = cfg.getBoolean(path + ".Menu.Use_One_Key");
        boolean bigMenu = cfg.getBoolean(path + ".Menu.Big");
        boolean separateMenu = cfg.getBoolean(path + ".Menu.Separate");
        boolean randomSlots = cfg.getBoolean(path + ".Menu.Random_Slots");

        ChestModule.OpenType openType = cfg.getEnum(path + ".Open_Type", ChestModule.OpenType.class, ChestModule.OpenType.CLICK);
        Material material = cfg.getEnum(path + ".Block.Material", Material.class, Material.CHEST);
        return new ChestSettings(dungeon, map, blockLimit, bigMenu, separateMenu, randomSlots, useOneKey, openType, material);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        for (Map.Entry<DungeonChestState, Integer> entry : this.getStateMap().entrySet()) {
            cfg.set(path + ".Map." + entry.getKey().name(), entry.getValue());
        }
        cfg.set(path + ".Block.Limit", this.getBlockLimit());
        cfg.set(path + ".Block.Material", this.getMaterial().name());

        cfg.set(path + ".Menu.Use_One_Key", this.isUseOneKeyForMenu());
        cfg.set(path + ".Menu.Big", this.isBigMenu());
        cfg.set(path + ".Menu.Separate", this.isSeparateMenu());
        cfg.set(path + ".Menu.Random_Slots", this.isRandomSlots());

        cfg.set(path + ".Open_Type", this.getOpenType().name());
    }

    @NotNull
    public Map<DungeonChestState, Integer> getStateMap() {
        return stateMap;
    }

    public int getTime(@NotNull DungeonChestState state) {
        return this.getStateMap().getOrDefault(state, -1);
    }

    public int getBlockLimit() {
        return blockLimit;
    }

    public boolean isBigMenu() {
        return bigMenu;
    }

    public boolean isRandomSlots() {
        return randomSlots;
    }

    public boolean isSeparateMenu() {
        return separateMenu;
    }

    public boolean isUseOneKeyForMenu() {
        return useOneKeyForMenu;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public ChestModule.OpenType getOpenType() {
        return openType;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setStateTime(@NotNull DungeonChestState state, int time) {
        this.getStateMap().put(state, time);
    }

    public void setBlockLimit(int blockLimit) {
        this.blockLimit = blockLimit;
    }

    public void setOpenType(@NotNull ChestModule.OpenType openType) {
        this.openType = openType;
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public void setUseOneKeyForMenu(boolean useOneKeyForMenu) {
        this.useOneKeyForMenu = useOneKeyForMenu;
    }

    public void setBigMenu(boolean bigMenu) {
        this.bigMenu = bigMenu;
    }

    public void setSeparateMenu(boolean separateMenu) {
        this.separateMenu = separateMenu;
    }

    public void setRandomSlots(boolean randomSlots) {
        this.randomSlots = randomSlots;
    }
}
