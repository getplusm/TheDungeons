package t.me.p1azmer.plugin.dungeons.dungeon.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.wrapper.UniInt;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.OpenType;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.models.Keys;
import t.me.p1azmer.plugin.dungeons.dungeon.models.Rewards;
import t.me.p1azmer.plugin.dungeons.dungeon.models.Timer;
import t.me.p1azmer.plugin.dungeons.dungeon.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.module.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.module.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.*;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.generator.RangeInfo;
import t.me.p1azmer.plugin.dungeons.generator.config.GeneratorConfig;
import t.me.p1azmer.plugin.dungeons.key.KeyManager;
import t.me.p1azmer.plugin.dungeons.scheduler.ThreadSync;
import t.me.p1azmer.plugin.dungeons.utils.Cuboid;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends AbstractConfigHolder<DungeonPlugin> implements ICleanable, Placeholder {

    final DungeonManager dungeonManager;
    final PlaceholderMap placeholderMap;
    final LocationGenerator locationGenerator;
    final ThreadSync threadSync;

    World world;
    String name;
    Region region;
    Cuboid cuboid;
    Location location;

    MainSettings settings;
    MobsSettings mobsSettings;
    PartySettings partySettings;
    StageSettings stageSettings;
    ChestSettings chestSettings;
    AccessSettings accessSettings;
    RewardSettings rewardSettings;
    ModuleSettings moduleSettings;
    EffectSettings effectSettings;
    AnnounceSettings announceSettings;
    CommandsSettings commandsSettings;
    HologramSettings hologramSettings;
    SchematicSettings schematicSettings;
    GenerationSettings generationSettings;

    Keys keys;
    Timer timer;
    Rewards rewards;
    DungeonStage stage;
    DungeonMainEditor editor;
    ModuleManager moduleManager;

    public Dungeon(@NotNull DungeonManager dungeonManager, @NotNull JYML cfg,
                   @NotNull LocationGenerator locationGenerator, @NotNull ThreadSync threadSync) {
        super(dungeonManager.plugin(), cfg);

        this.dungeonManager = dungeonManager;
        this.locationGenerator = locationGenerator;
        this.threadSync = threadSync;

        setTimer(new Timer(this));
        setStage(FREEZE);
        setStageSettings(new StageSettings(this, new HashMap<>()));
        getTimer().updateInstant(getStage());
        setRewards(new Rewards());
        setKeys(new Keys());
        setModuleManager(new ModuleManager(this, locationGenerator));
        handleRegisterRegion();
        handleRegisterSettings();

        this.placeholderMap = generatePlaceholderMap();
    }

    @Override
    public boolean load() {
        try {
            setSettings(MainSettings.read(this, cfg, "Settings"));
            setAccessSettings(AccessSettings.read(this, cfg, "Settings.Access"));
            setMobsSettings(MobsSettings.read(this, cfg, "Mobs"));
            setPartySettings(PartySettings.read(this, cfg, "Party"));
            setGenerationSettings(GenerationSettings.read(this, cfg, "Settings.Generation"));
            setModuleSettings(ModuleSettings.read(this, cfg, "Settings.Modules"));
            setStageSettings(StageSettings.read(this, cfg, "Settings.Stages"));
            setChestSettings(ChestSettings.read(this, cfg, "Settings.Chest"));
            setEffectSettings(EffectSettings.read(this, cfg, "Effects"));
            setAnnounceSettings(AnnounceSettings.read(this, cfg, "Settings.Announces"));
            setSchematicSettings(SchematicSettings.read(this, cfg, "Settings.Schematics"));
            setCommandsSettings(CommandsSettings.read(this, cfg, "Settings.Commands"));
            setRewardSettings(RewardSettings.read(this, cfg, "Settings.Reward"));

            setTimer(new Timer(this));

            String worldName = this.cfg.getString("World", "world");
            World world = Optional.ofNullable(Bukkit.getWorld(worldName)).orElseThrow(() -> {
                return new IllegalStateException("World '" + worldName + "' not found!");
            });
            setWorld(world);

            RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(this.getWorld().getName());
            if (rangeInfo == null) {
                plugin.error("Unable to load the dungeon '" + this.getId() + "' because you have not created a location generator with the world '" + this.getWorld().getName() + "'. Go to /plugins/TheDungeons/config.yml and setup the generator!");
                return false;
            }

            getKeys().setKeyIds(cfg.getStringSet("Key.Ids"));
            this.setName(cfg.getString("Name", getId()));

            for (String rewId : cfg.getSection("Rewards.List")) {
                String path = "Rewards.List." + rewId;

                double rewChance = cfg.getDouble(path + ".Chance");

                String itemRaw = cfg.getString(path + ".Item");
                if (itemRaw == null || itemRaw.isEmpty()) {
                    cfg.setItemEncoded(path + ".Item", new ItemStack(Material.DIAMOND));
                }
                ItemStack item = cfg.getItemEncoded(path + ".Item");
                if (item == null)
                    item = new ItemStack(Material.DIAMOND);

                if (cfg.contains(path + ".Max_Amount")) {
                    int maxAmount = cfg.getInt(path + ".Max_Amount", 3);
                    int minAmount = cfg.getInt(path + ".Min_Amount", 1);
                    UniInt amount = UniInt.of(minAmount, maxAmount);
                    cfg.remove(path + ".Min_Amount");
                    cfg.remove(path + ".Max_Amount");
                    amount.write(cfg, ".Amount");
                    cfg.saveChanges();
                }
                UniInt amount = UniInt.read(cfg, path + ".Amount");
                List<String> commands = cfg.getStringList(path + ".Commands");

                Reward reward = new Reward(this, rewId, rewChance, amount, item, commands);
                getRewards().addReward(rewId, reward);
            }

            this.setRegion(Region.read(this, cfg, "Settings.Region"));
            this.setHologramSettings(HologramSettings.read(this, cfg, "Hologram.Chest"));

            if (this.getRegion().isEnabled() && plugin.getRegionHandler() == null) {
                this.plugin().error("Warning! The dungeon '" + getId() + "' wants to use the regional system, but the region handler is not installed!");
            }
            this.moduleManager = new ModuleManager(this, locationGenerator);

            GenerationSettings generationSettings = this.getGenerationSettings();
            GenerationType generationType = generationSettings.getGenerationType();
            if (generationType.isStatic()) {
                if (Config.OTHER_DEBUG.get()) {
                    this.plugin().sendDebug("Prepare static generation settings for " + this.getId() + " dungeon");
                }
                Location spawnLocation = generationSettings
                        .getSpawnLocation()
                        .orElse(null);
                if (spawnLocation == null) {
                    this.plugin().error("It is impossible to load the dungeon " + this.getId() + ", since the spawn location has not been set, and the type of generation is " + generationType.name());
                    return false;
                }
                this.setLocation(spawnLocation);
            }
            return true;
        } catch (Exception exception) {
            DungeonPlugin.getLog().log(Level.SEVERE, "Dungeon '" + this.getId() + "' could not be loaded", exception);
            return false;
        }
    }

    @Override
    public void onSave() {
        cfg.set("Name", getName());

        getRegion().write(cfg, "Settings.Region");
        getSettings().write(cfg, "Settings");
        getMobsSettings().write(cfg, "Mobs");
        getAccessSettings().write(cfg, "Settings.Access");
        getHologramSettings().write(cfg, "Hologram.Chest");
        getPartySettings().write(cfg, "Party");
        getModuleSettings().write(cfg, "Settings.Modules");
        getStageSettings().write(cfg, "Settings.Stages");
        getChestSettings().write(cfg, "Settings.Chest");
        getEffectSettings().write(cfg, "Effects");
        getAnnounceSettings().write(cfg, "Settings.Announces");
        getSchematicSettings().write(cfg, "Settings.Schematics");
        getCommandsSettings().write(cfg, "Settings.Commands");
        getRewardSettings().write(cfg, "Settings.Reward");
        getGenerationSettings().write(cfg, "Settings.Generation");

        cfg.set("Rewards.List", null);
        for (Map.Entry<String, Reward> e : getRewards().getRewardsMap().entrySet()) {
            Reward reward = e.getValue();
            String path = "Rewards.List." + e.getKey() + ".";
            cfg.setItemEncoded(path + "Item", reward.getItem());
            cfg.set(path + "Chance", reward.getChance());
            reward.getAmount().write(cfg, path + "Amount");
        }

        cfg.set("World", getWorld().getName());
        cfg.set("Key.Ids", getKeys().getKeyIds());
        cfg.saveChanges();
    }

    @Override
    public void clear() {
        getTimer().cancel(true);
        this.getModuleManager()
                .getModules()
                .forEach(AbstractModule::shutdown);
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public DungeonMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new DungeonMainEditor(this);
        }
        return this.editor;
    }

    public Optional<Location> getLocation() {
        return Optional.ofNullable(this.location);
    }

    public Optional<Cuboid> getDungeonCuboid() {
        return Optional.ofNullable(cuboid);
    }

    public @NotNull Collection<Reward> getRewardCollection() {
        return getRewards().getRewardsMap().values();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return placeholderMap;
    }

    private @NotNull PlaceholderMap generatePlaceholderMap() {
        return new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, () -> Colorizer.apply(this.getName()))
                .add(Placeholders.DUNGEON_WORLD, () -> LangManager.getWorld(this.getWorld()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_NEXT_STAGE_IN, () -> TimeUtil.formatTime(getTimer().getTimeToNextStageInMillis()))
                .add(Placeholders.DUNGEON_KEY_IDS, () -> {
                    String keys = String.join(Colors2.GRAY + ", " + Colors2.LIGHT_PURPLE, getKeys().getKeyIds());
                    return Colorizer.apply(Colors2.LIGHT_PURPLE + keys);
                })
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> {
                    KeyManager keyManager = plugin.getKeyManager();
                    return Colorizer.apply(Colors2.LIGHT_PURPLE + getKeys().getKeyIds()
                            .stream()
                            .filter(founder -> keyManager.getKeyById(founder) != null)
                            .map(f -> Objects.requireNonNull(keyManager.getKeyById(f)).getName())
                            .collect(Collectors.joining(Colors2.GRAY + ", " + Colors2.LIGHT_PURPLE)));
                });
    }

    private void handleRegisterRegion() {
        val region = new Region(this, true, this.getId(), 15, List.of(
                "pistons deny",
                "pvp allow",
                "use allow",
                "chest-access allow"
        ));
        this.setRegion(region);
    }

    private void handleRegisterSettings() {
        val announceManager = plugin().getAnnounceManager();
        val defaultAnnounceMap = Map.of(
                PREPARE, Map.of(Objects.requireNonNull(announceManager.getAnnounce("prepare_default")), new int[]{26, 27, 28, 29, 30}),
                CLOSED, Map.of(Objects.requireNonNull(announceManager.getAnnounce("closed_default")), new int[]{0})
        );
        val accessSettings = new AccessSettings(this, false, new HashSet<>(), null);
        val mainSettings = new MainSettings(this, false, false,
                false, 1);
        val hologramSettings = new HologramSettings(this, 2, Map.of(
                ChestState.WAITING,
                List.of("#d8c2ffDungeon chest", "#dec1d2Status: #4dffc3Waiting you", "#FFC458Click me if you have key!"),
                ChestState.COOLDOWN,
                List.of("#d8c2ffDungeon chest", "#dec1d2Status: #db3251Closed",
                        "&eOpening in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN),
                ChestState.OPENED,
                List.of("#d8c2ffDungeon chest", "#dec1d2Status: #7fffd4Opened",
                        "#dec1d2Closing in: #99ff99" + Placeholders.DUNGEON_CHEST_NEXT_STATE_IN)
        ));
        val schematicSettings = new SchematicSettings(
                this,
                List.of("dungeon_rotten_mushroom"),
                true,
                false
        );
        val moduleSettings = new ModuleSettings(this, Map.of(
                ModuleId.SPAWN, true,
                ModuleId.ANNOUNCE, true,
                ModuleId.CHEST, true,
                ModuleId.COMMAND, true,
                ModuleId.HOLOGRAM, true,
                ModuleId.SCHEMATIC, true
        ));
        val stageSettings = new StageSettings(this, Map.of(
                FREEZE, 5,
                CHECK, 3,
                PREPARE, 30,
                WAITING_PLAYERS, 10,
                OPENING, 5,
                OPENED, 60,
                CLOSED, 5,
                DELETING, 1,
                CANCELLED, 1,
                REBOOTED, 1
        ));
        val chestSettings = new ChestSettings(this, Map.of(
                ChestState.WAITING, 10,
                ChestState.COOLDOWN, 5,
                ChestState.OPENED, 10,
                ChestState.CLOSED, 10,
                ChestState.DELETED, 1),
                3, false, false, false, false,
                OpenType.CLICK, Material.BARREL);
        val commandsSettings = new CommandsSettings(
                this,
                Map.of(
                        FREEZE, Collections.emptySet(),
                        CHECK, Collections.emptySet(),
                        PREPARE, Collections.emptySet(),
                        WAITING_PLAYERS, Collections.emptySet(),
                        OPENING, Collections.emptySet(),
                        OPENED, Collections.emptySet(),
                        CLOSED, Collections.emptySet(),
                        DELETING, Collections.emptySet(),
                        CANCELLED, Collections.emptySet(),
                        REBOOTED, Collections.emptySet())
        );
        setHologramSettings(hologramSettings);
        setSettings(mainSettings);
        setAccessSettings(accessSettings);
        setSchematicSettings(schematicSettings);
        setModuleSettings(moduleSettings);
        setStageSettings(stageSettings);
        setChestSettings(chestSettings);
        setCommandsSettings(commandsSettings);
        setPartySettings(new PartySettings(this, false, 2));
        setGenerationSettings(new GenerationSettings(this, GenerationType.DYNAMIC, null));
        setAnnounceSettings(new AnnounceSettings(this, defaultAnnounceMap));
        setEffectSettings(new EffectSettings(this, false, new ArrayList<>()));
        setRewardSettings(new RewardSettings(this, UniInt.of(1, 10)));
        setMobsSettings(new MobsSettings(this, false, new HashMap<>()));
    }
}