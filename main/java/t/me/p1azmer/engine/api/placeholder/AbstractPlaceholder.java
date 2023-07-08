package t.me.p1azmer.engine.api.placeholder;

import org.bukkit.OfflinePlayer;
import t.me.p1azmer.engine.NexPlugin;

import java.util.regex.Matcher;

public abstract class AbstractPlaceholder extends Placeholder {

    public AbstractPlaceholder(NexPlugin<?> plugin) {
        super(plugin);
    }

    public abstract String parse(Matcher matcher, OfflinePlayer p);
}
