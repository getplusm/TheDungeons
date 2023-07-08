package t.me.p1azmer.engine.api.hook;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.manager.AbstractListener;

@Deprecated
public abstract class AbstractHook<P extends NexPlugin<P>> extends AbstractListener<P> {

    private final String pluginName;

    public AbstractHook(@NotNull P plugin, @NotNull String pluginName) {
        super(plugin);
        this.pluginName = pluginName;
    }

    @NotNull
    public final String getPluginName() {
        return this.pluginName;
    }

    public abstract boolean setup();

    public abstract void shutdown();
}
