package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.api.handler.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.state.ChestState;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.ChestSettings;
import t.me.p1azmer.plugin.dungeons.key.Key;
import t.me.p1azmer.plugin.dungeons.key.KeyManager;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Collection;
import java.util.HashSet;

import static t.me.p1azmer.plugin.dungeons.dungeon.chest.state.ChestState.*;

@Getter
@Setter
public class ChestBlock implements ICleanable, Placeholder {

  private final Dungeon dungeon;
  private final Block block;
  private final Location location;
  private ChestMenu menu;
  private ChestState state;
  private final PlaceholderMap placeholderMap;

  // cache
  private int currentTick;
  private final Collection<Player> openedCache = new HashSet<>();

  public ChestBlock(@NotNull Dungeon dungeon, @NotNull Block block, @NotNull Location location, @NotNull ChestMenu menu) {
    this.dungeon = dungeon;
    this.block = block;
    this.location = location;
    this.menu = menu;
    this.state = WAITING;

    this.placeholderMap = new PlaceholderMap()
      .add(Placeholders.DUNGEON_CHEST_NEXT_STATE_IN, () -> TimeUtil.formatTimeLeft(System.currentTimeMillis() + this.getNextStateTime() * 1000L))
    ;
  }

  public int getNextStateTime() {
    ChestSettings chestSettings = this.getDungeon().getChestSettings();
    return chestSettings.getTime(this.getState()) - this.getCurrentTick();
  }

  public void setCurrentTick(int tick) {
    this.currentTick = Math.max(0, tick);
  }

  public boolean isOpenFor(@NotNull Player player) {
    ChestSettings chestSettings = this.getDungeon().getChestSettings();
    return chestSettings.isUseOneKeyForMenu() ? this.getState().isOpen() : this.openedCache.contains(player);
  }

  public void setState(@NotNull ChestState state) {
    this.setCurrentTick(0);
    this.state = state;
  }

  public void click(@NotNull Player player) {
    Dungeon dungeon = this.getDungeon();
    DungeonPlugin plugin = dungeon.plugin();
    KeyManager keyManager = plugin.getKeyManager();
    ChestSettings chestSettings = dungeon.getChestSettings();
    ChestModule.OpenType openType = chestSettings.getOpenType();

    if (openType.isClick() && this.getState().isWaiting()) {
      this.setState(dungeon.getSettings().isClickTimer() ? COOLDOWN : OPENED);
      return;
    }

    if (!this.getState().isOpen()) return;

    Key key = keyManager.getKeys(player, dungeon).stream().findFirst().orElse(null);

    boolean allowed = !isOpenFor(player) && !dungeon.getKeyIds().isEmpty();
    if (allowed) {
      if (key == null) {
        plugin.getMessage(Lang.DUNGEON_OPEN_ERROR_NO_KEY)
              .replace(dungeon.replacePlaceholders())
              .send(player);
        return;
      }
      if (Config.DUNGEON_HOLD_KEY_TO_OPEN.get()) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (!keyManager.isKey(main, key)) {
          plugin.getMessage(Lang.DUNGEON_OPEN_ERROR_NO_HOLD_KEY)
                .replace(dungeon.replacePlaceholders())
                .send(player);
          return;
        }
      }

      keyManager.takeKey(player, key, 1);
      if (chestSettings.isUseOneKeyForMenu()) this.setState(OPENED);
      else this.openedCache.add(player);

    } else if (!this.getState().isOpen()) this.setState(OPENED);

    if (block.hasMetadata(Keys.DUNGEON_CHEST_BLOCK.getKey())) this.getMenu().open(block, player);
  }

  public void tick(@NotNull DungeonPlugin plugin, int tick) {
    this.setCurrentTick(tick);
    HologramHandler handler = plugin.getHologramHandler();
    if (handler != null) handler.update(this);

    if (Config.TICK_CHEST_DEBUG.get()) {
      this.getDungeon().plugin().sendDebug("Tick the '" + this.getDungeon().getId() + "' dungeon chest with state = " + this.getState().name() + ", time=" + this.getCurrentTick() + ", coordinate=" + Placeholders.forLocation(this.getLocation()).apply("%location_x%, %location_z%") + ", next state in=" + this.getNextStateTime());
    }
  }

  @Override
  public void clear() {
    if (!this.getState().isDeleted()) this.setState(DELETED);

    this.setCurrentTick(0);
    if (this.menu != null) {
      this.menu.clear();
      this.menu = null;
    }
    this.openedCache.clear();
  }

  @Override
  public @NotNull PlaceholderMap getPlaceholders() {
    return this.placeholderMap;
  }
}
