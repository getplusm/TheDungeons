package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.key.Key;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Collection;
import java.util.HashSet;

import static t.me.p1azmer.plugin.dungeons.dungeon.chest.DungeonChestState.*;

public class DungeonChestBlock implements ICleanable, IPlaceholderMap {

    private final Dungeon dungeon;
    private final Block block;
    private final Location location;
    private DungeonChestMenu gui;
    private DungeonChestState chestState;
    private final PlaceholderMap placeholderMap;

    // cache
    private boolean playerClicked = false;
    private int currentTick;
    private Collection<Player> openedCache;

    public DungeonChestBlock(@NotNull Dungeon dungeon, @NotNull Block block, @NotNull Location location, @NotNull DungeonChestMenu gui) {
        this.dungeon = dungeon;
        this.block = block;
        this.location = location;
        this.gui = gui;
        this.chestState = WAITING;
        this.openedCache = new HashSet<>();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.DUNGEON_CHEST_NEXT_STATE_IN, () -> String.valueOf(this.getNextStateTime()))
        ;
    }

    @NotNull
    public Dungeon getDungeon() {
        return dungeon;
    }

    @NotNull
    public Block getBlock() {
        return block;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public DungeonChestMenu getGui() {
        return gui;
    }

    public boolean isPlayerClicked() {
        return playerClicked;
    }

    public int getCurrentTick() {
        return this.currentTick;
    }

    public int getNextStateTime() {
        return this.getDungeon().getChestSettings().getTime(this.getState()) - this.getCurrentTick();
    }

    public void setCurrentTick(int tick) {
        this.currentTick = Math.max(0, tick);
    }

    @NotNull
    public DungeonChestState getState() {
        return chestState;
    }

    public boolean isOpenFor(@NotNull Player player) {
        return this.getDungeon().getChestSettings().isUseOneKeyForMenu() ? this.getState().isOpen() : this.openedCache.contains(player);
    }

    public void setChestState(@NotNull DungeonChestState state) {
        this.setCurrentTick(0);
        this.chestState = state;
    }

    public void click(@NotNull Player player) {
        ChestModule.OpenType openType = this.getDungeon().getChestSettings().getOpenType();
        if (openType.isClick() && this.getState().isWaiting()) {
            this.setChestState(this.getDungeon().getSettings().isClickTimer() ? COOLDOWN : OPENED);
            return;
        }
        if (!this.getState().isOpen()) return;

        Key key = this.getDungeon().plugin().getKeyManager().getKeys(player, this.getDungeon()).stream().findFirst().orElse(null);

        if (!isOpenFor(player) && !this.getDungeon().getKeyIds().isEmpty()) {
            if (key == null) {
                this.getDungeon().plugin().getMessage(Lang.DUNGEON_OPEN_ERROR_NO_KEY).replace(this.getDungeon().replacePlaceholders()).send(player);
                return;
            }
            if (Config.DUNGEON_HOLD_KEY_TO_OPEN.get()) {
                ItemStack main = player.getInventory().getItemInMainHand();
                if (!this.getDungeon().plugin().getKeyManager().isKey(main, key)) {
                    this.getDungeon().plugin().getMessage(Lang.DUNGEON_OPEN_ERROR_NO_HOLD_KEY).replace(this.getDungeon().replacePlaceholders()).send(player);
                    return;
                }
            }

            this.getDungeon().plugin().getKeyManager().takeKey(player, key, 1);
            if (dungeon.getChestSettings().isUseOneKeyForMenu())
                this.setChestState(OPENED);
            else
                this.openedCache.add(player);
        } else
            this.setChestState(OPENED);

        if (block.hasMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey())) {
            this.getGui().open(block, player);
        }
    }

    public void tick(@NotNull DungeonPlugin plugin, int tick) {
        this.setCurrentTick(tick);
        HologramHandler handler = plugin.getHologramHandler();
        if (handler != null) {
            handler.update(this);
        }
        if (Config.DEBUG_TICK_CHEST.get()) {
            this.getDungeon().plugin().sendDebug("Tick the '" + this.getDungeon().getId() + "' dungeon chest with state = " + this.getState().name() + ", time=" + this.getCurrentTick() + ", coordinate=" + Placeholders.forLocation(this.getLocation()).apply("%location_x%, %location_z%") + ", next state in=" + this.getNextStateTime());
        }
    }

    @Override
    public void clear() {
        if (!this.getState().isDeleted())
            this.setChestState(DELETED);

        this.setCurrentTick(0);
        if (this.gui != null) {
            this.gui.clear();
            this.gui = null;
        }
        if (this.openedCache != null) {
            this.openedCache.clear();
            this.openedCache = null;
        }
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
