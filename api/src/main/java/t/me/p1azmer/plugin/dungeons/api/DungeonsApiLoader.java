package t.me.p1azmer.plugin.dungeons.api;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DungeonsApiLoader extends DungeonsApi { // TODO javadocs

    public DungeonsApiLoader() {
        api = this;
    }
}
