package t.me.p1azmer.plugin.dungeons.announce.editor;

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
import t.me.p1azmer.plugin.dungeons.announce.AnnounceManager;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class AnnounceListEditor extends EditorMenu<DungeonPlugin, AnnounceManager> implements AutoPaged<Announce> {

  public AnnounceListEditor(@NotNull AnnounceManager manager) {
    super(manager.plugin(), manager, Config.EDITOR_TITLE_ANNOUNCE.get(), 45);

    this.addReturn(39).setClick((viewer, event) -> {
      this.plugin.runTask(task -> this.plugin.getEditor().open(viewer.getPlayer(), 1));
    });
    this.addNextPage(44);
    this.addPreviousPage(36);

    this.addCreation(EditorLocales.ANNOUNCE_CREATE, 41).setClick((viewer, event) -> {
      this.handleInput(viewer, Lang.Editor_Announce_Enter_Create, wrapper -> {
        if (!manager.create(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
          EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.Editor_Announce_Error_Exist).getLocalized());
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
  public List<Announce> getObjects(@NotNull Player player) {
    return this.object.getAnnounces().stream().sorted(Comparator.comparing(Announce::getId)).toList();
  }

  @Override
  @NotNull
  public ItemStack getObjectStack(@NotNull Player player, @NotNull Announce announce) {
    ItemStack item = announce.getIcon();
    ItemReplacer.create(item)
                .readLocale(EditorLocales.ANNOUNCE_OBJECT)
                .trimmed()
                .hideFlags()
                .replace(announce.replacePlaceholders())
                .replace(Colorizer::apply)
                .writeMeta();
    return item;
  }

  @Override
  @NotNull
  public ItemClick getObjectClick(@NotNull Announce announce) {
    return (viewer, event) -> {
      if (event.isShiftClick() && event.isRightClick()) {
        this.object.delete(announce);
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
        return;
      }
      this.plugin.runTask(task -> announce.getEditor().open(viewer.getPlayer(), 1));
    };
  }
}