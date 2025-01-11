package t.me.p1azmer.plugin.dungeons.announce;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.announce.editor.AnnounceListEditor;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.config.Config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnounceManager extends AbstractManager<DungeonPlugin> {
    private final Map<String, Announce> announceMap;
    private AnnounceListEditor editor;

    public AnnounceManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.announceMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getConfigManager().extractResources(Config.DIR_ANNOUNCE);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_ANNOUNCE, true)) {
            Announce announce = new Announce(this, cfg);
            if (announce.load()) {
                this.announceMap.put(announce.getId(), announce);
            } else this.plugin.error("Announce not loaded: '" + cfg.getFile().getName() + "'.");
        }
        this.plugin.info("Loaded " + this.getAnnounceMap().size() + " announces.");
    }

    @Override
    protected void onShutdown() {
        this.announceMap.values().forEach(Announce::clear);
        this.announceMap.clear();
    }

    @NotNull
    public Map<String, Announce> getAnnounceMap() {
        return announceMap;
    }

    @NotNull
    public Collection<Announce> getAnnounces() {
        return this.announceMap.values();
    }

    @Nullable
    public Announce getAnnounce(@NotNull String id) {
        return this.getAnnounceMap().get(id);
    }

    @NotNull
    public AnnounceListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new AnnounceListEditor(this);
        }
        return this.editor;
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getAnnounce(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_ANNOUNCE, id + ".yml");
        Announce announce = new Announce(this, cfg);
        announce.save();
        announce.load();

        this.getAnnounceMap().put(announce.getId(), announce);
        return true;
    }

    public void delete(@NotNull Announce announce) {
        if (announce.getFile().delete()) {
            announce.clear();
            this.getAnnounceMap().remove(announce.getId());
        }
    }
}