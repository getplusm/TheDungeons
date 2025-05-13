package t.me.p1azmer.plugin.dungeons.api;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class DungeonsApi {

    protected static DungeonsApi api;

    public static DungeonsApi getApiOrThrow() {
        return getApi().orElseThrow(() -> new RuntimeException("Dungeons API is not yet loaded or not loaded in principle"));
    }

    public static Optional<DungeonsApi> getApi() {
        return Optional.ofNullable(api);
    }
}
