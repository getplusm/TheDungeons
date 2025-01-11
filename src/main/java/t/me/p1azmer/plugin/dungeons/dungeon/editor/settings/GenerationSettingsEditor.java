package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.generation.GenerationType;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.GenerationSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;

public class GenerationSettingsEditor extends EditorMenu<DungeonPlugin, GenerationSettings> {

    public GenerationSettingsEditor(@NotNull GenerationSettings settings) {
        super(settings.getDungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 9);
        Dungeon dungeon = settings.getDungeon();

        this.addReturn(8).setClick((viewer, event) -> dungeon.getEditor().openNextTick(viewer.getPlayer(), 1));


        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_GENERATION_TYPE, 2)
                .setClick((viewer, event) -> {
                    GenerationType generationType = CollectionsUtil.next(settings.getGenerationType());
                    settings.setGenerationType(generationType);
                    this.save(viewer);
                }).getOptions()
                .setDisplayModifier((viewer, item) -> {
                    ItemStack replacer = new ItemStack(settings.getGenerationType().getIcon());
                    item.setItemMeta(replacer.getItemMeta());
                    ItemReplacer
                            .create(item)
                            .readLocale(EditorLocales.DUNGEON_SETTINGS_GENERATION_TYPE)
                            .writeMeta();
                });

        this.addItem(new ItemStack(Material.DIRT), EditorLocales.DUNGEON_SETTINGS_GENERATION_LOCATION, 4)
                .setClick((viewer, event) -> {
                    ClickType clickType = event.getClick();

                    if (clickType == ClickType.LEFT) {
                        Player player = viewer.getPlayer();
                        Location location = player.getLocation();
                        settings.setSpawnLocation(location);
                    } else if (clickType == ClickType.SHIFT_RIGHT) {
                        settings.setSpawnLocation(null);
                    }

                    this.save(viewer);
                });

        this.getItems()
                .forEach(menuItem -> menuItem
                        .getOptions()
                        .addDisplayModifier(((viewer, item) -> {
                            ItemReplacer
                                    .replace(item, dungeon.replacePlaceholders());
                            ItemReplacer
                                    .replace(item, settings.replacePlaceholders());
                            ItemReplacer
                                    .replace(item, dungeon.getSettings().replacePlaceholders());
                        })));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getDungeon().save();
        this.openNextTick(viewer.getPlayer(), viewer.getPage());
    }
}