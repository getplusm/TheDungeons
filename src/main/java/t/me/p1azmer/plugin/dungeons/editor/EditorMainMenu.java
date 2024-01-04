package t.me.p1azmer.plugin.dungeons.editor;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.announce.editor.AnnounceListEditor;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonListEditor;
import t.me.p1azmer.plugin.dungeons.key.editor.KeyListEditor;
import t.me.p1azmer.plugin.dungeons.mob.editor.MobListEditor;

public class EditorMainMenu extends EditorMenu<DungeonPlugin, DungeonPlugin> {

    private static final String TEXTURE_DUNGEON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIwYWYxMTIyYzFlODFmYWI2MmY0ZDgyOTZjNThkODE0ZjA1NWQyOTE2OWQxMDEyY2MzNGMwY2Q4NDJlN2YwYiJ9fX0=";
    private static final String TEXTURE_KEY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVlZmE0Y2QyYTU1OGU0YTgxMmUyZWE3NTQxZTYyNzUwYjk2YmExZDgyYzFkYTlmZDVmMmUzZmI5MzA4YzYzNSJ9fX0=";
    private static final String TEXTURE_MOB = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3OTAxZTA1OWRjZTM2ODYxN2FhMDZmMmQ0NmY5ZmFiZThkMjdlOGQ3MWZiYzhlYzA1MTg2Y2JiYWNlMzY1ZCJ9fX0=";

    private static final String TEXTURE_ANNOUNCE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0=";

    private DungeonListEditor dungeonListEditor;
    private KeyListEditor keyListEditor;
    private MobListEditor mobListEditor;
    private AnnounceListEditor announceListEditor;

    public EditorMainMenu(@NotNull DungeonPlugin plugin) {
        super(plugin, plugin, Config.EDITOR_TITLE_DUNGEON.get(), 27);

        this.addExit(22);

        this.addItem(ItemUtil.createCustomHead(TEXTURE_DUNGEON), EditorLocales.DUNGEON_EDITOR, 10)
                .setClick((viewer, event) -> this.plugin.runTask(task -> this.getDungeonEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead(TEXTURE_ANNOUNCE), EditorLocales.ANNOUNCE_EDITOR, 12)
                .setClick((viewer, event) -> this.plugin.runTask(task -> this.getAnnounceEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead(TEXTURE_KEY), EditorLocales.KEYS_EDITOR, 14)
                .setClick((viewer, event) -> this.plugin.runTask(task -> this.getKeysEditor().open(viewer.getPlayer(), 1)));
        this.addItem(ItemUtil.createCustomHead(TEXTURE_MOB), EditorLocales.MOB_EDITOR, 16)
                .setClick((viewer, event) -> this.plugin.runTask(task -> this.getMobEditor().open(viewer.getPlayer(), 1)));
    }

    @Override
    public void clear() {
        if (this.dungeonListEditor != null) {
            this.dungeonListEditor.clear();
            this.dungeonListEditor = null;
        }
        if (this.keyListEditor != null) {
            this.keyListEditor.clear();
            this.keyListEditor = null;
        }
        super.clear();
    }

    @NotNull
    public DungeonListEditor getDungeonEditor() {
        if (this.dungeonListEditor == null) {
            this.dungeonListEditor = new DungeonListEditor(this.plugin.getDungeonManager());
        }
        return this.dungeonListEditor;
    }

    @NotNull
    public KeyListEditor getKeysEditor() {
        if (this.keyListEditor == null) {
            this.keyListEditor = new KeyListEditor(this.plugin.getKeyManager());
        }
        return this.keyListEditor;
    }

    @NotNull
    public MobListEditor getMobEditor() {
        if (this.mobListEditor == null) {
            this.mobListEditor = new MobListEditor(this.plugin.getMobManager());
        }
        return mobListEditor;
    }

    @NotNull
    public AnnounceListEditor getAnnounceEditor() {
        if (this.announceListEditor == null)
            this.announceListEditor = new AnnounceListEditor(this.plugin.getAnnounceManager());
        return this.announceListEditor;
    }
}