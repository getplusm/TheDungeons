package t.me.p1azmer.engine.api.manager;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.config.JYML;

import java.io.File;

@Deprecated
public abstract class AbstractLoadableItem<P extends NexPlugin<P>> implements ConfigHolder {

    protected final P plugin;
    protected final JYML cfg;
    private final String id;

    public AbstractLoadableItem(@NotNull P plugin, @NotNull String path) {
        this(plugin, new JYML(new File(path)));
    }

    public AbstractLoadableItem(@NotNull P plugin, @NotNull JYML cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.id = this.getFile().getName().replace(".yml", "").toLowerCase();
    }

    @NotNull
    public P plugin() {
        return this.plugin;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final JYML getConfig() {
        return this.cfg;
    }
}