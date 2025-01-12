package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.AccessSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessSettingsEditor extends EditorMenu<DungeonPlugin, AccessSettings> {

    public AccessSettingsEditor(@NotNull AccessSettings settings) {
        super(settings.getDungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 9);
        Dungeon dungeon = settings.getDungeon();

        addReturn(8).setClick((viewer, event) -> plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1)));

        addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_ACCESS_ENABLED, 0).setClick((viewer, event) -> {
            settings.setEnabled(!settings.isEnabled());
            save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isEnabled() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZlYzNkMjVhZTBkMTQ3YzM0MmM0NTM3MGUwZTQzMzAwYTRlNDhhNWI0M2Y5YmI4NThiYWJmZjc1NjE0NGRhYyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ4ZDdkMWUwM2UxYWYxNDViMDEyNWFiODQxMjg1NjcyYjQyMTI2NWRhMmFiOTE1MDE1ZjkwNTg0MzhiYTJkOCJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_ACCESS_ENABLED).writeMeta();
        });

        addItem(Material.PAPER, EditorLocales.DUNGEON_SETTINGS_ACCESS_NOT_ACCESS_MESSAGE, 3).setClick((viewer, event) -> {
            handleInput(viewer, Lang.EDITOR_ACCESS_ENTER_NOT_ACCESS_MESSAGE, wrapper -> {
                String message = wrapper.getText();
                settings.setNotAccessMessage(message);
                dungeon.save();
                return true;
            });
        });

        if (!EngineUtils.hasPlugin("Fabled")) {
            return;
        }
        addItem(Material.FLOWER_BANNER_PATTERN, EditorLocales.DUNGEON_SETTINGS_ACCESS_PSAPI_CLASSES, 5).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                settings.setProSkillAPIAccessClasses(new HashSet<>());
                save(viewer);
                return;
            }
            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), Fabled.getClasses().values()
                        .stream().map(FabledClass::getName).collect(Collectors.toSet()), true);
                handleInput(viewer, Lang.EDITOR_ACCESS_PSAPI_ENTER_CLASS, wrapper -> {
                    String className = wrapper.getText();
                    FabledClass fabledClass = Fabled.getClass(className);
                    if (fabledClass == null) {
                        EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.EDITOR_DUNGEON_ERROR_ACCESS_PSAPI_CLASS_NOT_FOUND).getLocalized());
                        return false;
                    }
                    Set<String> proSkillAPIAccessClasses = settings.getProSkillAPIAccessClasses();
                    proSkillAPIAccessClasses.add(className);
                    settings.setProSkillAPIAccessClasses(proSkillAPIAccessClasses);
                    dungeon.save();
                    return true;
                });
            }
        });
        getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier(((viewer, item) -> {
            ItemReplacer.replace(item, dungeon.replacePlaceholders());
            ItemReplacer.replace(item, settings.replacePlaceholders());
            ItemReplacer.replace(item, dungeon.getSettings().replacePlaceholders());
        })));
    }

    private void save(@NotNull MenuViewer viewer) {
        object.getDungeon().save();
        plugin.runTask(task -> open(viewer.getPlayer(), viewer.getPage()));
    }
}
