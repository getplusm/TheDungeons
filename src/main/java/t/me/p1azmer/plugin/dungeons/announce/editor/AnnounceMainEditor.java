package t.me.p1azmer.plugin.dungeons.announce.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

public class AnnounceMainEditor extends EditorMenu<DungeonPlugin, Announce> {

    public AnnounceMainEditor(@NotNull Announce announce) {
        super(announce.plugin(), announce, Config.EDITOR_TITLE_ANNOUNCE.get(), 9);

        this.addReturn(8).setClick((viewer, event) -> {
            this.plugin.runTask(task -> announce.getManager().getEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.COMPARATOR, EditorLocales.ANNOUNCE_MESSAGES, 0).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
                String text = wrapper.getText();
                LangMessage messages = announce.getMessage();
                announce.setMessage(new LangMessage(plugin(), messages.getRaw() + "\n" + text));
                this.save(viewer);
                return true;
            });
        });
        this.addItem(new ItemStack(Material.PLAYER_HEAD),
                EditorLocales.ANNOUNCE_GLOBAL, 2).setClick((viewer, event) -> {
            announce.setGlobal(!announce.isGlobal());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            ItemStack replacer = announce.isGlobal() ? ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=") :
                    ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTRkZGE4MGVhNjRiMThiYzM3NWI4MWNlMWVkMTkwN2ZjODFhZWE2YjFjZjNjNGY3YWQzMTQ0Mzg5ZjY0YyJ9fX0=");
            item.setItemMeta(replacer.getItemMeta());
            ItemReplacer.create(item).readLocale(EditorLocales.ANNOUNCE_GLOBAL).writeMeta();
        });
        this.addItem(announce.getIcon(), EditorLocales.ANNOUNCE_ICON, 4).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                announce.setIcon(cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(announce.getIcon().getType());
            item.setItemMeta(announce.getIcon().getItemMeta());
            ItemUtil.mapMeta(item, meta -> {
                meta.setDisplayName(EditorLocales.ANNOUNCE_ICON.getLocalizedName());
                meta.setLore(EditorLocales.ANNOUNCE_ICON.getLocalizedLore());
                meta.addItemFlags(ItemFlag.values());
            });
        }));

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, announce.replacePlaceholders())));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}