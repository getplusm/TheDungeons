package t.me.p1azmer.plugin.dungeons.mob.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.mob.config.MobConfig;
import t.me.p1azmer.plugin.dungeons.mob.style.MobStyleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MobStylesEditor extends EditorMenu<DungeonPlugin, MobConfig> implements AutoPaged<MobStyleType> {

    public MobStylesEditor(@NotNull DungeonPlugin plugin, @NotNull MobConfig mobConfig) {
        super(plugin, mobConfig, Config.EDITOR_TITLE_MOB.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> {
            mobConfig.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);
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
    public List<MobStyleType> getObjects(@NotNull Player player) {
        return new ArrayList<>(Arrays.asList(MobStyleType.get(this.object.getEntityType())));
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull MobStyleType styleType) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.MOB_STYLE_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.MOB_STYLE_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, str -> str
                    .replace(Placeholders.MOB_STYLE_TYPE, plugin.getLangManager().getEnum(styleType))
                    .replace(Placeholders.MOB_STYLE_VALUE, this.object.getStyle(styleType))
            );
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull MobStyleType styleType) {

        return (viewer, event) -> {
            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), Stream.of(styleType.getWrapper().getWriter().values()).map(String::valueOf).toList(), true);
                this.handleInput(viewer, Lang.Editor_Mov_Enter_Style, wrapper -> {
                    this.object.addStyle(styleType, wrapper.getTextRaw());
                    this.object.save();
                    return true;
                });
            } else if (event.isRightClick()) {
                this.object.removeStyle(styleType);
                this.openNextTick(viewer, viewer.getPage());
            }
        };
    }
}