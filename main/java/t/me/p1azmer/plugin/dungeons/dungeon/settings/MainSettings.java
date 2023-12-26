package t.me.p1azmer.plugin.dungeons.dungeon.settings;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class MainSettings implements IPlaceholderMap {
    private final Dungeon dungeon;
    private boolean enabled;
    private boolean clickTimer;
    private boolean bigChest;
    private boolean separateChestBlockGui;
    private boolean randomSlots;
    private boolean underground;
    private boolean useOneKeyForChest;
    private boolean letPlayersWhenClose;

    private int minimalOnline;
    private int chestBlockLimit;
    private int chestBlockSearchRadius;

    private long chestWaitTime;
    private long chestOpenTime;
    private long chestCloseTime;
    private long refreshTime;
    private long regionCloseTime;
    private long regionWaitTime;
    private long regionOpenTime;

    private Dungeon.OpenType chestOpenType;
    private Material chestMaterial;
    private List<String> openCommands;
    private List<String> closeCommands;
    private Map<String, Integer> mobMap;

    private final PlaceholderMap placeholderMap;

    public MainSettings(@NotNull Dungeon dungeon,
                        boolean enabled, boolean clickTimer, boolean bigChest,
                        int minimalOnline, int chestBlockLimit,
                        boolean separateChestBlockGui, boolean randomSlots, boolean underground,
                        int chestBlockSearchRadius, long chestWaitTime, long chestOpenTime, long chestCloseTime, long refreshTime,
                        @NotNull Dungeon.OpenType chestOpenType, @NotNull Material chestMaterial,
                        boolean useOneKeyForChest, boolean letPlayersWhenClose, long regionCloseTime, long regionWaitTime, long regionOpenTime,
                        @NotNull List<String> openCommands, @NotNull List<String> closeCommands,
                        @NotNull Map<String, Integer> mobMap) {
        this.dungeon = dungeon;
        this.enabled = enabled;
        this.clickTimer = clickTimer;
        this.bigChest = bigChest;
        this.minimalOnline = minimalOnline;
        this.chestBlockLimit = chestBlockLimit;
        this.separateChestBlockGui = separateChestBlockGui;
        this.randomSlots = randomSlots;
        this.underground = underground;
        this.chestBlockSearchRadius = chestBlockSearchRadius;
        this.chestWaitTime = chestWaitTime;
        this.chestOpenTime = chestOpenTime;
        this.chestCloseTime = chestCloseTime;
        this.refreshTime = refreshTime;
        this.chestOpenType = chestOpenType;
        this.chestMaterial = chestMaterial;
        this.letPlayersWhenClose = letPlayersWhenClose;
        this.useOneKeyForChest = useOneKeyForChest;
        this.regionCloseTime = regionCloseTime;
        this.regionWaitTime = regionWaitTime;
        this.regionOpenTime = regionOpenTime;
        this.openCommands = openCommands;
        this.closeCommands = closeCommands;
        this.mobMap = mobMap;


        this.placeholderMap = (new PlaceholderMap()
                .add(Placeholders.DUNGEON_SETTINGS_BIG_CHEST, () -> LangManager.getBoolean(this.isBigChest()))
                .add(Placeholders.DUNGEON_SETTINGS_BLOCKS_SIZE, () -> NumberUtil.format(this.getChestBlockSearchRadius()))
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_CLOSE_TIME, () -> String.valueOf(this.getChestCloseTime()))
                .add(Placeholders.DUNGEON_SETTINGS_ENABLED, () -> LangManager.getBoolean(this.isEnabled()))
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_OPEN_TIME, () -> String.valueOf(this.getChestOpenTime()))
                .add(Placeholders.DUNGEON_SETTINGS_REFRESH, () -> String.valueOf(this.getRefreshTime()))
                .add(Placeholders.DUNGEON_SETTINGS_CLICK_TIMER, () -> LangManager.getBoolean(this.isClickTimer()))
                .add(Placeholders.DUNGEON_SETTINGS_UNDERGROUND, () -> LangManager.getBoolean(this.isUnderground()))
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_WAIT_TIME, () -> String.valueOf(this.getChestWaitTime()))
                .add(Placeholders.DUNGEON_SETTINGS_RANDOM_SLOTS, () -> LangManager.getBoolean(this.isRandomSlots()))
                .add(Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE, () -> String.valueOf(this.getMinimalOnline()))
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_BLOCK_LIMIT, () -> String.valueOf(this.getChestBlockLimit()))
                .add(Placeholders.DUNGEON_SETTINGS_SEPARATE_CHEST_BLOCK, () -> LangManager.getBoolean(this.isSeparateChestBlockGui()))
                .add(Placeholders.DUNGEON_SETTINGS_OPEN_TYPE, () -> this.getChestOpenType().name())
                .add(Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL, () -> this.getChestMaterial().name())
                .add(Placeholders.DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE, () -> LangManager.getBoolean(this.isLetPlayersWhenClose()))
                .add(Placeholders.DUNGEON_SETTINGS_USE_ONE_KEY_FOR_CHEST, () -> LangManager.getBoolean(this.isUseOneKeyForChest()))
                .add(Placeholders.DUNGEON_SETTINGS_REGION_CLOSE_TIME, () -> String.valueOf(this.getRegionCloseTime()))
                .add(Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME, () -> String.valueOf(this.getRegionWaitTime()))
                .add(Placeholders.DUNGEON_SETTINGS_REGION_OPEN_TIME, () -> String.valueOf(this.getRegionOpenTime()))
                .add(Placeholders.DUNGEON_SETTINGS_CLOSE_COMMANDS, () -> String.join("\n", this.getCloseCommands()))
                .add(Placeholders.DUNGEON_SETTINGS_OPEN_COMMANDS, () -> String.join("\n", this.getOpenCommands()))
                .add(Placeholders.DUNGEON_SETTINGS_MOBS, () -> this.getMobMap().entrySet().stream()
                        .map(enrty -> Colorizer.apply(Colors.LIGHT_YELLOW + enrty.getKey() + ": " + enrty.getValue())).collect(Collectors.joining("\n")))
        );
    }

    @NotNull
    public static MainSettings read(@NotNull Dungeon dungeon, @NotNull JYML cfg, @NotNull String path) {
        boolean enabled = cfg.getBoolean(path + ".Enabled", true); //
        Dungeon.OpenType openType = StringUtil.getEnum(cfg.getString(path + "OpenType", null), Dungeon.OpenType.class).orElse(Dungeon.OpenType.CLICK);//
        int chestWaitTime = cfg.getInt(path + "Chest.Wait", cfg.getInt(path + "Timer.Wait", 60));//
        int chestCloseTime = cfg.getInt(path + "Chest.Close", cfg.getInt(path + "Timer.Close", 15));//
        int chestOpenTime = cfg.getInt(path + "Chest.Open", cfg.getInt(path + "Timer.Open", 15));//
        int refreshTime = cfg.getInt(path + "Settings.Time.Refresh", cfg.getInt(path + "Timer.Refresh", 120));//
        if (refreshTime < 5) {
            refreshTime = 5;
            dungeon.plugin().error("The '" + dungeon.getId() + "' dungeon setup has a short reboot interval. Changed to 5 to reduce system load");
        }
        int closeTime = cfg.getInt(path + "Settings.Time.Close", 20);
        int waitTime = cfg.getInt(path + "Settings.Time.Wait", 20);
        int openTime = cfg.getInt(path + "Settings.Time.Open", 20);

        int minimalOnline = cfg.getInt(path + "Settings.Minimal_Online");//
        boolean clickTimer = cfg.getBoolean(path + "Settings.Click_Timer"); //
        boolean randomSlots = cfg.getBoolean(path + "Settings.Random.Slots");//
        boolean underground = cfg.getBoolean(path + "Settings.Underground");//
        boolean bigChest = cfg.getBoolean(path + "Settings.Big_Chest");//
        int chestSearchRadius = cfg.getInt(path + "Chest.Search_Radius", 15);//
        if (chestSearchRadius <= 0)
            chestSearchRadius = 15;
        int chestLimit = cfg.getInt(path + "Chest.Limit", 1);
        if (chestLimit <= 0)
            chestLimit = 1;
        boolean chestSeparateGui = cfg.getBoolean(path + "Chest.Separate_Gui", false);
        Material chestMaterial = StringUtil.getEnum(cfg.getString(path + "Chest.Material", null), Material.class).orElse(null);
        if (chestMaterial == null) {
            chestMaterial = Material.CHEST;
            dungeon.plugin().error("Error loading material for chest block. Material not found, check the '" + dungeon.getId() + "' dungeon settings and restart it");
        }
        boolean letPlayersWhenClose = cfg.getBoolean(path + "Settings.Let_Players_When_Close", true);
        boolean useOneKeyForChest = cfg.getBoolean(path + "Settings.Use_One_Key_For_Chest", true);
        List<String> openCommands = cfg.getStringList(path + "Settings.Open.Commands");
        List<String> closeCommands = cfg.getStringList(path + "Settings.Close.Commands");

        // Mobs

        Map<String, Integer> mobs = new HashMap<>();
        for (String mobId : cfg.getSection(path + "Mobs")) {
            int amount = cfg.getInt(path + "Mobs." + mobId + ".Amount");
            mobs.put(mobId, amount);
        }

        return new MainSettings(dungeon, enabled, clickTimer, bigChest, minimalOnline, chestLimit, chestSeparateGui, randomSlots, underground, chestSearchRadius, chestWaitTime, chestOpenTime, chestCloseTime, refreshTime, openType, chestMaterial, useOneKeyForChest, letPlayersWhenClose, closeTime, waitTime, openTime, openCommands, closeCommands, mobs);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + "Enabled", this.isEnabled());
        cfg.set(path + "OpenType", this.getChestOpenType().name());
        cfg.set(path + "Chest.Wait", this.getChestWaitTime());
        cfg.set(path + "Chest.Open", this.getChestOpenTime());
        cfg.set(path + "Chest.Close", this.getChestCloseTime());
        // old data
        cfg.set(path + "Timer.Wait", null);
        cfg.set(path + "Timer.Open", null);
        cfg.set(path + "Timer.Close", null);
        cfg.set(path + "Timer.Refresh", null);
        //
        cfg.set(path + "Settings.Time.Close", this.getRegionCloseTime());
        cfg.set(path + "Settings.Time.Open", this.getRegionOpenTime());
        cfg.set(path + "Settings.Time.Wait", this.getRegionWaitTime());
        cfg.set(path + "Settings.Time.Refresh", this.getRefreshTime());
        cfg.set(path + "Settings.Minimal_Online", this.getMinimalOnline());
        cfg.set(path + "Settings.Click_Timer", this.isClickTimer());
        cfg.set(path + "Settings.Random.Slots", this.isRandomSlots());
        cfg.set(path + "Settings.Underground", this.isUnderground());
        cfg.set(path + "Settings.Big_Chest", this.isBigChest());
        cfg.set(path + "Settings.Let_Players_When_Close", this.isLetPlayersWhenClose());
        cfg.set(path + "Settings.Use_One_Key_For_Chest", this.isUseOneKeyForChest());
        cfg.set(path + "Settings.Open.Commands", this.getOpenCommands());
        cfg.set(path + "Settings.Close.Commands", this.getCloseCommands());

        // chest
        cfg.set(path + "Chest.Amount", null);
        cfg.set(path + "Chest.Search_Radius", this.getChestBlockSearchRadius());
        cfg.set(path + "Chest.Limit", this.getChestBlockLimit());
        cfg.set(path + "Chest.Separate_Gui", this.isSeparateChestBlockGui());
        cfg.set(path + "Chest.Material", this.getChestMaterial());

        // mobs
        cfg.set(path + "Mobs", null);
        this.getMobMap().forEach((mobId, amount) -> cfg.set(path + "Mobs." + mobId + ".Amount", amount));
    }

    @NotNull
    public Dungeon getDungeon() {
        return dungeon;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isClickTimer() {
        return clickTimer;
    }

    public boolean isBigChest() {
        return bigChest;
    }

    public int getMinimalOnline() {
        return minimalOnline;
    }

    public int getChestBlockLimit() {
        return chestBlockLimit;
    }

    public boolean isSeparateChestBlockGui() {
        return separateChestBlockGui;
    }

    public boolean isRandomSlots() {
        return randomSlots;
    }

    public boolean isUnderground() {
        return underground;
    }

    public int getChestBlockSearchRadius() {
        return chestBlockSearchRadius;
    }

    public long getChestWaitTime() {
        return chestWaitTime;
    }

    public long getChestOpenTime() {
        return chestOpenTime;
    }

    public long getChestCloseTime() {
        return chestCloseTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    @NotNull
    public Dungeon.OpenType getChestOpenType() {
        return chestOpenType;
    }

    @NotNull
    public Material getChestMaterial() {
        return chestMaterial;
    }

    public boolean isUseOneKeyForChest() {
        return useOneKeyForChest;
    }

    public boolean isLetPlayersWhenClose() {
        return letPlayersWhenClose;
    }

    public long getRegionCloseTime() {
        return regionCloseTime;
    }

    public long getRegionWaitTime() {
        return regionWaitTime;
    }

    public long getRegionOpenTime() {
        return regionOpenTime;
    }

    @NotNull
    public List<String> getCloseCommands() {
        return closeCommands;
    }

    @NotNull
    public List<String> getOpenCommands() {
        return openCommands;
    }

    @NotNull
    public Map<String, Integer> getMobMap() {
        return mobMap;
    }

//    public int getPeekTime(@NotNull DungeonStage stage){
//        return switch (stage){
//            case OPEN -> this.getRegionOpenTime();
//        }
//    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public @NotNull UnaryOperator<String> replacePlaceholders(int time) {
        return s -> s
                .replace(Placeholders.DUNGEON_SETTINGS_CHEST_WAIT_TIME, String.valueOf(getChestWaitTime() - time))
                .replace(Placeholders.DUNGEON_SETTINGS_CHEST_OPEN_TIME, String.valueOf(getChestOpenTime() - time))
                .replace(Placeholders.DUNGEON_SETTINGS_CHEST_CLOSE_TIME, String.valueOf(getChestCloseTime() - time))
                .replace(Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME, String.valueOf(getRegionWaitTime() - time))
                .replace(Placeholders.DUNGEON_SETTINGS_REGION_OPEN_TIME, String.valueOf(getRegionOpenTime() - time))
                .replace(Placeholders.DUNGEON_SETTINGS_REGION_CLOSE_TIME, String.valueOf(getRegionCloseTime() - time))
                ;
    }

    public void setMobMap(@NotNull Map<String, Integer> mobMap) {
        this.mobMap = mobMap;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setClickTimer(boolean clickTimer) {
        this.clickTimer = clickTimer;
    }

    public void setBigChest(boolean bigChest) {
        this.bigChest = bigChest;
    }

    public void setMinimalOnline(int minimalOnline) {
        this.minimalOnline = minimalOnline;
    }

    public void setChestBlockLimit(int chestBlockLimit) {
        this.chestBlockLimit = chestBlockLimit;
    }

    public void setSeparateChestBlockGui(boolean separateChestBlockGui) {
        this.separateChestBlockGui = separateChestBlockGui;
    }

    public void setRandomSlots(boolean randomSlots) {
        this.randomSlots = randomSlots;
    }

    public void setUnderground(boolean underground) {
        this.underground = underground;
    }

    public void setChestBlockSearchRadius(int chestBlockSearchRadius) {
        this.chestBlockSearchRadius = chestBlockSearchRadius;
    }

    public void setChestWaitTime(long chestWaitTime) {
        this.chestWaitTime = chestWaitTime;
    }

    public void setChestOpenTime(long chestOpenTime) {
        this.chestOpenTime = chestOpenTime;
    }

    public void setChestCloseTime(long chestCloseTime) {
        this.chestCloseTime = chestCloseTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public void setChestOpenType(Dungeon.OpenType chestOpenType) {
        this.chestOpenType = chestOpenType;
    }

    public void setChestMaterial(Material chestMaterial) {
        this.chestMaterial = chestMaterial;
    }

    public void setUseOneKeyForChest(boolean useOneKeyForChest) {
        this.useOneKeyForChest = useOneKeyForChest;
    }

    public void setLetPlayersWhenClose(boolean letPlayersWhenClose) {
        this.letPlayersWhenClose = letPlayersWhenClose;
    }

    public void setRegionCloseTime(long regionCloseTime) {
        this.regionCloseTime = regionCloseTime;
    }

    public void setRegionWaitTime(long regionWaitTime) {
        this.regionWaitTime = regionWaitTime;
    }

    public void setRegionOpenTime(long regionOpenTime) {
        this.regionOpenTime = regionOpenTime;
    }

    public void setCloseCommands(@NotNull List<String> closeCommands) {
        this.closeCommands = closeCommands;
    }

    public void setOpenCommands(@NotNull List<String> openCommands) {
        this.openCommands = openCommands;
    }
}
