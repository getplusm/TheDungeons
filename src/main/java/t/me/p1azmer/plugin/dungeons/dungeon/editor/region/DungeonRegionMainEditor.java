package t.me.p1azmer.plugin.dungeons.dungeon.editor.region;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.region.Region;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class DungeonRegionMainEditor extends EditorMenu<DungeonPlugin, Region> {

    public DungeonRegionMainEditor(@NotNull Region region) {
        super(region.getDungeon().plugin(), region, Config.EDITOR_TITLE_DUNGEON.get(), 27);
        Dungeon dungeon = region.getDungeon();

        this.addReturn(22).setClick((viewer, event) -> this.plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1)));


        this.addItem(Material.NAME_TAG, EditorLocales.REGION_NAME, 2).setClick((viewer, event) -> this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
            region.setName(wrapper.getText());
            dungeon.save();
            return true;
        }));

        this.addItem(region.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, EditorLocales.REGION_ENABLED, 0).setClick((viewer, event) -> {
            region.setEnabled(!region.isEnabled());
            this.save(viewer);
        });

        this.addItem(Material.COMPARATOR, EditorLocales.REGION_RADIUS, 4).setClick((viewer, event) -> this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
            int radius = wrapper.asInt();
            if (radius <= 0) {
                EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.ERROR_NUMBER_INVALID).replace("%num%", radius).getLocalized());
                return false;
            }
            region.setRadius(radius);
            dungeon.save();
            return true;
        }));


        this.addItem(Material.ARMOR_STAND, EditorLocales.REGION_FLAGS, 6).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    region.setFlags(new ArrayList<>());
                    this.save(viewer);
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_HOLOGRAM_TEXT, wrapper -> {
                        List<String> list = region.getFlags();
                        StringBuilder builder = new StringBuilder();
                        for (String text : wrapper.getText().split(" ")) {
                            builder.append(text);
                        }
                        list.add(builder.toString());
                        region.setFlags(list);
                        dungeon.save();
                        return true;
                    });
                }
            }
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    if (item.getType().equals(Material.EMERALD_BLOCK) && !region.isEnabled())
                        item.setType(Material.REDSTONE_BLOCK);
                    else if (item.getType().equals(Material.REDSTONE_BLOCK) && region.isEnabled())
                        item.setType(Material.EMERALD_BLOCK);
                    ItemReplacer.replace(item, region.replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getDungeon().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }
}