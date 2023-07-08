package t.me.p1azmer.plugin.dungeons.key;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PDCUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.engine.utils.collections.AutoRemovalMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

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
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(key.getName());
        });

        key.setItem(item);
        key.save();
        key.load();

        this.getKeysMap().put(key.getId(), key);
        return true;
    }

    public boolean delete(@NotNull Key crateKey) {
        if (crateKey.getFile().delete()) {
            crateKey.clear();
            this.getKeysMap().remove(crateKey.getId());
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
        String id = PDCUtil.getString(item, Keys.CRATE_KEY_ID).orElse(null);
        return id == null ? null : this.getKeyById(id);
    }

    @NotNull
    public Set<Key> getKeys(@NotNull Dungeon crate) {
        return crate.getKeyIds().stream().map(this::getKeyById).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @NotNull
    public Set<Key> getKeys(@NotNull Player player, @NotNull Dungeon crate) {
        return this.getKeys(crate).stream().filter(key -> this.getKeysAmount(player, key) > 0).collect(Collectors.toSet());
    }

    @Nullable
    public ItemStack getFirstKeyStack(@NotNull Player player, @NotNull Key crateKey) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) continue;

            Key crateKey2 = this.getKeyByItem(item);
            if (crateKey2 != null && crateKey2.equals(crateKey)) {
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

    public int getKeysAmount(@NotNull Player player, @NotNull Dungeon crate) {
        return this.getKeys(player, crate).stream().mapToInt(key -> this.getKeysAmount(player, key)).sum();
    }

    public int getKeysAmount(@NotNull Player player, @NotNull Key crateKey) {
        return PlayerUtil.countItem(player, itemHas -> {
            Key itemKey = this.getKeyByItem(itemHas);
            return itemKey != null && itemKey.getId().equalsIgnoreCase(crateKey.getId());
        });
    }

    public boolean hasKey(@NotNull Player player, @NotNull Dungeon crate) {
        return !this.getKeys(player, crate).isEmpty();
    }

    public boolean hasKey(@NotNull Player player, @NotNull Key crateKey) {
        return this.getKeysAmount(player, crateKey) > 0;
    }

//    public void giveKeysOnHold(@NotNull Player player) {
//        CrateUser user = plugin.getUserManager().getUserData(player);
//        user.getKeysOnHold().forEach((keyId, amount) -> {
//            Key crateKey = this.getKeyById(keyId);
//            if (crateKey == null) return;
//
//            this.giveKey(player, crateKey, amount);
//        });
//        user.cleanKeysOnHold();
//        user.saveData(this.plugin);
//    }

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