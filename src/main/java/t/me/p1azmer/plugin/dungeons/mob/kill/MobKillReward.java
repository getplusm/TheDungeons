package t.me.p1azmer.plugin.dungeons.mob.kill;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.random.Rnd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MobKillReward {

    String mobId;
    List<String> commands;
    Map<String, Pair<ItemStack, Double>> itemsMap;

    @NotNull
    public static MobKillReward read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        Map<String, Pair<ItemStack, Double>> items = new HashMap<>();
        for (String itemId : cfg.getSection(path + ".Items")) {
            ItemStack item = cfg.getItem(path + ".Items." + itemId + ".Item");
            double chance = cfg.getDouble(path + ".Items." + itemId + ".Chance");
            items.put(itemId, Pair.of(item, chance));
        }
        List<String> commands = cfg.getStringList(path + ".Commands");
        return new MobKillReward(id, commands, items);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.getItemsMap().forEach((id, pair) -> {
            cfg.setItem(path + ".Items." + id + ".Item", pair.getFirst());
            cfg.set(path + ".Items." + id + ".Chance", pair.getSecond());
        });
        cfg.set(path + ".Commands", this.getCommands());
    }

    public void reward(@NotNull Player player) {
        this.getItemsMap().values().forEach(pair -> {
            if (Rnd.chance(pair.getSecond())) {
                PlayerUtil.addItem(player, pair.getFirst());
            }
        });
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));
    }
}
