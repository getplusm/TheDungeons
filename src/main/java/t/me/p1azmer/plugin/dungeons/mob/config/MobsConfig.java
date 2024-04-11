package t.me.p1azmer.plugin.dungeons.mob.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.plugin.dungeons.mob.Placeholders;
import t.me.p1azmer.plugin.dungeons.mob.kill.MobKillReward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static t.me.p1azmer.engine.utils.Colors.LIGHT_YELLOW;


public class MobsConfig {
    public static final JOption<String> NAME_FORMAT = JOption.create(
                    "Mobs.DisplayName_Format",
                    LIGHT_YELLOW + Placeholders.MOB_NAME,
                    "Sets entity display name format for internal AMA mobs.",
                    "Placeholders: " + Placeholders.MOB_NAME
            )
            .mapReader(Colorizer::apply);

    public static final JOption<Boolean> KILL_REWARD_ENABLED = JOption.create(
            "Mobs.Kill_Rewards.Enabled",
            true,
            "Enables/Disables the Mob Kill Rewards feature."
    );

    public static final JOption<Map<String, MobKillReward>> KILL_REWARD_VALUES = JOption.forMap(
                    "Mobs.Kill_Rewards.Table",
                    (cfg, path, key) -> MobKillReward.read(cfg, path + "." + key, key),
                    () -> {
                        Map<String, MobKillReward> map = new HashMap<>();
                        Map<String, Pair<ItemStack, Double>> items = new HashMap<>();
                        ItemStack defaultItem = new ItemStack(Material.GOLD_NUGGET);
                        ItemReplacer.create(defaultItem)
                                .setDisplayName("&6Default item")
                                .replace(Colorizer::apply)
                                .writeMeta();
                        items.put("default", Pair.of(defaultItem, 50D));

                        map.put(Placeholders.DEFAULT, new MobKillReward(Placeholders.DEFAULT, new ArrayList<>(), items));
                        map.put("default_skeleton", new MobKillReward("default_skeleton", new ArrayList<>(), items));
                        return map;
                    },
                    "Here you can create custom rewards for mob kills on the dungeon.",
                    "For commands, use '" + Placeholders.PLAYER_NAME + "' placehodler for a player name."
            )
            .setWriter((cfg, path, map) -> map.forEach((id, reward) -> reward.write(cfg, path + "." + id)));
}