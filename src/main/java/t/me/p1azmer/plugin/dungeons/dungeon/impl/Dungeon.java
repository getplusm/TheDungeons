package t.me.p1azmer.plugin.dungeons.dungeon.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.Announce;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.*;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageSettings;
import t.me.p1azmer.plugin.dungeons.utils.DungeonCuboid;
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

    private DungeonRegion dungeonRegion;
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

    private Map<String, DungeonReward> rewardMap;
    private Set<String> keyIds;
    private MobList mobList;

    private DungeonCuboid dungeonCuboid;
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

        this.setPartySettings(new PartySettings(this, false, 2));
        this.setHologramSettings(new HologramSettings(this, 2, Map.of(
                DungeonChestState.WAITING, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #4dffc3Waiting you", "#FFC458Click me if you have key!"),
                DungeonChestState.COOLDOWN, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #db3251Closed", "&eOpening in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN),
                DungeonChestState.OPENED, List.of("#d8c2ffDungeon chest", "#dec1d2Status: #7fffd4Opened", "#dec1d2Closing in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN))
        ));
        this.setSettings(new MainSettings(this, false, false, false, 1, new HashMap<>()));
        this.setDungeonRegion(new DungeonRegion(this, true, this.getId(), 15, List.of("pistons deny", "pvp allow", "use allow", "chest-access allow")));
        this.setAnnounceSettings(new AnnounceSettings(this, Map.of(
                PREPARE, new Announce(List.of(new LangMessage(plugin(),
                        "<! prefix:\"false\" !>" +
                                "\n" + Colors2.LIGHT_RED + Colors2.BOLD + "NOTE:" +
                                "\n" + Colors2.LIGHT_PURPLE + "Until the new dungeon is spawned:" +
                                "\n" + Colors2.LIGHT_ORANGE + Placeholders.DUNGEON_NEXT_STAGE_IN + Colors2.LIGHT_PURPLE + " sec." +
                                "\n"
                )), true, new int[]{26, 27, 28, 29, 30}),
                DungeonStage.CLOSED, new Announce(List.of(new LangMessage(plugin(),
                        """
                                <! prefix:"false" !>
                                #c71585Attention \u2757
                                #fcf2f8At the coordinates: #ffb6ad\u2690 %location_world%, %location_x%, %location_y%, %location_z%
                                #fcf2f8A dungeon has appeared #ffb6ad%dungeon_name%
                                #fcf2f8In order to open it you need
                                #ffe4e0\u25aa #c71585%dungeon_key_names%""")), true, new int[]{0}))));
        this.setSchematicSettings(new SchematicSettings(this, List.of("dungeon_rotten_mushroom"), true, false));
        this.setModuleSettings(new ModuleSettings(this, Map.of(ModuleId.SPAWN, true, ModuleId.ANNOUNCE, true, ModuleId.CHEST, true, ModuleId.COMMAND, true, ModuleId.HOLOGRAM, true, ModuleId.SCHEMATIC, true)));
        this.setStageSettings(new StageSettings(this, Map.of(FREEZE, 5, CHECK, 3, PREPARE, 30, WAITING_PLAYERS, 10, OPENING, 5, OPENED, 60, CLOSED, 5, DELETING, 1, CANCELLED, 1, REBOOTED, 1)));
        this.setChestSettings(new ChestSettings(this, Map.of(DungeonChestState.WAITING, 10, DungeonChestState.COOLDOWN, 5, DungeonChestState.OPENED, 10, DungeonChestState.CLOSED, 10, DungeonChestState.DELETED, 1), 3, false, false, false, false, ChestModule.OpenType.CLICK, Material.BARREL));
        this.setEffectSettings(new EffectSettings(this, false, new ArrayList<>()));
        this.setCommandsSettings(new CommandsSettings(this, Map.of(FREEZE, new ArrayList<>(), CHECK, new ArrayList<>(), PREPARE, new ArrayList<>(), WAITING_PLAYERS, new ArrayList<>(), OPENING, new ArrayList<>(), OPENED, new ArrayList<>(), CLOSED, new ArrayList<>(), DELETING, new ArrayList<>(), CANCELLED, new ArrayList<>(), REBOOTED, new ArrayList<>())));

        this.setMobList(new MobList());
        this.setModuleManager(new ModuleManager(this));
        this.setKeyIds(new HashSet<>());
        this.setRewardsMap(new LinkedHashMap<>());

        this.selfTick = new AtomicInteger();
        DungeonStage.call(this, FREEZE, "loading");

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, () -> Colorizer.apply(this.getName()))
                .add(Placeholders.DUNGEON_WORLD, () -> LangManager.getWorld(this.getWorld()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_KEY_IDS, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join(", ", this.getKeyIds())))
                .add(Placeholders.DUNGEON_NEXT_STAGE_IN, () -> String.valueOf(this.getNextStageTime()))
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> this.getKeyIds().stream().map(f ->
                        Objects.requireNonNull(plugin.getKeyManager().getKeyById(f)).getName()).collect(Collectors.joining(", ")))
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

        String worldName = this.cfg.getString("World", "world");
        this.world = plugin.getServer().getWorld(worldName);
        if (this.world == null) {
            plugin.error("World '" + worldName + "' not found in server!");
            return false;
        }

        this.setKeyIds(cfg.getStringSet("Key.Ids"));
        this.setName(cfg.getString("Name", getId()));

        for (String rewId : cfg.getSection("Rewards.List")) {
            String path = "Rewards.List." + rewId + ".";

            String rewName = cfg.getString(path + "Name", rewId);
            double rewChance = cfg.getDouble(path + "Chance");

            String itemRaw = cfg.getString(path + "Item");
            if (itemRaw == null || itemRaw.isEmpty()) {
                cfg.set(path + "Item", ItemReader.write(new ItemStack(Material.DIAMOND)));
            }
            ItemStack item = ItemReader.read(cfg.getString(path + "Item", null))[0];
            if (item == null)
                item = ItemReader.read(cfg.getString(path + "Item", null))[0];
            else {
                cfg.set(path + "Item", null);
                cfg.set(path + "Item", ItemReader.write(item));
            }

            int maxAmount = cfg.getInt(path + "Max_Amount", 3);
            int minAmount = cfg.getInt(path + "Min_Amount", 1);

            DungeonReward reward = new DungeonReward(this, rewId, rewName, rewChance, minAmount, maxAmount, item);
            this.rewardMap.put(rewId, reward);
        }

        this.setDungeonRegion(DungeonRegion.read(this, cfg, "Settings.Region"));
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

        cfg.set("Rewards.List", null);
        for (Map.Entry<String, DungeonReward> e : this.getRewardsMap().entrySet()) {
            DungeonReward reward = e.getValue();
            String path = "Rewards.List." + e.getKey() + ".";
            cfg.set(path + "Item", ItemReader.write(reward.getItem()));
            cfg.set(path + "Chance", reward.getChance());
            cfg.set(path + "Max_Amount", reward.getMaxAmount());
            cfg.set(path + "Min_Amount", reward.getMinAmount());
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
            this.rewardMap.values().forEach(DungeonReward::clear);
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
    public DungeonCuboid getDungeonCuboid() {
        return dungeonCuboid;
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
    public Map<String, DungeonReward> getRewardsMap() {
        return this.rewardMap;
    }

    @Nullable
    public DungeonReward getReward(@NotNull String id) {
        return this.getRewardsMap().get(id.toLowerCase());
    }

    @NotNull
    public Collection<DungeonReward> getRewards() {
        return this.getRewardsMap().values();
    }

    @NotNull
    public MobList getMobs() {
        if (this.mobList == null)
            this.mobList = new MobList();

        this.mobList.getEnemies().removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }

    @NotNull
    public AtomicInteger getSelfTick() {
        return selfTick;
    }

    @NotNull
    public DungeonRegion getDungeonRegion() {
        return dungeonRegion;
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

    public void setMobList(@NotNull MobList mobList) {
        this.mobList = mobList;
    }

    public void setWorld(@NotNull World world) {
        this.world = world;
    }

    public void setDungeonCuboid(@Nullable DungeonCuboid dungeonCuboid) {
        this.dungeonCuboid = dungeonCuboid;
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

    public void setRewardsMap(@NotNull Map<String, DungeonReward> rewards) {
        this.rewardMap = rewards;
    }

    public void setRewards(@NotNull List<DungeonReward> rewards) {
        this.setRewardsMap(rewards.stream().collect(
                Collectors.toMap(DungeonReward::getId, Function.identity(), (has, add) -> add, LinkedHashMap::new)));
    }

    public void addReward(@NotNull DungeonReward DungeonReward) {
        this.getRewardsMap().put(DungeonReward.getId(), DungeonReward);
    }

    public void removeReward(@NotNull DungeonReward DungeonReward) {
        this.removeReward(DungeonReward.getId());
    }

    public void removeReward(@NotNull String id) {
        this.getRewardsMap().remove(id);
    }

    public void setDungeonRegion(@NotNull DungeonRegion dungeonRegion) {
        this.dungeonRegion = dungeonRegion;
    }

    public void setSelfTick(int tick) {
        this.selfTick.set(tick);
    }

    public void killMobs() {
        for (MobFaction faction : MobFaction.values()) {
            this.killMobs(faction);
        }
    }

    public void killMobs(@NotNull MobFaction faction) {
        this.getMobs().removeAll(faction);
    }

    public void reboot() {
        this.plugin.warn("Starting the reboot '" + this.getId() + "' dungeon!");
        DungeonStage.call(this, REBOOTED, "Reboot from gui");
        DungeonStage.call(this, CANCELLED, "Reboot start");
    }

    public void cancel(boolean shutdown) {
        if (this.getLocation() != null) {
            this.plugin.getSchematicHandler().undo(this);
            this.killMobs();
            this.setLocation(null);
        }

        this.setDungeonCuboid(null);

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
        this.getStage().tick(this);
    }
}
