package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.impl.ChestModule;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.ChestSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.RewardSettings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

@Getter
public class ChestMenu extends AbstractListener<DungeonPlugin> implements ICleanable {
  private final Dungeon dungeon;
  private final Inventory inventory;
  private Block block;
  private final Set<Reward> cachedRewards;
  private boolean fistOpen;

  public ChestMenu(@NotNull Block block, @NotNull Dungeon dungeon, @NotNull Map<Reward, Double> rewards) {
    super(dungeon.plugin());
    this.dungeon = dungeon;

    this.cachedRewards = new HashSet<>();
    this.fistOpen = false;
    this.block = block;
    int menuSize = dungeon.getChestSettings().isBigMenu() ? 54 : 27;
    this.inventory = Bukkit.createInventory(null, menuSize, Colorizer.apply(dungeon.getName()));

    this.initRewards(rewards, dungeon.getChestSettings(), dungeon.getRewardSettings());
    this.registerListeners();
  }

  public void initRewards(@NotNull Map<Reward, Double> rewards, @NotNull ChestSettings settings, @NotNull RewardSettings rewardSettings) {
    if (rewards.isEmpty()) {
      this.getDungeon().getModuleManager()
          .getModule(ChestModule.class)
          .ifPresent(module -> module.debug("Your reward maps are empty!"));
      return;
    }
    for (int i = 0; i < rewardSettings.getLimit().roll(); i++) {
      Reward reward = Rnd.getByWeight(rewards);
      ItemStack item = reward.getItem();
      item.setAmount(reward.getAmount().roll());
      this.cachedRewards.add(reward);

      if (settings.isRandomSlots()) {
        int slot = Rnd.get(0, this.inventory.getSize() - 1);
        ItemStack currentItem = this.inventory.getItem(slot);
        boolean allowed = currentItem == null || currentItem.getType().isAir();
        if (allowed)
          this.inventory.setItem(slot, item);
        else
          this.inventory.addItem(reward.getItem());
        continue;
      }
      this.inventory.addItem(reward.getItem());
    }
  }

  public void open(@NotNull Block block, @NotNull Player player) {
    Location blockLocation = block.getLocation();
    Location menuBlockLocation = this.block.getLocation();

    if (this.block.equals(block) || blockLocation.equals(menuBlockLocation)) {
      player.openInventory(this.getInventory());

      if (!this.fistOpen) {
        PlaceholderMap dungeonPlaceholders = this.dungeon.getPlaceholders();
        this.cachedRewards.forEach(reward -> reward.getCommands()
                                                   .forEach(command -> {
                                                     UnaryOperator<String> inter = PlaceholderMap.fusion(reward.getPlaceholders(), dungeonPlaceholders).replacer();
                                                     PlayerUtil.dispatchCommand(player, inter.apply(command));
                                                   })
        );
        this.fistOpen = true;
      }
    }
  }

  @Override
  public void clear() {
    this.unregisterListeners();
    this.fistOpen = false;
    this.block = null;
    plugin.runTask(sync -> new HashSet<>(this.inventory.getViewers()).forEach(HumanEntity::closeInventory));

    this.cachedRewards.clear();
    this.inventory.clear();
  }
}
