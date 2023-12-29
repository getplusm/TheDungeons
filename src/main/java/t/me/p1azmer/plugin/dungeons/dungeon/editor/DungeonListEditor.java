package t.me.p1azmer.plugin.dungeons.dungeon.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.DungeonManager;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class DungeonListEditor extends EditorMenu<DungeonPlugin, DungeonManager> implements AutoPaged<Dungeon> {

    public DungeonListEditor(@NotNull DungeonManager manager) {
        super(manager.plugin(), manager, Config.EDITOR_TITLE_DUNGEON.get(), 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.DUNGEON_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_DUNGEON_ENTER_ID, wrapper -> {
                if (!manager.create(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.DUNGEON_ERROR_EXISTS).getLocalized());
                    return false;
                }
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<Dungeon> getObjects(@NotNull Player player) {
        return this.object.getDungeons().stream().sorted(Comparator.comparing(Dungeon::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Dungeon dungeon) {
        Material material = dungeon.getChestSettings().getMaterial();
        ItemStack item = new ItemStack(material.isAir() ? Material.CHEST : material);
        ItemReplacer.create(item)
                .readLocale(EditorLocales.DUNGEON_OBJECT)
                .trimmed()
                .hideFlags()
                .replace(dungeon.replacePlaceholders())
                .replace(Colorizer::apply)
                .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Dungeon dungeon) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.delete(dungeon);
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                return;
            }
            this.plugin.runTask(task -> dungeon.getEditor().open(viewer.getPlayer(), 1));
        };
    }
}