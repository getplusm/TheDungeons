package t.me.p1azmer.plugin.dungeons.dungeon.editor.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.CommandsSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.StageSettings;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class StageSettingsEditor extends EditorMenu<DungeonPlugin, StageSettings> implements AutoPaged<DungeonStage> {

    public StageSettingsEditor(@NotNull StageSettings settings) {
        super(settings.dungeon().plugin(), settings, Config.EDITOR_TITLE_DUNGEON.get(), 36);

        this.addReturn(31).setClick((viewer, event) -> {
            this.plugin.runTask(task -> settings.dungeon().getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(32);
        this.addPreviousPage(30);

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemReplacer.replace(item, settings.replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.dungeon().save();
        DungeonStage.call(this.object.dungeon(), DungeonStage.CANCELLED, "stage editor need reboot");
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
        ItemStack item = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNiOGYwNjg4NWQxZGFhZmQyNmNkOTViMzQ4MmNiNTI1ZDg4MWE2N2UwZDI0NzE2MWI5MDhkOTNkNTZkMTE0ZiJ9fX0=");
        ItemReplacer.create(item)
                .readLocale(EditorLocales.STAGES_OBJECT)
                .trimmed()
                .hideFlags()
                .replace(s -> s
                        .replace(Placeholders.EDITOR_STAGE_NAME, stage.name())
                        .replace(Placeholders.EDITOR_STAGE_TIME, String.valueOf(this.object.getTime(stage)))
                )
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
                EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_DUNGEON_WRITE_VALUE).getLocalized());
                EditorManager.startEdit(player, wrapper -> {
                    int value = wrapper.asInt(1);
                    this.object.setTime(stage, value);
                    this.save(viewer);
                    return true;
                });
                plugin.runTask(task -> player.closeInventory());
            }
        };
    }
}