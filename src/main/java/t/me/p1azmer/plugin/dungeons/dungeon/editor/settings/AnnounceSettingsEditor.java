package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.AnnounceSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnnounceSettingsEditor extends EditorMenu<DungeonPlugin, AnnounceSettings> implements AutoPaged<DungeonStage> {

    public AnnounceSettingsEditor(@NotNull AnnounceSettings settings) {
        super(settings.dungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 36);

        this.addReturn(31).setClick((viewer, event) -> this.plugin.runTask(task -> settings.dungeon().getEditor().open(viewer.getPlayer(), 1)));
        this.addNextPage(32);
        this.addPreviousPage(30);

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, settings.replacePlaceholders())));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(10, 17).toArray();
    }

    @Override
    @NotNull
    public List<DungeonStage> getObjects(@NotNull Player player) {
        return new ArrayList<>(Arrays.stream(DungeonStage.values()).toList());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull DungeonStage stage) {
        ItemStack item = new ItemStack(Material.FLOWER_BANNER_PATTERN);
        Map<Announce, int[]> map = this.object.getAnnounceMap(stage);

        ItemReplacer.create(item)
                .readLocale(EditorLocales.ANNOUNCE_MODULE_OBJECT)
                .trimmed()
                .replace(Placeholders.EDITOR_STAGE_NAME, stage.name())
                .replace(Placeholders.EDITOR_STAGE_DESCRIPTION, stage.getDescription(plugin()))
                .hideFlags()
                .replace(Placeholders.EDITOR_STAGE_ANNOUNCES, Colorizer.apply(Colors.LIGHT_PURPLE + String.join("\n", map.entrySet().stream().map(pair -> Colors.LIGHT_PURPLE + pair.getKey().getId() + " " + Colors.PURPLE + Arrays.toString(pair.getValue())).toList())))
                .replace(this.object.replacePlaceholders())
                .replace(Colorizer::apply)
                .writeMeta();

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull DungeonStage stage) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();

            if (event.getClick().equals(ClickType.LEFT)) {
                EditorManager.prompt(player, plugin.getMessage(Lang.Editor_Enter_Announce_And_Time).getLocalized());
                EditorManager.suggestValues(player, plugin.getAnnounceManager().getAnnounces().stream().map(AbstractConfigHolder::getId).collect(Collectors.toList()), false);
                EditorManager.startEdit(player, wrapper -> {
                    String message = wrapper.getText();
                    String[] splitter = message.split(" ");
                    Announce announce = plugin.getAnnounceManager().getAnnounce(splitter[0]);
                    if (announce == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.Editor_Announce_And_Time_Error).getLocalized());
                        EditorManager.displayValues(player, false, 1);
                        return false;
                    }

                    int[] time = splitter.length == 1 ? new int[]{0} : Arrays.stream(splitter[1].replace(" ", "").split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    Map<Announce, int[]> map = this.object.getAnnounceMap(stage);
                    if (map.containsKey(announce) && map.get(announce).length > 0) {
                        time = mergeArrays(time, map.get(announce));
                    }
                    map.put(announce, time);
                    this.object.setAnnounce(stage, map);
                    this.save(viewer);
                    return true;
                });
                plugin.runTask(task -> player.closeInventory());
                return;
            }
            if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                this.object.removeAnnounce(stage);
                this.save(viewer);
            }
        };
    }

    private static int[] mergeArrays(int[] array1, int[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        int[] mergedArray = new int[length1 + length2];

        System.arraycopy(array1, 0, mergedArray, 0, length1);
        System.arraycopy(array2, 0, mergedArray, length1, length2);

        return mergedArray;
    }
}