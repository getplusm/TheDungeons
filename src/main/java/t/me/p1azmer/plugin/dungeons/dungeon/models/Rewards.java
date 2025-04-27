package t.me.p1azmer.plugin.dungeons.dungeon.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.reward.Reward;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Rewards {
    @NonFinal Map<String, Reward> rewardsMap = new ConcurrentHashMap<>();

    public void addReward(@NotNull String id, @NotNull Reward reward) {
        getRewardsMap().put(id, reward);
    }

    public void setRewards(@NotNull List<Reward> rewards) {
        setRewardsMap(rewards.stream().collect(Collectors.toMap(Reward::getId, Function.identity(), (has, add) -> add, LinkedHashMap::new)));
    }

    public void addReward(@NotNull Reward Reward) {
        getRewardsMap().put(Reward.getId(), Reward);
    }

    public Optional<Reward> getReward(@NotNull String id) {
        return Optional.ofNullable(getRewardsMap().get(id));
    }

    public void removeReward(@NotNull Reward Reward) {
        removeReward(Reward.getId());
    }

    public void removeReward(@NotNull String id) {
        getRewardsMap().remove(id);
    }
}
