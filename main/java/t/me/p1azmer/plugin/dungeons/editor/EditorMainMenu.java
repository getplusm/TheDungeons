package t.me.p1azmer.plugin.dungeons.editor;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.editor.DungeonListEditor;
import t.me.p1azmer.plugin.dungeons.key.editor.KeyListEditor;

public class EditorMainMenu extends EditorMenu<DungeonPlugin, DungeonPlugin> {

    private static final String TEXTURE_CRATE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRiNTRmM2U2YTcwMTZhMTRlNzZlZWRjNTMwNDBmNDk2MDU4MWJhY2FjODE0ZmZiNTgxNmZhZTFhMTRiMDE4ZCJ9fX0=";
    private static final String TEXTURE_KEY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVlZmE0Y2QyYTU1OGU0YTgxMmUyZWE3NTQxZTYyNzUwYjk2YmExZDgyYzFkYTlmZDVmMmUzZmI5MzA4YzYzNSJ9fX0=";

    private DungeonListEditor dungeonListEditor;
    private KeyListEditor keyListEditor;

    public EditorMainMenu(@NotNull DungeonPlugin plugin) {
        super(plugin, plugin, Config.EDITOR_TITLE_CRATE.get(), 27);

        this.addExit(22);

        this.addItem(ItemUtil.createCustomHead(TEXTURE_CRATE), EditorLocales.DUNGEON_EDITOR, 11)
                .setClick((viewer, event) -> {
                    this.plugin.runTask(task -> this.getCratesEditor().open(viewer.getPlayer(), 1));
                });
        this.addItem(ItemUtil.createCustomHead(TEXTURE_KEY), EditorLocales.KEYS_EDITOR, 15)
                .setClick((viewer, event) -> {
                    this.plugin.runTask(task -> this.getKeysEditor().open(viewer.getPlayer(), 1));
                });
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
    public DungeonListEditor getCratesEditor() {
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
}