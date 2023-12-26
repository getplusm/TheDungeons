package t.me.p1azmer.plugin.dungeons.dungeon.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.mob.MobFaction;
import t.me.p1azmer.plugin.dungeons.api.mob.MobList;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonRegion;
import t.me.p1azmer.plugin.dungeons.dungeon.categories.DungeonReward;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.ModuleManager;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.*;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestMenu;
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
    private EffectSettings effectSettings;

    private Map<String, DungeonReward> rewardMap;
    private Set<String> keyIds;
    private MobList mobList;

    private DungeonCuboid dungeonCuboid;
    private final DungeonManager manager;
    private ModuleManager moduleManager;
    private final PlaceholderMap placeholderMap;

    // weak cache
    private AtomicInteger timer = new AtomicInteger(0);
    private final Map<Block, DungeonChestMenu> cachedMenus = new LinkedHashMap<>();
    private long idleTime = 0;
    private boolean forceChange = false;
    private int currentTick = 0;
    private int generateState = 0;
    private DungeonMainEditor editor;
    private World world;
    private Location location;

    public Dungeon(@NotNull DungeonManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        DungeonStage.call(this, FREEZE, "loading");
        this.setKeyIds(new HashSet<>());
        this.setRewardsMap(new LinkedHashMap<>());
        this.setModuleManager(new ModuleManager(this));

        this.mobList = new MobList();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, this::getName)
                .add(Placeholders.DUNGEON_WORLD, () -> LangManager.getWorld(this.getWorld()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_KEY_IDS, () -> Colorizer.apply(Colors.LIGHT_PURPLE + String.join(", ", this.getKeyIds())))
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> this.getKeyIds().stream().map(f ->
                        Objects.requireNonNull(plugin.getKeyManager().getKeyById(f)).getName()).collect(Collectors.joining(", ")))
        ;
    }

    @Override
    public boolean load() {
        this.setSettings(MainSettings.read(this, cfg, ""));
        this.setPartySettings(PartySettings.read(this, cfg, "Party"));
        this.setModuleSettings(ModuleSettings.read(cfg, "Settings.Modules"));
        this.setStageSettings(StageSettings.read(cfg, "Settings.Stages"));
        this.setEffectSettings(EffectSettings.read(cfg, "Effects"));

        if (this.getSettings().getChestMaterial().isAir()) {
            plugin.error("Material of chest is air in dungeon: " + getId());
            return false;
        }

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
        if (this.getRewardsMap().isEmpty()) {
            plugin.warn("Dungeon '" + getId() + "' has empty rewards, it will not be spawned!");
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
        this.getEffectSettings().write(cfg, "Effects");

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
        this.cachedMenus.clear();
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

    public EffectSettings getEffectSettings() {
        return effectSettings;
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

    public boolean isForceChange() {
        return forceChange;
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

    public int getGenerateState() {
        return generateState;
    }

    @NotNull
    public MobList getMobs() {
        if (this.mobList == null)
            this.mobList = new MobList();

        this.mobList.getEnemies().removeIf(mob -> !mob.isValid() || mob.isDead());
        return mobList;
    }

    @NotNull
    public AtomicInteger getTimer() {
        return timer;
    }

    public long getIdleTime() {
        return idleTime;
    }

    @NotNull
    public DungeonRegion getDungeonRegion() {
        return dungeonRegion;
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

    public void setDungeonCuboid(@NotNull DungeonCuboid dungeonCuboid) {
        this.dungeonCuboid = dungeonCuboid;
    }

    public void setStage(@NotNull DungeonStage stage) {
        this.stage = stage;
    }

    public void setForceChange(boolean forceChange) {
        this.forceChange = forceChange;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setModuleSettings(@NotNull ModuleSettings moduleSettings) {
        this.moduleSettings = moduleSettings;
    }

    public void setStageSettings(@NotNull StageSettings stageSettings) {
        this.stageSettings = stageSettings;
    }

    public void setEffectSettings(EffectSettings effectSettings) {
        this.effectSettings = effectSettings;
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

    public void setGenerateState(int generateState, String from) {
        //this.plugin.debug("Change generator state at " + this.getGenerateState() + " to " + generateState + ", " + from);
        this.generateState = generateState;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public void setDungeonRegion(@NotNull DungeonRegion dungeonRegion) {
        this.dungeonRegion = dungeonRegion;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
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

        this.cachedMenus.values().forEach(DungeonChestMenu::clear);
        this.cachedMenus.clear();
        this.setDungeonCuboid(null);

        if (!shutdown) {
            this.setIdleTime(0);
            DungeonStage.call(this, FREEZE, "cancelled and refresh");
        }

        if (plugin.getRegionHandler() != null) {
            plugin.getRegionHandler().delete(this);
        }
        this.getModuleManager().getModules().forEach(AbstractModule::deactivate);
        this.getTimer().set(0);
    }

    public void tick() {
        this.setCurrentTick(this.getTimer().get());
        this.getStage().tick(this);

//        if (!getStage().isFreeze()) {
//            this.idleTime++;
//        }
//        if (this.generateState == 0 && this.getStage().isFreeze() && (this.getSettings().getRefreshTime() <= 5 || time + 30 >= this.getSettings().getRefreshTime())) {
//            this.startGenerators();
//            this.plugin.runTaskTimer(bukkitTask -> {
//                if (this.getGenerateState() == -1) {
//                    this.call(CANCELLED, "Cannot generate location!");
//                    bukkitTask.cancel();
//                    this.setGenerateState(0, "tick");
//                } else if (this.getGenerateState() == 2) {
//                    bukkitTask.cancel();
//                    this.setGenerateState(3, "tick");
//                }
//            }, 1, 10L);
//        }
//
//        if (this.getStage().isOpen() && this.getIdleTime() >= Config.IDLE_BREAK.get()) {
//            this.call(CANCELLED, "tick");
//            return;
//        }
//        if (this.bossBar == null && Config.BOSSBAR_ENABLED.get()) {
//            this.bossBar = Bukkit.createBossBar(Colorizer.apply(Config.BOSSBAR_TITLE.get()), Config.BOSSBAR_COLOR.get(), Config.BOSSBAR_STYLE.get());
//        }
//
//        switch (this.getStage()) {
//            case WAITING -> {
//                if (this.getSettings().getRegionWaitTime() == time) {
//                    plugin.getServer().getOnlinePlayers().forEach(all ->
//                            plugin.getMessage(Lang.NOTIFY_SINGLE)
//                                    .replace(this.getSettings().replacePlaceholders(time))
//                                    .replace(this.replacePlaceholders())
//                                    .send(all));
//                } else {
//                    plugin.getServer().getOnlinePlayers().forEach(all -> {
//                        plugin.getMessage(Lang.NOTIFY_EVERY)
//                                .replace(this.getSettings().replacePlaceholders(time))
//                                .replace(this.replacePlaceholders())
//                                .send(all);
//                        if (Config.BOSSBAR_ENABLED.get()) {
//                            if (!this.bossBar.getPlayers().contains(all))
//                                this.bossBar.addPlayer(all);
//                            String text = Config.BOSSBAR_TITLE.get();
//                            text = Colorizer.apply(this.getSettings().replacePlaceholders(time).apply(text));
//                            text = Colorizer.apply(this.replacePlaceholders().apply(text));
//                            this.bossBar.setTitle(text);
//                            double progress = (double) (this.getSettings().getRegionWaitTime() - time) / this.getSettings().getRegionWaitTime();
//                            progress = Math.max(0.0, Math.min(progress, 1.0));
//                            this.bossBar.setProgress(progress);
//                        }
//                    });
//                }
//            }
//            case CLOSED, OPENING -> {
//                if (bossBar != null) {
//                    this.bossBar.removeAll();
//                }
//            }
//        }
    }

    public enum OpenType {
        CLICK,
        TIMER;

        public boolean isClick() {
            return this.equals(CLICK);
        }
    }
}
