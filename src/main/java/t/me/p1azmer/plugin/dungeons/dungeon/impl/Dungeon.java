package t.me.p1azmer.plugin.dungeons.dungeon.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.engine.utils.values.UniInt;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.*;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageSettings;
import t.me.p1azmer.plugin.dungeons.utils.Cuboid;
import t.me.p1azmer.plugin.dungeons.utils.ItemReader;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage.*;

public class Dungeon extends AbstractConfigHolder<DungeonPlugin> implements ICleanable, IPlaceholderMap {

    private String name;
    private DungeonStage stage;

    private Region region;
    private MainSettings settings;
    private HologramSettings hologramSettings;
    private PartySettings partySettings;
    private ModuleSettings moduleSettings;
    private StageSettings stageSettings;
    private ChestSettings chestSettings;
    private EffectSettings effectSettings;
    private AnnounceSettings announceSettings;
    private SchematicSettings schematicSettings;
    private CommandsSettings commandsSettings;

    private RewardSettings rewardSettings;

    private Map<String, Reward> rewardMap;
    private Set<String> keyIds;

    private Cuboid cuboid;
    private final DungeonManager manager;
    private ModuleManager moduleManager;
    private final PlaceholderMap placeholderMap;
    private World world;
    private Location location; // add permanent dungeon loca
    // weak cache
    private AtomicInteger selfTick;
    private DungeonMainEditor editor;

    public Dungeon(@NotNull DungeonManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;
        this.setStage(FREEZE);
        this.setPartySettings(new PartySettings(this, false, 2));
        this.setHologramSettings(new HologramSettings(this, 2, Map.of(
                DungeonChestState.WAITING, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #4dffc3Waiting you", "#FFC458Click me if you have key!"),
                DungeonChestState.COOLDOWN, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #db3251Closed", "&eOpening in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN),
                DungeonChestState.OPENED, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #7fffd4Opened", "#dec1d2Closing in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN))
        ));
        this.setSettings(new MainSettings(this, false, false, false, 1, new HashMap<>()));
        this.setDungeonRegion(new Region(this, true, this.getId(), 15, List.of("pistons deny", "pvp allow", "use allow", "chest-access allow")));
        this.setAnnounceSettings(new AnnounceSettings(this, Map.of(
                PREPARE, Map.of(Objects.requireNonNull(plugin().getAnnounceManager().getAnnounce("prepare_default")), new int[]{26, 27, 28, 29, 30}),
                DungeonStage.CLOSED, Map.of(Objects.requireNonNull(plugin().getAnnounceManager().getAnnounce("closed_default")), new int[]{0}))));
        this.setSchematicSettings(new SchematicSettings(this, List.of("dungeon_rotten_mushroom"), true, false));
        this.setModuleSettings(new ModuleSettings(this, Map.of(ModuleId.SPAWN, true, ModuleId.ANNOUNCE, true, ModuleId.CHEST, true, ModuleId.COMMAND, true, ModuleId.HOLOGRAM, true, ModuleId.SCHEMATIC, true)));
        this.setStageSettings(new StageSettings(this, Map.of(FREEZE, 5, CHECK, 3, PREPARE, 30, WAITING_PLAYERS, 10, OPENING, 5, OPENED, 60, CLOSED, 5, DELETING, 1, CANCELLED, 1, REBOOTED, 1)));
        this.setChestSettings(new ChestSettings(this, Map.of(DungeonChestState.WAITING, 10, DungeonChestState.COOLDOWN, 5, DungeonChestState.OPENED, 10, DungeonChestState.CLOSED, 10, DungeonChestState.DELETED, 1), 3, false, false, false, false, ChestModule.OpenType.CLICK, Material.BARREL));
        this.setEffectSettings(new EffectSettings(this, false, new ArrayList<>()));
        this.setCommandsSettings(new CommandsSettings(this, Map.of(FREEZE, new ArrayList<>(), CHECK, new ArrayList<>(), PREPARE, new ArrayList<>(), WAITING_PLAYERS, new ArrayList<>(), OPENING, new ArrayList<>(), OPENED, new ArrayList<>(), CLOSED, new ArrayList<>(), DELETING, new ArrayList<>(), CANCELLED, new ArrayList<>(), REBOOTED, new ArrayList<>())));
        this.setRewardSettings(new RewardSettings(this, UniInt.of(5, 15)));

        this.setModuleManager(new ModuleManager(this));
        this.setKeyIds(new HashSet<>());
        this.setRewardsMap(new LinkedHashMap<>());

        this.selfTick = new AtomicInteger();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, () -> Colorizer.apply(this.getName()))
                .add(Placeholders.DUNGEON_WORLD, () -> LangManager.getWorld(this.getWorld()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_KEY_IDS, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join(Colors.GRAY + ", " + Colors.LIGHT_PURPLE, this.getKeyIds())))
                .add(Placeholders.DUNGEON_NEXT_STAGE_IN, () -> TimeUtil.formatTimeLeft(System.currentTimeMillis() + this.getNextStageTime() * 1000L))
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> Colorizer.apply(Colors.LIGHT_PURPLE + this.getKeyIds().stream().map(f ->
                        Objects.requireNonNull(plugin.getKeyManager().getKeyById(f)).getName()).collect(Collectors.joining(Colors.GRAY + ", " + Colors.LIGHT_PURPLE))))
        ;
    }

    @Override
    public boolean load() {
        this.selfTick = new AtomicInteger();
        this.setSettings(MainSettings.read(this, cfg, ""));
        this.setPartySettings(PartySettings.read(this, cfg, "Party"));
        this.setModuleSettings(ModuleSettings.read(this, cfg, "Settings.Modules"));
        this.setStageSettings(StageSettings.read(this, cfg, "Settings.Stages"));
        this.setChestSettings(ChestSettings.read(this, cfg, "Settings.Chest"));
        this.setEffectSettings(EffectSettings.read(this, cfg, "Effects"));
        this.setAnnounceSettings(AnnounceSettings.read(this, cfg, "Settings.Announces"));
        this.setSchematicSettings(SchematicSettings.read(this, cfg, "Settings.Schematics"));
        this.setCommandsSettings(CommandsSettings.read(this, cfg, "Settings.Commands"));
        this.setRewardSettings(RewardSettings.read(this, cfg, "Settings.Reward"));

        String worldName = this.cfg.getString("World", "world");
        this.world = plugin.getServer().getWorld(worldName);
        if (this.world == null) {
            plugin.error("World '" + worldName + "' not found in server!");
            return false;
        }

        this.setKeyIds(cfg.getStringSet("Key.Ids"));
        this.setName(cfg.getString("Name", getId()));

        for (String rewId : cfg.getSection("Rewards.List")) {
            String path = "Rewards.List." + rewId;

            double rewChance = cfg.getDouble(path + ".Chance");

            String itemRaw = cfg.getString(path + ".Item");
            if (itemRaw == null || itemRaw.isEmpty()) {
                cfg.set(path + ".Item", ItemReader.write(new ItemStack(Material.DIAMOND)));
            }
            ItemStack item = ItemReader.read(cfg.getString(path + ".Item", null))[0];
            if (item == null)
                item = new ItemStack(Material.DIAMOND);

            if (cfg.contains(path + ".Max_Amount")){
                int maxAmount = cfg.getInt(path + ".Max_Amount", 3);
                int minAmount = cfg.getInt(path + ".Min_Amount", 1);
                UniInt amount = UniInt.of(minAmount, maxAmount);
                cfg.remove(path+".Min_Amount");
                cfg.remove(path+".Max_Amount");
                amount.write(cfg, ".Amount");
                cfg.saveChanges();
            }
            UniInt amount = UniInt.read(cfg, path + ".Amount");
            List<String> commands = cfg.getStringList(path + ".Commands");

            Reward reward = new Reward(this, rewId, rewChance, amount, item, commands);
            this.rewardMap.put(rewId, reward);
        }

        this.setDungeonRegion(Region.read(this, cfg, "Settings.Region"));
        this.setHologramSettings(HologramSettings.read(this, cfg, "Hologram.Chest"));

        if (this.getDungeonRegion().isEnabled() && plugin.getRegionHandler() == null) {
            this.plugin.error("Warning! Dungeon '" + getId() + "' wants to use the region system, but the Region handler is not installed!");
        }
        this.moduleManager = new ModuleManager(this);
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", getName());

        this.getDungeonRegion().write(cfg, "Settings.Region");
        this.getSettings().write(cfg, "");
        this.getHologramSettings().write(cfg, "Hologram.Chest");
        this.getPartySettings().write(cfg, "Party");
        this.getModuleSettings().write(cfg, "Settings.Modules");
        this.getStageSettings().write(cfg, "Settings.Stages");
        this.getChestSettings().write(cfg, "Settings.Chest");
        this.getEffectSettings().write(cfg, "Effects");
        this.getAnnounceSettings().write(cfg, "Settings.Announces");
        this.getSchematicSettings().write(cfg, "Settings.Schematics");
        this.getCommandsSettings().write(cfg, "Settings.Commands");
        this.getRewardSettings().write(cfg, "Settings.Reward");

        cfg.set("Rewards.List", null);
        for (Map.Entry<String, Reward> e : this.getRewardsMap().entrySet()) {
            Reward reward = e.getValue();
            String path = "Rewards.List." + e.getKey() + ".";
            cfg.set(path + "Item", ItemReader.write(reward.getItem()));
            cfg.set(path + "Chance", reward.getChance());
            reward.getAmount().write(cfg, path + "Amount");
        }

        cfg.set("World", this.getWorld().getName());
        cfg.set("Key.Ids", this.getKeyIds());
        cfg.saveChanges();
    }

    @Override
    public void clear() {
        this.cancel(true);
        this.getModuleManager().getModules().forEach(AbstractModule::shutdown);
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.rewardMap != null) {
            this.rewardMap.values().forEach(Reward::clear);
            this.rewardMap.clear();
            this.rewardMap = null;
        }
        this.selfTick.set(0);
    }

    @NotNull
    public DungeonManager getManager() {
        return manager;
    }

    @NotNull
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    @NotNull
    public ModuleSettings getModuleSettings() {
        return moduleSettings;
    }

    @NotNull
    public StageSettings getStageSettings() {
        return stageSettings;
    }

    @NotNull
    public ChestSettings getChestSettings() {
        return chestSettings;
    }

    @NotNull
    public EffectSettings getEffectSettings() {
        return effectSettings;
    }

    @NotNull
    public AnnounceSettings getAnnounceSettings() {
        return announceSettings;
    }

    @NotNull
    public SchematicSettings getSchematicSettings() {
        return schematicSettings;
    }

    @NotNull
    public CommandsSettings getCommandsSettings() {
        return commandsSettings;
    }

    public RewardSettings getRewardSettings() {
        return rewardSettings;
    }

    @NotNull
    public DungeonMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new DungeonMainEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public Set<String> getKeyIds() {
        return keyIds;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Cuboid getDungeonCuboid() {
        return cuboid;
    }

    @NotNull
    public DungeonStage getStage() {
        return stage;
    }

    @NotNull
    public MainSettings getSettings() {
        return settings;
    }

    @NotNull
    public HologramSettings getHologramSettings() {
        return hologramSettings;
    }

    @NotNull
    public PartySettings getPartySettings() {
        return partySettings;
    }

    @NotNull
    public Map<String, Reward> getRewardsMap() {
        return this.rewardMap;
    }

    @Nullable
    public Reward getReward(@NotNull String id) {
        return this.getRewardsMap().get(id.toLowerCase());
    }

    @NotNull
    public Collection<Reward> getRewards() {
        return this.getRewardsMap().values();
    }

    @NotNull
    public AtomicInteger getSelfTick() {
        return selfTick;
    }

    @NotNull
    public Region getDungeonRegion() {
        return region;
    }

    public int getNextStageTime() {
        return this.getStageSettings().getTime(this.getStage()) - this.getSelfTick().get();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return placeholderMap;
    } //

    public void setModuleManager(@Nullable ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void setKeyIds(@NotNull Set<String> keyIds) {
        this.keyIds = new HashSet<>(keyIds.stream().filter(Predicate.not(String::isEmpty)).map(String::toLowerCase).toList());
    }

    public void setWorld(@NotNull World world) {
        this.world = world;
    }

    public void setCuboid(@Nullable Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void setStage(@NotNull DungeonStage stage) {
        this.stage = stage;
    }

    public void setSettings(@NotNull MainSettings settings) {
        this.settings = settings;
    }

    public void setHologramSettings(@NotNull HologramSettings hologramSettings) {
        this.hologramSettings = hologramSettings;
    }

    public void setPartySettings(@NotNull PartySettings partySettings) {
        this.partySettings = partySettings;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setModuleSettings(@NotNull ModuleSettings moduleSettings) {
        this.moduleSettings = moduleSettings;
    }

    public void setStageSettings(@NotNull StageSettings stageSettings) {
        this.stageSettings = stageSettings;
    }

    public void setChestSettings(@NotNull ChestSettings chestSettings) {
        this.chestSettings = chestSettings;
    }

    public void setEffectSettings(@NotNull EffectSettings effectSettings) {
        this.effectSettings = effectSettings;
    }

    public void setAnnounceSettings(@NotNull AnnounceSettings announceSettings) {
        this.announceSettings = announceSettings;
    }

    public void setSchematicSettings(@NotNull SchematicSettings schematicSettings) {
        this.schematicSettings = schematicSettings;
    }

    public void setCommandsSettings(@NotNull CommandsSettings commandsSettings) {
        this.commandsSettings = commandsSettings;
    }

    public void setRewardSettings(RewardSettings rewardSettings) {
        this.rewardSettings = rewardSettings;
    }

    public void setRewardsMap(@NotNull Map<String, Reward> rewards) {
        this.rewardMap = rewards;
    }

    public void setRewards(@NotNull List<Reward> rewards) {
        this.setRewardsMap(rewards.stream().collect(
                Collectors.toMap(Reward::getId, Function.identity(), (has, add) -> add, LinkedHashMap::new)));
    }

    public void addReward(@NotNull Reward Reward) {
        this.getRewardsMap().put(Reward.getId(), Reward);
    }

    public void removeReward(@NotNull Reward Reward) {
        this.removeReward(Reward.getId());
    }

    public void removeReward(@NotNull String id) {
        this.getRewardsMap().remove(id);
    }

    public void setDungeonRegion(@NotNull Region region) {
        this.region = region;
    }

    public void setSelfTick(int tick) {
        this.selfTick.set(tick);
    }

    public void reboot() {
        this.plugin.warn("Starting the reboot '" + this.getId() + "' dungeon!");
        DungeonStage.call(this, REBOOTED, "Reboot from gui");
        DungeonStage.call(this, CANCELLED, "Reboot start");
    }

    public void cancel(boolean shutdown) {
        if (this.getLocation() != null) {
            if (plugin.getSchematicHandler() != null) {
                this.plugin.getSchematicHandler().undo(this);
            }
            this.setLocation(null);
        }

        this.setCuboid(null);

        if (!shutdown) {
            DungeonStage.call(this, FREEZE, "cancelled and refresh");
        }

        if (plugin.getRegionHandler() != null) {
            plugin.getRegionHandler().delete(this);
        }
        this.getModuleManager().getModules().forEach(AbstractModule::deactivate);
        this.setSelfTick(0);
    }

    public void tick() {
        this.getModuleManager().getModules().forEach(AbstractModule::update);
        this.getStage().tick(this);
    }
}
