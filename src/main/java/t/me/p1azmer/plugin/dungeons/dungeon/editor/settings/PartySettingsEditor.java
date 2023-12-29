package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.PartySettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class PartySettingsEditor extends EditorMenu<DungeonPlugin, PartySettings> {

    public PartySettingsEditor(@NotNull PartySettings settings) {
        super(settings.dungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 27);
        Dungeon dungeon = settings.dungeon();

        this.addReturn(22).setClick((viewer, event) -> {
            dungeon.getEditor().openNextTick(viewer.getPlayer(), 1);
        });


        this.addItem(settings.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, EditorLocales.PARTY_ENABLED, 0).setClick((viewer, event) -> {
            settings.setEnabled(!settings.isEnabled());
            this.save(viewer);
        });

        this.addItem(Material.COMPARATOR, EditorLocales.PARTY_SIZE, 8).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_VALUE, wrapper -> {
                int size = wrapper.asInt();
                if (size <= 0) {
                    EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.ERROR_NUMBER_INVALID).replace("%num%", size).getLocalized());
                    return false;
                }
                settings.setSize(size);
                dungeon.save();
                return true;
            });
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    if (item.getType().equals(Material.EMERALD_BLOCK) && !settings.isEnabled())
                        item.setType(Material.REDSTONE_BLOCK);
                    else if (item.getType().equals(Material.REDSTONE_BLOCK) && settings.isEnabled())
                        item.setType(Material.EMERALD_BLOCK);

                    ItemReplacer.replace(item, settings.replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
        this.openNextTick(viewer.getPlayer(), viewer.getPage());
    }
}