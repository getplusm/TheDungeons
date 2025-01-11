package t.me.p1azmer.plugin.dungeons.key;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.ICleanable;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PDCUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Keys;
import t.me.p1azmer.plugin.dungeons.key.editor.KeyMainEditor;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Key extends AbstractConfigHolder<DungeonPlugin> implements Placeholder, ICleanable {

    String name;
    ItemStack item;
    KeyMainEditor editor;

    final PlaceholderMap placeholders;

    public Key(@NotNull DungeonPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.placeholders = new PlaceholderMap()
                .add(Placeholders.KEY_ID, this::getId)
                .add(Placeholders.KEY_NAME, this::getName)
                .add(Placeholders.KEY_ITEM_NAME, () -> ItemUtil.getItemName(this.getItem()))
        ;
    }

    @Override
    public boolean load() {
        this.name = cfg.getString("Name", getId());
        ItemStack item = cfg.getItem("Item");
        if (item.getType().isAir()) {
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

    @NotNull
    public KeyMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new KeyMainEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public ItemStack getRawItem() {
        return new ItemStack(item);
    }

    @NotNull
    public ItemStack getItem() {
        ItemStack item = this.getRawItem();
        PDCUtil.set(item, Keys.DUNGEON_KEY_ID, this.getId());
        return item;
    }
}
