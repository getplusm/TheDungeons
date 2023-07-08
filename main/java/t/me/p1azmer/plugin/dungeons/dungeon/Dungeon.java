package t.me.p1azmer.plugin.dungeons.dungeon;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonMainEditor;
import t.me.p1azmer.plugin.dungeons.generator.ChestFiller;
import t.me.p1azmer.plugin.dungeons.generator.ChestFinder;
import t.me.p1azmer.plugin.dungeons.generator.RandomLocationGenerator;
import t.me.p1azmer.plugin.dungeons.generator.SchematicPlacementTask;
import t.me.p1azmer.plugin.dungeons.key.Key;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.menu.DungeonGUI;
import t.me.p1azmer.plugin.dungeons.utils.RegionUtil;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static t.me.p1azmer.plugin.dungeons.dungeon.DungeonState.*;

public class Dungeon extends AbstractConfigHolder<DungeonPlugin> implements ICleanable, IPlaceholder {

    private String name;
    private DungeonState state;
    private Set<String> keyIds;

    private final PlaceholderMap placeholderMap;

    private LinkedHashMap<String, DungeonReward> rewardMap;

    private DungeonMainEditor editor;

    // Timer
    private long waitTime; // ожидание в системе
    private long openTime; // ожидание открытия
    private long closeTime; // ожидание закрытия
    private long refreshTime;

    private OpenType openType;
    private World world;
    @Nullable
    private Location location;

    private List<File> schematicFile;
    private List<String> schematics;
    private boolean schematicRandom;

    private boolean effectsEnabled;

    private List<PotionEffect> potionEffects;

    private int minimalOnline;

    private Material chestMaterial;

    private Hologram hologram;

    // private cache
    private boolean spawn = false;
    private Block block = null;
    private DungeonGUI cachedMenu = null;
    private long idleTime = 0; // время жизни, если никто не нажал
    private boolean forceChange = false;
    private BossBar bossBar;
    private Collection<Player> openCache = new HashSet<>();
    private boolean caseIsOpen = false;
//        bossBar.addPlayer(player);

    // messages
    private List<String> waitMessage;
    private List<String> openMessage;
    private List<String> closeMessage;

    public Dungeon(@NotNull DungeonPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.setKeyIds(new HashSet<>());
        this.setSchematics(new ArrayList<>());
        this.setRewardsMap(new LinkedHashMap<>());
        this.setPotionEffects(new ArrayList<>());
        this.setOpenType(OpenType.CLICK);

        setState(FREEZE);
        setWaitTime(0);
        setOpenTime(0);
        setCloseTime(0);
        setRefreshTime(300);

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_NAME, this::getName)
                .add(Placeholders.DUNGEON_WAIT_IN, String.valueOf(this.getWaitTime()))
                .add(Placeholders.DUNGEON_OPEN_IN, String.valueOf(this.getOpenTime()))
                .add(Placeholders.DUNGEON_CLOSE_IN, String.valueOf(this.getCloseTime()))
                .add(Placeholders.DUNGEON_ID, this.getId())
                .add(Placeholders.DUNGEON_SCHEMATICS, String.join(", ", this.getSchematics()))
                .add(Placeholders.DUNGEON_OPEN_TYPE, getOpenType().name())
                .add(Placeholders.DUNGEON_KEY_IDS, () -> String.join(", ", this.getKeyIds()))
                .add(Placeholders.DUNGEON_KEY_NAMES, () -> this.getKeyIds().stream().map(f -> Objects.requireNonNull(plugin.getKeyManager().getKeyById(f)).getName()).collect(Collectors.joining(", ")))
                //.add(Placeholders.CRATE_BLOCK_HOLOGRAM_OFFSET_Y, () -> NumberUtil.format(this.getBlockHologramOffsetY()))
                .add(Placeholders.DUNGEON_HOLOGRAM_TEXT_OPEN, () -> String.join("\n", this.openMessage))
                .add(Placeholders.DUNGEON_HOLOGRAM_TEXT_CLOSE, () -> String.join("\n", this.closeMessage))
                .add(Placeholders.DUNGEON_HOLOGRAM_TEXT_WAIT, () -> String.join("\n", this.waitMessage))
        ;
    }

    @Override
    public boolean load() {
        this.schematicRandom = cfg.getBoolean("Schematic.Random");
        this.schematics = cfg.getStringList("Schematic.List");
        if (this.schematics.isEmpty()) {
            throw new IllegalArgumentException("Schematics not found on config dungeons: " + getId());
        }
        this.setSchematics(this.schematics);

        // check schematic folder
        File schematicsFolder = new File(plugin.getDataFolder() + Config.DIR_SCHEMATICS);
        if (!schematicsFolder.exists())
            schematicsFolder.mkdirs();

        // Проверяем, что файл схематики существует
        for (File schematicFile : this.getSchematicFiles()) {
            if (schematicFile != null && !schematicFile.exists()) {
                plugin.warn("Schematic file not found at path: " + schematicFile.getPath());

                // Загружаем файл схематики из ресурсов плагина
                InputStream resourceStream = plugin.getResource(schematicsFolder.toPath() + "/" + schematicFile.getName());
                if (resourceStream == null) {
                    plugin.error("Schematic file not found on plugin: " + schematicFile.getName());
                    continue;
                }

                try {
                    // Создаем новый файл и копируем содержимое из InputStream
                    try (OutputStream outputStream = new FileOutputStream(schematicFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = resourceStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                    plugin.warn("Schematic loaded!");
                } catch (IOException e) {
                    plugin.warn("An error occurred when copying the schematic file: " + e.getMessage());
                }
            }
        }

        this.setChestMaterial(StringUtil.getEnum(cfg.getString("Chest.Material", null), Material.class).orElse(null));
        if (this.getChestMaterial() == null || this.getChestMaterial().equals(Material.AIR)) {
            throw new IllegalArgumentException("Material of chest is null or air in dungeon: " + getId());
        }

        String worldName = this.cfg.getString("World", "world");
        this.world = plugin.getServer().getWorld(worldName);
        if (this.world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' not found in server!");
        }

        this.setKeyIds(cfg.getStringSet("Key.Ids"));

        this.setName(cfg.getString("Name", getId()));
        for (String rewId : cfg.getSection("Rewards.List")) {
            String path = "Rewards.List." + rewId + ".";

            String rewName = cfg.getString(path + "Name", rewId);
            double rewChance = cfg.getDouble(path + "Chance");

//            boolean rBroadcast = cfg.getBoolean(path + "Broadcast");
            ItemStack rewPreview = cfg.getItem(path + "Item", new ItemStack(Material.BARRIER));

            int maxAmount = cfg.getInt(path + "Limits.Max", 3);
            int minAmount = cfg.getInt(path + "Limits.Min", 1);

            DungeonReward reward = new DungeonReward(this, rewId, rewName, rewChance,
//                    rBroadcast,
                    minAmount, maxAmount,
                    rewPreview);
            this.rewardMap.put(rewId, reward);
        }
        this.setOpenType(cfg.getEnum("OpenType", OpenType.class, OpenType.TIMER));
        this.setState(FREEZE);
        this.setWaitTime(cfg.getLong("Timer.Wait"));
        this.setOpenTime(cfg.getLong("Timer.Open"));
        this.setCloseTime(cfg.getLong("Timer.Close"));
        this.setRefreshTime(cfg.getLong("Timer.Refresh"));
        this.setCloseMessage(cfg.getStringList("Messages.Close"));
        this.setOpenMessage(cfg.getStringList("Messages.Open"));
        this.setWaitMessage(cfg.getStringList("Messages.Wait"));

        this.setEffectsEnabled(cfg.getBoolean("Effects.Enable"));

        for (String effectName : cfg.getSection("Effects.List")) {
            String path = "Effects.List." + effectName + ".";
            PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
            if (potionEffectType == null) {
                plugin.error("Dungeon '" + getId() + "' has error when create the PotionEffect! Potion with name " + effectName + " not found!");
                continue;
            }
            int duration = cfg.getInt(path + "Duration", 25);
            int amplifier = cfg.getInt(path + "Amplifier", 1);
            PotionEffect potionEffect = potionEffectType.createEffect(duration, amplifier);
            this.potionEffects.add(potionEffect);
        }

        if (getWaitMessage().isEmpty()) setWaitMessage(Arrays.asList("Wait for unlock", "Click for open"));
        if (getOpenMessage().isEmpty())
            setCloseMessage(Arrays.asList("Dungeon is open", "Close in: " + Placeholders.DUNGEON_CLOSE_IN));
        if (getCloseMessage().isEmpty())
            setOpenMessage(Arrays.asList("Dungeon is close", "Open in: " + Placeholders.DUNGEON_OPEN_IN));

        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", getName());
        cfg.set("Rewards.List", null);
        for (Map.Entry<String, DungeonReward> e : this.getRewardsMap().entrySet()) {
            DungeonReward reward = e.getValue();
            String path = "Rewards.List." + e.getKey() + ".";

            cfg.setItem(path + "Item", reward.getItem());
//            cfg.set(path + "Broadcast", reward.isBroadcast());
            cfg.set(path + "Chance", reward.getChance());
            cfg.set(path + "Max_Amount", reward.getMaxAmount());
            cfg.set(path + "Min_Amount", reward.getMinAmount());
        }

        cfg.set("Key.Ids", this.getKeyIds());
        cfg.set("OpenType", getOpenType().name());

        cfg.set("Timer.Wait", getWaitTime());
        cfg.set("Timer.Open", getOpenTime());
        cfg.set("Timer.Close", getCloseTime());
        cfg.set("Timer.Refresh", getRefreshTime());

        cfg.set("Messages.Close", getCloseMessage());
        cfg.set("Messages.Open", getOpenMessage());
        cfg.set("Messages.Wait", getWaitMessage());

        cfg.set("Schematic.Random", isSchematicRandom());
        cfg.set("Schematic.List", getSchematics());

        cfg.set("Effects.Enable", isEffectsEnabled());
        cfg.set("Effects.List", null);
        for (PotionEffect effectType : this.getPotionEffects()) {
            String path = "Effects.List." + effectType.getType().getName().toUpperCase(Locale.ENGLISH) + ".";
            cfg.set(path + "Duration", effectType.getDuration());
            cfg.set(path + "Amplifier", effectType.getAmplifier());
        }
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.rewardMap != null) {
            this.rewardMap.values().forEach(DungeonReward::clear);
            this.rewardMap.clear();
            this.rewardMap = null;
        }
        if (this.cachedMenu != null) {
            this.cachedMenu.clear();
            this.cachedMenu = null;
        }
        this.cancel(true);
    }

    public void call(@NotNull DungeonState state) {
        switch (state) {
            case WAITING:
                // Действия для состояния WAITING
                break;
            case PREPARE:
                this.spawn();
                break;
            case OPEN:
                this.open();
                break;
            case CLOSED:
            case CANCEL:
                this.cancel(false);
                break;
        }
        this.setState(state);
    }

    public void spawn() {
        this.spawn(RandomLocationGenerator.getRandomLocation(this.getWorld()), false);
    }

    public void spawn(boolean force) {
        this.spawn(RandomLocationGenerator.getRandomLocation(this.getWorld()), force);
    }

    public void spawn(Location location, boolean force) {
        if (isSpawn()) return;

        File schematic = this.schematicRandom ? Rnd.get(this.getSchematicFiles()) : this.getSchematicFiles().get(Rnd.nextInt(this.getSchematicFiles().size()));

        this.setLocation(location);
        if (this.getLocation() == null) {
            this.call(CANCEL);
            return;
        }
        if (schematic != null) {
            SchematicPlacementTask placementTask = new SchematicPlacementTask(getLocation(), schematic, 3);
            placementTask.runTask(this.plugin);
        } else {
            this.call(CANCEL);
            return;
        }
        plugin.runTaskLater(task -> {

            setBlock(ChestFinder.findNearestChest(getLocation(), getChestMaterial()));
            if (getBlock() == null) {
                this.call(CANCEL);
                return;
            }
            getBlock().setMetadata(getId(), new FixedMetadataValue(plugin, this));

            ChestFiller.fillChest(this, getBlock(), getRewards());

            plugin.getServer().getOnlinePlayers().forEach(all -> plugin.getMessage(Lang.DUNGEON_SPAWN_NOTIFY)
                    .replace(this.replacePlaceholders())
                    .replace(Placeholders.LOCATION.replacer(getLocation()))
                    .send(all));

            getBlock().setMetadata(Keys.CHEST_BLOCK.getKey(), new FixedMetadataValue(plugin, "locked"));
            this.updateHologram(0);
            setSpawn(true);
            RegionUtil.createRegion(getLocation().clone().add(15, 15, -15), getLocation().clone().add(-15, -15, 15), "Dungeons_" + getId() + "_" + plugin.getName());
            setForceChange(force);
        }, 25);
    }

    public void open() {
        if (!this.isSpawn() || getBlock() == null) {
            this.call(DungeonState.PREPARE);
            return;
        }
        getBlock().setMetadata(Keys.CHEST_BLOCK.getKey(), new FixedMetadataValue(plugin, "unlocked"));

        this.updateHologram(0);
        // open logic
    }

    public void updateHologram(int time) {
        if (getBlock() == null) {
            if (this.hologram != null) {
                this.hologram.delete();
                this.hologram = null;
            }
            return;
        }
        if (this.hologram == null) {
            this.hologram = plugin.getHologramAPI().createHologram(getBlock().getLocation().clone().add(0.5, 1.5, 0.5));
            this.hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
        }
        switch (this.getState()) {
            case PREPARE:
                updateHologramLines(time, getOpenType().isClick() ? this.getWaitMessage() : this.getOpenMessage());
                break;
            case OPEN:
                updateHologramLines(time, this.getCloseMessage());
                break;
        }
    }

    private void updateHologramLines(int time, List<String> message) {
        HologramLines lines = this.hologram.getLines();
        int lineCount = Math.min(lines.size(), message.size());
        for (int i = 0; i < lineCount; i++) {
            if (lines.get(i) instanceof TextHologramLine) {
                TextHologramLine line = (TextHologramLine) lines.get(i);
                String originalText = line.getText();
                String newText = message.get(i);
                newText = this.replacePlaceholders(time).apply(newText);
                newText = this.replacePlaceholders().apply(newText);

                if (originalText == null || originalText.isEmpty() || !originalText.equals(newText)) {
                    line.setText(Colorizer.apply(newText));
                }
            }
        }

        if (message.size() < lineCount) {
            for (int i = lineCount - 1; i >= message.size(); i--) {
                HologramLine line = lines.get(i);
                this.hologram.getLines().remove(line);
            }
        } else if (message.size() > lineCount) {
            for (int i = lineCount; i < message.size(); i++) {
                String newText = message.get(i);
                newText = this.replacePlaceholders(time).apply(newText);
                newText = this.replacePlaceholders().apply(newText);
                this.hologram.getLines().appendText(Colorizer.apply(newText));
            }
        }
    }

    public void cancel(boolean shutdown) {
        if (this.hologram != null) {
            this.hologram.delete();
            this.hologram = null;
        }
        if (getLocation() != null) {
            if (shutdown) {
                RegionUtil.removeRegion("Dungeons_" + getId() + "_" + plugin.getName(), getLocation().getWorld());
                SchematicPlacementTask.restore(this.getLocation());
                setLocation(null);
                if (getBlock() != null) {
                    getBlock().removeMetadata(Keys.CHEST_BLOCK.getKey(), plugin);
                    setBlock(null);
                }
            } else {
                plugin.runTask(task -> {
                    RegionUtil.removeRegion("Dungeons_" + getId() + "_" + plugin.getName(), getLocation().getWorld());
                    SchematicPlacementTask.restore(this.getLocation());
                    setLocation(null);
                    if (getBlock() != null) {
                        getBlock().removeMetadata(Keys.CHEST_BLOCK.getKey(), plugin);
                        setBlock(null);
                    }
                });
            }
        }
        if (this.cachedMenu != null) {
            this.cachedMenu.clear();
            this.cachedMenu = null;
        }
        if (this.openCache != null) {
            this.openCache.clear();
            this.openCache = null;
        }
        setSpawn(false);
        this.caseIsOpen = false;

        if (!shutdown) {
            plugin.runTaskLater(task -> {
                this.idleTime = 0;
                setState(FREEZE);
                this.openCache = new HashSet<>();
            }, 25);
        }
    }

    public void tick(int time) {
        if (!getState().isFreeze()) {
            this.idleTime++;
        }
        if (this.getState().isPrepare() && this.idleTime >= Config.IDLE_BREAK.get()) {
            this.call(CANCEL);
            return;
        }
        if (this.bossBar == null) {
            this.bossBar = Bukkit.createBossBar(Colorizer.apply(Config.BOSSBAR_TITLE.get()), Config.BOSSBAR_COLOR.get(), Config.BOSSBAR_STYLE.get());
        }

        switch (this.getState()) {
            case WAITING:
                if (getWaitTime() == time) {
                    plugin.getServer().getOnlinePlayers().forEach(all ->
                            plugin.getMessage(Lang.NOTIFY_SINGLE)
                                    .replace(this.replacePlaceholders(time))
                                    .replace(this.replacePlaceholders())
                                    .send(all));
                } else {
                    plugin.getServer().getOnlinePlayers().forEach(all -> {
                        plugin.getMessage(Lang.NOTIFY_EVERY)
                                .replace(this.replacePlaceholders(time))
                                .replace(this.replacePlaceholders())
                                .send(all);
                        if (!this.bossBar.getPlayers().contains(all))
                            this.bossBar.addPlayer(all);
                        String text = Config.BOSSBAR_TITLE.get();
                        text = replacePlaceholders(time).apply(text);
                        this.bossBar.setTitle(text);
                        double progress = (double) (getWaitTime() - time) / getWaitTime();
                        progress = Math.max(0.0, Math.min(progress, 1.0));
                        this.bossBar.setProgress(progress);
                    });
                }
                break;
            case PREPARE:
            case OPEN:
                this.bossBar.removeAll();
                this.updateHologram(time);
                break;
        }
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

    public void setKeyIds(@NotNull Set<String> keyIds) {
        this.keyIds = new HashSet<>(keyIds.stream().filter(f->!f.isEmpty()).map(String::toLowerCase).collect(Collectors.toList()));
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public World getWorld() {
        return world;
    }

    public List<String> getSchematics() {
        return schematics;
    }

    public boolean isSpawn() {
        return spawn;
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public DungeonState getState() {
        return state;
    }

    public boolean isEffectsEnabled() {
        return effectsEnabled;
    }

    public void setEffectsEnabled(boolean effectsEnabled) {
        this.effectsEnabled = effectsEnabled;
    }

    public boolean isForceChange() {
        return forceChange;
    }

    public void setForceChange(boolean forceChange) {
        this.forceChange = forceChange;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public long getOpenTime() {
        return openTime;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public List<String> getCloseMessage() {
        return closeMessage;
    }

    public void setCloseMessage(List<String> closeMessage) {
        this.closeMessage = closeMessage;
    }

    public void addCloseMessage(String text) {
        this.getCloseMessage().add(text);
    }

    public List<String> getOpenMessage() {
        return openMessage;
    }

    public void setOpenMessage(List<String> openMessage) {
        this.openMessage = openMessage;
    }

    public void addOpenMessage(String text) {
        this.getOpenMessage().add(text);
    }

    public List<String> getWaitMessage() {
        return waitMessage;
    }

    public void setWaitMessage(List<String> waitMessage) {
        this.waitMessage = waitMessage;
    }

    public void addWaitMessage(String text) {
        this.getWaitMessage().add(text);
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public void addPotionEffect(PotionEffect potionEffectType) {
        this.getPotionEffects().add(potionEffectType);
    }

    public Material getChestMaterial() {
        return chestMaterial;
    }

    public void setChestMaterial(Material chestMaterial) {
        this.chestMaterial = chestMaterial;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public void setState(DungeonState state) {
        this.state = state;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    @NotNull
    public List<File> getSchematicFiles() {
        return schematicFile;
    }

    public void setSchematicFiles(List<File> schematicFile) {
        this.schematicFile = schematicFile;
    }

    public void setSchematics(List<String> schematics) {
        this.schematics = schematics;
        if (!this.schematics.isEmpty()) {
            this.setSchematicFiles(schematics.stream().map(f -> {
                if (!f.endsWith(".schem"))
                    f = f + ".schem";
                return new File(plugin.getDataFolder() + Config.DIR_SCHEMATICS + f);
            }).collect(Collectors.toList()));
        }
    }

    public void addSchematic(String schematic) {
        this.getSchematics().add(schematic);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return placeholderMap;
    }

    public @NotNull UnaryOperator<String> replacePlaceholders(int time) {
        return s -> s
                .replace(Placeholders.DUNGEON_WAIT_IN, String.valueOf(getWaitTime() - time))
                .replace(Placeholders.DUNGEON_OPEN_IN, String.valueOf(getOpenTime() - time))
                .replace(Placeholders.DUNGEON_CLOSE_IN, String.valueOf(getCloseTime() - time))

                ;
    }

    @NotNull
    public LinkedHashMap<String, DungeonReward> getRewardsMap() {
        return this.rewardMap;
    }

    public void setRewardsMap(@NotNull LinkedHashMap<String, DungeonReward> rewards) {
        this.rewardMap = rewards;
    }

    @NotNull
    public Collection<DungeonReward> getRewards() {
        return this.getRewardsMap().values();
    }

    @NotNull
    public List<DungeonReward> getRewards(@NotNull Player player) {
        return this.getRewards().stream().collect(Collectors.toList());
    }

    public void setRewards(@NotNull List<DungeonReward> rewards) {
        this.setRewardsMap(rewards.stream().collect(
                Collectors.toMap(DungeonReward::getId, Function.identity(), (has, add) -> add, LinkedHashMap::new)));
    }

    @Nullable
    public DungeonReward getReward(@NotNull String id) {
        return this.getRewardsMap().get(id.toLowerCase());
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

    public OpenType getOpenType() {
        return openType;
    }

    public void setOpenType(OpenType openType) {
        this.openType = openType;
    }

    public boolean isSchematicRandom() {
        return schematicRandom;
    }

    public void setSchematicRandom(boolean schematicRandom) {
        this.schematicRandom = schematicRandom;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setupMenu(List<DungeonReward> rewards) {
        if (this.cachedMenu == null) {
            this.cachedMenu = new DungeonGUI(this, rewards);
        }
    }

    @Nullable
    public DungeonGUI getCachedMenu() {
        return this.cachedMenu;
    }

    @Nullable
    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void open(Player player) {
        Key key = this.plugin.getKeyManager().getKeys(player, this).stream().findFirst().orElse(null);
        if (!this.getKeyIds().isEmpty()) {
            if (key == null) {
                plugin.getMessage(Lang.DUNGEON_OPEN_ERROR_NO_KEY).replace(this.replacePlaceholders()).send(player);
                return;
            }
            if (Config.DUNGEON_HOLD_KEY_TO_OPEN.get()) {
                ItemStack main = player.getInventory().getItemInMainHand();
                if (!this.plugin.getKeyManager().isKey(main, key)) {
                    plugin.getMessage(Lang.DUNGEON_OPEN_ERROR_NO_HOLD_KEY).replace(this.replacePlaceholders()).send(player);
                    return;
                }
            }
        }
        switch (this.getOpenType()) {
            case TIMER:
                if (!getState().isOpen()) {
                    return;
                }
                if (block.getMetadata(Keys.CHEST_BLOCK.getKey()).get(0).asString().equals("unlocked")) {
                    DungeonGUI menu = this.getCachedMenu();
                    if (menu != null) {
                        if (key != null) {
                            if (this.openCache.add(player)) {
                                this.plugin.getKeyManager().takeKey(player, key, 1);
                            }
                            this.cachedMenu.open(player);
                        } else {
                            this.cachedMenu.open(player);
                        }
                    }
                }
                break;
            case CLICK:
                if (!this.caseIsOpen) {
                    if (key != null) {
                        this.plugin.getKeyManager().takeKey(player, key, 1);
                    }
                    this.caseIsOpen = true;
                    this.call(OPEN);
                }
                if (block.getMetadata(Keys.CHEST_BLOCK.getKey()).get(0).asString().equals("unlocked")) {
                    DungeonGUI menu = this.getCachedMenu();
                    if (menu != null) {
                        this.cachedMenu.open(player);
                    }
                }
                break;
        }
    }

    public enum OpenType {
        CLICK,
        TIMER;

        public boolean isClick() {
            return this.equals(CLICK);
        }
    }
}
