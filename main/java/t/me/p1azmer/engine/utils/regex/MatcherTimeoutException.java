package t.me.p1azmer.engine.utils.regex;

import org.jetbrains.annotations.NotNull;

public class MatcherTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1453635499261100540L;

    private final String chars;
    private final long   timeout;

    MatcherTimeoutException(@NotNull CharSequence chars, long timeout) {
        this.chars = chars.toString();
        this.timeout = timeout;
    }

    @NotNull
    public String getString() {
        return this.chars;
    }

    public long getTimeout() {
        return this.timeout;
    }
}
