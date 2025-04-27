package t.me.p1azmer.plugin.dungeons.dungeon.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Keys {
    @NonFinal
    Set<String> keyIds = new HashSet<>();

    public void setKeyIds(@NotNull Set<String> keyIds) {
        this.keyIds = keyIds.stream()
                .filter(Predicate.not(String::isEmpty))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
