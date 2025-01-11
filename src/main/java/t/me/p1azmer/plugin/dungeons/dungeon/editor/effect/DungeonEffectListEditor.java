package t.me.p1azmer.plugin.dungeons.dungeon.editor.effect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.effect.Effect;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.editor.EditorLocales;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DungeonEffectListEditor extends EditorMenu<DungeonPlugin, Dungeon> implements AutoPaged<Effect> {

    public DungeonEffectListEditor(@NotNull Dungeon dungeon) {
        super(dungeon.plugin(), dungeon, Config.EDITOR_TITLE_DUNGEON.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> this.plugin.runTask(rask -> dungeon.getEditor().open(viewer.getPlayer(), 1)));
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.EFFECT_CREATE, 42).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), Arrays.stream(PotionEffectTypeWrapper.values()).map(PotionEffectType::getName).collect(Collectors.toList()), false);
            this.handleInput(viewer, Lang.EDITOR_EFFECT_ENTER_TYPE, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                PotionEffectType potionEffectType = PotionEffectType.getByName(id);
                if (potionEffectType == null) {
                    EditorManager.suggestValues(viewer.getPlayer(), Arrays.stream(PotionEffectTypeWrapper.values()).map(PotionEffectType::getName).collect(Collectors.toList()), false);
                    return false;
                }
                this.object.getEffectSettings().getEffects().add(new Effect(potionEffectType, 25, 1));
                return true;
            });
        });

        this.addItem(Material.HOPPER, EditorLocales.EFFECT_SORT, 38).setClick((viewer, event) -> {
            Comparator<Effect> comparator;
            t.me.p1azmer.engine.api.menu.click.ClickType type = t.me.p1azmer.engine.api.menu.click.ClickType.from(event);
            if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_1) {
                comparator = Comparator.comparingDouble(Effect::getDuration).reversed();
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_2) {
                comparator = Comparator.comparingDouble(Effect::getDuration).reversed();
            } else if (type == t.me.p1azmer.engine.api.menu.click.ClickType.NUMBER_3) {
                comparator = Comparator.comparing(r -> r.getPotionEffectType().getName());
            } else return;
            this.object.getEffectSettings().setEffects(this.object.getEffectSettings().getEffects().stream().sorted(comparator).toList());
            this.save(viewer);
        });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, dungeon.replacePlaceholders())));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
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
    public List<Effect> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getEffectSettings().getEffects());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Effect effect) {
        ItemStack item = new ItemStack(Material.POTION);
        ItemUtil.editMeta(item, meta -> {
            if (meta instanceof PotionMeta potionMeta)
                potionMeta.addCustomEffect(effect.build(), true);

            meta.setDisplayName(EditorLocales.EFFECT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.EFFECT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
        });
        ItemReplacer.replace(item, effect.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Effect effect) {
        return (viewer, event) -> {
            if (event.getClick().isShiftClick() && event.getClick().isRightClick()) {
                this.object.getEffectSettings().getEffects().remove(effect);
                this.save(viewer);
                return;
            }
            if (event.getClick().isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_EFFECT_ENTER_DURATION, wrapper -> {
                    int duration = wrapper.asInt();
                    if (duration <= 0) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_NUMBER_INVALID).replace("%num%", duration).getLocalized());
                        return false;
                    }
                    effect.setDuration(duration);
                    this.save(viewer);
                    return true;
                });
                return;
            }
            if (event.getClick().isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_EFFECT_ENTER_AMPLIFIER, wrapper -> {
                    int amplifier = wrapper.asInt();
                    if (amplifier <= 0) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_NUMBER_INVALID).replace("%num%", amplifier).getLocalized());
                        return false;
                    }
                    effect.setAmplifier(amplifier);
                    this.save(viewer);
                    return true;
                });
            }
        };
    }
}