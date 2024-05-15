package t.me.p1azmer.plugin.dungeons.mob.editor;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;
import t.me.p1azmer.plugin.dungeons.mob.MobManager;
import t.me.p1azmer.plugin.dungeons.mob.config.MobConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class MobListEditor extends EditorMenu<DungeonPlugin, MobManager> implements AutoPaged<MobConfig> {

  public MobListEditor(@NotNull MobManager mobManager) {
    super(mobManager.plugin(), mobManager, Config.EDITOR_TITLE_MOB.get(), 45);

    this.addReturn(39).setClick((viewer, event) -> {
      this.plugin.getEditor().openNextTick(viewer, 1);
    });
    this.addNextPage(44);
    this.addPreviousPage(36);

    this.addCreation(EditorLocales.MOB_CREATE, 41).setClick((viewer, event) -> {
      this.handleInput(viewer, Lang.Editor_Mob_Enter_Create, wrapper -> {
        if (!mobManager.createMobConfig(wrapper.getTextRaw())) {
          EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.Editor_Mob_Error_Exist).getLocalized());
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
  public List<MobConfig> getObjects(@NotNull Player player) {
    return new ArrayList<>(plugin.getMobManager().getMobConfigs().stream().sorted(Comparator.comparing(MobConfig::getId)).toList());
  }

  @Override
  @NotNull
  public ItemStack getObjectStack(@NotNull Player player, @NotNull MobConfig mob) {
    Material material = Material.getMaterial(mob.getEntityType().name() + "_SPAWN_EGG");
    if (mob.getEntityType() == EntityType.MUSHROOM_COW) material = Material.MOOSHROOM_SPAWN_EGG;
    if (material == null) material = Material.BAT_SPAWN_EGG;

    ItemStack item = new ItemStack(material);
    ItemUtil.editMeta(item, meta -> {
      meta.setDisplayName(EditorLocales.MOB_OBJECT.getLocalizedName());
      meta.setLore(EditorLocales.MOB_OBJECT.getLocalizedLore());
      meta.addItemFlags(ItemFlag.values());
      ItemReplacer.replace(meta, mob.replacePlaceholders());
    });
    return item;
  }

  @Override
  @NotNull
  public ItemClick getObjectClick(@NotNull MobConfig mob) {
    return (viewer, event) -> {
      if (event.isShiftClick()) {
        if (mob.getFile().delete()) {
          mob.clear();
          this.object.getMobConfigMap().remove(mob.getId());
          this.openNextTick(viewer, viewer.getPage());
        }
        return;
      }
      mob.getEditor().openNextTick(viewer, 1);
    };
  }
}