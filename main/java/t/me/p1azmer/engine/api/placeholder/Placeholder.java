package t.me.p1azmer.engine.api.placeholder;

import t.me.p1azmer.engine.NexPlugin;

import java.util.regex.Pattern;

public abstract class Placeholder {

    protected NexPlugin<?> plugin;

    private Pattern pattern;

    public Placeholder(NexPlugin<?> plugin) {
        this.plugin = plugin;
    }

    public abstract String getRegex();

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(getRegex());
        }
        return pattern;
    }
}
