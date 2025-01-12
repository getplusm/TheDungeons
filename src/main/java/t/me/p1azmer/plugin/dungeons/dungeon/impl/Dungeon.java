package t.me.p1azmer.plugin.dungeons.dungeon.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
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
import t.me.p1azmer.plugin.dungeons.api.handler.region.RegionHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.OpenType;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleId;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends AbstractConfigHolder<DungeonPlugin> implements ICleanable, Placeholder {

    String name;
    Region region;

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

    Set<String> keyIds;
    Map<String, Reward> rewardsMap;

    World world;
    Cuboid cuboid;
    Location location;

    ModuleManager moduleManager;
    final DungeonManager manager;
    final PlaceholderMap placeholderMap;
    final LocationGenerator locationGenerator;
    final ThreadSync threadSync;

    // ----------------------------------- cache ----------------------------------------------
    AtomicInteger selfTick;
    DungeonMainEditor editor;
    DungeonStage stage;
    // ----------------------------------------------------------------------------------------

    public Dungeon(@NotNull DungeonManager manager, @NotNull JYML cfg,
                   @NotNull LocationGenerator locationGenerator, @NotNull ThreadSync threadSync) {
        super(manager.plugin(), cfg);
        this.manager = manager;
        this.locationGenerator = locationGenerator;
        this.threadSync = threadSync;

        handleRegisterRegion();
        handleRegisterSettings();

        this.setStage(FREEZE);
        this.setModuleManager(new ModuleManager(this, locationGenerator));
        this.setKeyIds(new HashSet<>());
        this.setRewardsMap(new ConcurrentHashMap<>());

        this.selfTick = new AtomicInteger();
        this.placeholderMap = generatePlaceholderMap();
    }

    @Override
    public boolean load() {
        this.selfTick = new AtomicInteger();
        this.setSettings(MainSettings.read(this, cfg, "Settings"));
        setAccessSettings(AccessSettings.read(this, cfg, "Settings.Access"));
        this.setMobsSettings(MobsSettings.read(this, cfg, "Mobs"));
        this.setPartySettings(PartySettings.read(this, cfg, "Party"));
        this.setGenerationSettings(GenerationSettings.read(this, cfg, "Settings.Generation"));
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

        RangeInfo rangeInfo = GeneratorConfig.LOCATION_SEARCH_RANGES.get().get(this.getWorld().getName());
        if (rangeInfo == null) {
            plugin.error("Unable to load the dungeon '" + this.getId() + "' because you have not created a location generator with the world '" + this.getWorld().getName() + "'. Go to /plugins/TheDungeons/config.yml and setup the generator!");
            return false;
        }

        this.setKeyIds(cfg.getStringSet("Key.Ids"));
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
            this.rewardsMap.put(rewId, reward);
        }

        this.setRegion(Region.read(this, cfg, "Settings.Region"));
        this.setHologramSettings(HologramSettings.read(this, cfg, "Hologram.Chest"));

        if (this.getRegion().isEnabled() && plugin.getRegionHandler() == null) {
            this.plugin().error("Warning! Dungeon '" + getId() + "' wants to use the region system, but the Region handler is not installed!");
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
                this.plugin().error("It is impossible to load the dungeon '" + this.getId() + "', since the spawn location is not set, and the generation type is " + generationType.name());
                return false;
            }
            this.setLocation(spawnLocation);
        }
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", getName());

        this.getRegion().write(cfg, "Settings.Region");
        this.getSettings().write(cfg, "");
        this.getMobsSettings().write(cfg, "Mobs");
        getAccessSettings().write(cfg, "Settings.Access");
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
        this.getGenerationSettings().write(cfg, "Settings.Generation");

        cfg.set("Rewards.List", null);
        for (Map.Entry<String, Reward> e : this.getRewardsMap().entrySet()) {
            Reward reward = e.getValue();
            String path = "Rewards.List." + e.getKey() + ".";
            cfg.setItemEncoded(path + "Item", reward.getItem());
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
        this.getModuleManager()
                .getModules()
                .forEach(AbstractModule::shutdown);
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.rewardsMap != null) {
            this.rewardsMap
                    .values()
                    .forEach(Reward::clear);
            this.rewardsMap.clear();
            this.rewardsMap = null;
        }
        this.selfTick.set(0);
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

    public Optional<Reward> getReward(@NotNull String id) {
        return Optional.ofNullable(this.getRewardsMap().get(id.toLowerCase()));
    }

    public @NotNull Collection<Reward> getRewards() {
        return this.getRewardsMap().values();
    }

    public int getNextStageTime() {
        StageSettings settings = this.getStageSettings();
        return settings.getTime(this.getStage()) - this.getSelfTick().get();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return placeholderMap;
    }

    public void setKeyIds(@NotNull Set<String> keyIds) {
        this.keyIds = keyIds.stream()
                .filter(Predicate.not(String::isEmpty))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public void setSelfTick(int tick) {
        this.selfTick.set(tick);
    }

    public void setRewards(@NotNull List<Reward> rewards) {
        this.setRewardsMap(
                rewards.stream()
                        .collect(Collectors.toMap(Reward::getId, Function.identity(), (has, add) -> add, LinkedHashMap::new))
        );
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

    public void reboot() {
        this.plugin().warn("Starting the reboot '" + this.getId() + "' dungeon!");

        CompletableFuture.runAsync(() -> {
            DungeonStage.call(this, REBOOTED, "Reboot from gui");
            DungeonStage.call(this, CANCELLED, "Reboot start");
        });
    }

    public void cancel(boolean shutdown) {
        if (!shutdown) DungeonStage.call(this, FREEZE, "cancelled and refresh");

        RegionHandler regionHandler = this.plugin().getRegionHandler();
        GenerationType generationType = this.getGenerationSettings().getGenerationType();
        AbstractModule.ActionType actionType = AbstractModule.ActionType.of(shutdown);

        this.getModuleManager()
                .getModules()
                .forEach(module -> module.tryDeactivate(actionType));
        if (regionHandler != null && this.getRegion().isCreated())
            regionHandler.delete(this);

        if (generationType.isDynamic()) {
            this.setLocation(null);
            this.setCuboid(null);
        }
        this.setSelfTick(0);
    }

    public void tick() {
        for (AbstractModule module : this.getModuleManager().getModules()) {
            module.update();
        }
        this.getStage().tick(this);
    }

    private @NotNull PlaceholderMap generatePlaceholderMap() {
        return new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, () -> Colorizer.apply(this.getName()))
                .add(Placeholders.DUNGEON_WORLD, () -> LangManager.getWorld(this.getWorld()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_KEY_IDS, () -> {
                    String keys = String.join(Colors2.GRAY + ", " + Colors2.LIGHT_PURPLE, this.getKeyIds());
                    return Colorizer.apply(Colors2.LIGHT_PURPLE + keys);
                })
                .add(Placeholders.DUNGEON_NEXT_STAGE_IN, () -> TimeUtil.formatTimeLeft(System.currentTimeMillis() + this.getNextStageTime() * 1000L))
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> {
                    KeyManager keyManager = plugin.getKeyManager();
                    return Colorizer.apply(Colors2.LIGHT_PURPLE + this.getKeyIds()
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
        this.setHologramSettings(hologramSettings);
        this.setSettings(mainSettings);
        setAccessSettings(accessSettings);
        this.setSchematicSettings(schematicSettings);
        this.setModuleSettings(moduleSettings);
        this.setStageSettings(stageSettings);
        this.setChestSettings(chestSettings);
        this.setCommandsSettings(commandsSettings);
        this.setPartySettings(new PartySettings(this, false, 2));
        this.setGenerationSettings(new GenerationSettings(this, GenerationType.DYNAMIC, null));
        this.setAnnounceSettings(new AnnounceSettings(this, defaultAnnounceMap));
        this.setEffectSettings(new EffectSettings(this, false, new ArrayList<>()));
        this.setRewardSettings(new RewardSettings(this, UniInt.of(1, 10)));
        this.setMobsSettings(new MobsSettings(this, false, new HashMap<>()));
    }
}