package t.me.p1azmer.plugin.dungeons.generator;

import org.bukkit.block.Block;
import t.me.p1azmer.engine.utils.random.Rnd;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonReward;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChestFiller {

    public static void fillChest(Dungeon dungeon, Block chestBlock, Collection<DungeonReward> dungeonRewards) {
        List<DungeonReward> rewards = new ArrayList<>();
        for (DungeonReward dungeonReward : dungeonRewards) {
            if (Rnd.chance(dungeonReward.getChance())) {
//                int minAmount = dungeonReward.getMinAmount();
//                int maxAmount = dungeonReward.getMaxAmount();
//                ItemStack item = dungeonReward.getItem();
//                item.setAmount(Rnd.get(minAmount, maxAmount));
                rewards.add(dungeonReward);
            }
        }
        dungeon.setupMenu(rewards);
    }
}