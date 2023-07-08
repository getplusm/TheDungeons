package t.me.p1azmer.engine.api.placeholder;

import java.util.regex.Matcher;

public class CachedPlaceholder {
    private final Matcher matcher;
    private final Placeholder abstractPlaceholder;

    public CachedPlaceholder(Matcher matcher, Placeholder abstractPlaceholder) {
        this.matcher = matcher;
        this.abstractPlaceholder = abstractPlaceholder;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public Placeholder getPlaceholder() {
        return abstractPlaceholder;
    }
}
