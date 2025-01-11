package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.MainSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.MobsSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class MainSettingsEditor extends EditorMenu<DungeonPlugin, MainSettings> {

    public MainSettingsEditor(@NotNull MainSettings settings) {
        super(settings.getDungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 9);
        Dungeon dungeon = settings.getDungeon();
        MobsSettings mobsSettings = dungeon.getMobsSettings();
        ItemStack mobHead = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5NDlhMTZjYmFlNTRmNTRhMGFmMTA1ZjRjZGExNzEyZWI1YzM5YTc3Y2NhOWE5ZWQ1NTI4ZTAzYjczYWMwIn19fQ==");
        String MINIMAL_ONLINE_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZjYmFlNzI0NmNjMmM2ZTg4ODU4NzE5OGM3OTU5OTc5NjY2YjRmNWE0MDg4ZjI0ZTI2ZTA3NWYxNDBhZTZjMyJ9fX0=";
        ItemStack minimalOnlineHead = ItemUtil.createCustomHead(MINIMAL_ONLINE_HEAD_TEXTURE);

        this.addReturn(8).setClick((viewer, event) -> this.plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1)));

        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_ENABLE, 0).setClick((viewer, event) -> {
            settings.setEnabled(!settings.isEnabled());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isEnabled() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZlYzNkMjVhZTBkMTQ3YzM0MmM0NTM3MGUwZTQzMzAwYTRlNDhhNWI0M2Y5YmI4NThiYWJmZjc1NjE0NGRhYyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ4ZDdkMWUwM2UxYWYxNDViMDEyNWFiODQxMjg1NjcyYjQyMTI2NWRhMmFiOTE1MDE1ZjkwNTg0MzhiYTJkOCJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_ENABLE).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_CLICK_TIMER, 2).setClick((viewer, event) -> {
            settings.setClickTimer(!settings.isClickTimer());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isClickTimer() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_CLICK_TIMER).writeMeta();
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD), EditorLocales.DUNGEON_SETTINGS_LET_PLAYER_WHEN_CLOSE, 3).setClick((viewer, event) -> {
            settings.setLetPlayersWhenClose(!settings.isLetPlayersWhenClose());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = settings.isLetPlayersWhenClose() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.DUNGEON_SETTINGS_LET_PLAYER_WHEN_CLOSE).writeMeta();
        });

        this.addItem(minimalOnlineHead, EditorLocales.DUNGEON_SETTINGS_MINIMAL_ONLINE, 5).setClick((viewer, event) -> this.handleInput(viewer, Lang.EDITOR_DUNGEON_WRITE_VALUE, wrapper -> {
            settings.setMinimalOnline(wrapper.asAnyInt(0));
            dungeon.save();
            return true;
        }));
        this.addItem(mobHead, EditorLocales.DUNGEON_SETTINGS_MOBS, 6).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    mobsSettings.getMobMap().clear();
                    this.save(viewer);
                }
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), plugin().getMobManager().getMobIds(), false);
            this.handleInput(viewer, Lang.Editor_Mob_Enter_Id, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length != 2) return false;

                String mobId = split[0];

                int value = NumberUtil.getInteger(split[1], 1);
                mobsSettings.getMobMap().put(mobId, value);
                dungeon.save();
                return true;
            });
        });
        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier(((viewer, item) -> {
            ItemReplacer.replace(item, dungeon.replacePlaceholders());
            ItemReplacer.replace(item, dungeon.getRegion().replacePlaceholders());
            ItemReplacer.replace(item, dungeon.getMobsSettings().replacePlaceholders());
            ItemReplacer.replace(item, dungeon.getSettings().replacePlaceholders());
        })));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getDungeon().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }
}