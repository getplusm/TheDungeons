package t.me.p1azmer.plugin.dungeons.key;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyManager extends AbstractManager<DungeonPlugin> {

    private final Map<String, Key> keysMap;

    public KeyManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.keysMap = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.plugin.getConfigManager().extractResources(Config.DIR_KEYS);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_KEYS, true)) {
            Key key = new Key(this.plugin, cfg);
            if (key.load()) {
                this.keysMap.put(key.getId(), key);
            } else this.plugin.error("Key not loaded: '" + cfg.getFile().getName() + "'.");
        }
        this.plugin.info("Loaded " + this.getKeysMap().size() + " dungeon keys.");

        this.addListener(new KeyListener(this));
    }

    @Override
    public void onShutdown() {
        this.getKeys().forEach(Key::clear);
        this.getKeysMap().clear();
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getKeyById(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_KEYS, id + ".yml");
        Key key = new Key(this.plugin, cfg);
        key.setName("&a"+StringUtil.capitalizeFully(id) + " Key");

        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemReplacer.create(item).setDisplayName(key.getName()).writeMeta();

        key.setItem(item);
        key.save();
        key.load();

        this.getKeysMap().put(key.getId(), key);
        return true;
    }

    public boolean delete(@NotNull Key keyId) {
        if (keyId.getFile().delete()) {
            keyId.clear();
            this.getKeysMap().remove(keyId.getId());
            return true;
        }
        return false;
    }

    @NotNull
    public Map<String, Key> getKeysMap() {
        return this.keysMap;
    }

    @NotNull
    public Collection<Key> getKeys() {
        return this.getKeysMap().values();
    }

    @NotNull
    public List<String> getKeyIds() {
        return new ArrayList<>(this.getKeysMap().keySet());
    }

    @Nullable
    public Key getKeyById(@NotNull String id) {
        return this.getKeysMap().get(id.toLowerCase());
    }

    @Nullable
    public Key getKeyByItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.DUNGEON_KEY_ID).orElse(null);
        return id == null ? null : this.getKeyById(id);
    }

    @NotNull
    public Set<Key> getKeys(@NotNull Dungeon dungeon) {
        return dungeon.getKeyIds().stream().map(this::getKeyById).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @NotNull
    public Set<Key> getKeys(@NotNull Player player, @NotNull Dungeon dungeon) {
        return this.getKeys(dungeon).stream().filter(key -> this.getKeysAmount(player, key) > 0).collect(Collectors.toSet());
    }

    @Nullable
    public ItemStack getFirstKeyStack(@NotNull Player player, @NotNull Key keyId) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            Key crateKey2 = this.getKeyByItem(item);
            if (crateKey2 != null && crateKey2.equals(keyId)) {
                return item;
            }
        }
        return null;
    }

    public boolean isKey(@NotNull ItemStack item) {
        return this.getKeyByItem(item) != null;
    }

    public boolean isKey(@NotNull ItemStack item, @NotNull Key other) {
        Key key = this.getKeyByItem(item);
        return key != null && key.getId().equalsIgnoreCase(other.getId());
    }

    public int getKeysAmount(@NotNull Player player, @NotNull Dungeon dungeon) {
        return this.getKeys(player, dungeon).stream().mapToInt(key -> this.getKeysAmount(player, key)).sum();
    }

    public int getKeysAmount(@NotNull Player player, @NotNull Key keyId) {
        return PlayerUtil.countItem(player, itemHas -> {
            Key itemKey = this.getKeyByItem(itemHas);
            return itemKey != null && itemKey.getId().equalsIgnoreCase(keyId.getId());
        });
    }

    public boolean hasKey(@NotNull Player player, @NotNull Dungeon dungeon) {
        return !this.getKeys(player, dungeon).isEmpty();
    }

    public boolean hasKey(@NotNull Player player, @NotNull Key keyId) {
        return this.getKeysAmount(player, keyId) > 0;
    }

    public void setKey(@NotNull Player player, @NotNull Key key, int amount) {

        ItemStack keyItem = key.getItem();
        int has = PlayerUtil.countItem(player, keyItem);
        if (has > amount) {
            PlayerUtil.takeItem(player, keyItem, has - amount);
        } else if (has < amount) {
            PlayerUtil.addItem(player, keyItem, amount - has);
        }
        //return true;
    }

    public void giveKey(@NotNull Player player, @NotNull Key key, int amount) {
        ItemStack keyItem = key.getItem();
        keyItem.setAmount(amount < 0 ? Math.abs(amount) : amount);
        PlayerUtil.addItem(player, keyItem);
    }

    public void takeKey(@NotNull Player player, @NotNull Key key, int amount) {
        Predicate<ItemStack> predicate = itemHas -> {
            Key itemKey = this.getKeyByItem(itemHas);
            return itemKey != null && itemKey.getId().equalsIgnoreCase(key.getId());
        };
        int has = PlayerUtil.countItem(player, predicate);
        if (has < amount) amount = has;

        PlayerUtil.takeItem(player, predicate, amount);
        //return true;
    }
}