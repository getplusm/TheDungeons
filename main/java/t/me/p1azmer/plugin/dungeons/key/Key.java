package t.me.p1azmer.plugin.dungeons.key;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.api.placeholder.IPlaceholder;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PDCUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.key.editor.KeyMainEditor;

public class Key extends AbstractConfigHolder<DungeonPlugin> implements IPlaceholder, ICleanable {

    private String name;
    private ItemStack item;

    private KeyMainEditor editor;

    private final PlaceholderMap placeholderMap;

    public Key(@NotNull DungeonPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.KEY_ID, this::getId)
                .add(Placeholders.KEY_NAME, this::getName)
                .add(Placeholders.KEY_ITEM_NAME, () -> ItemUtil.getItemName(this.getItem()))
        ;
    }

    @Override
    public boolean load() {
        this.name = cfg.getString("Name", getId());
        ItemStack item = cfg.getItem("Item");
        if (item.getType().equals(Material.AIR)) {
            item = new ItemStack(Material.TRIPWIRE_HOOK);
        }
        this.setItem(item);
        return true;
    }

    @Override
    public void onSave() {
        this.cfg.set("Name", getName());
        this.cfg.setItem("Item", getRawItem());
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public KeyMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new KeyMainEditor(this);
        }
        return this.editor;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public ItemStack getRawItem() {
        return new ItemStack(item);
    }

    @NotNull
    public ItemStack getItem() {
        ItemStack item = this.getRawItem();
        PDCUtil.set(item, Keys.CRATE_KEY_ID, this.getId());
        return item;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
