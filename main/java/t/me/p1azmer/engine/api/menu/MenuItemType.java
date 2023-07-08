package t.me.p1azmer.engine.api.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.editor.EditorButtonType;
import t.me.p1azmer.engine.utils.Colorizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum MenuItemType implements EditorButtonType {
    NONE,
    PAGE_NEXT(Material.ARROW, "&6Следующая страница"),
    PAGE_PREVIOUS(Material.ARROW, "&6Предыдущая страница"),
    CLOSE(Material.BARRIER, "&cЗакрыть"),
    RETURN(Material.BARRIER, "&cНазад"),
    CONFIRMATION_ACCEPT(Material.LIME_GLAZED_TERRACOTTA, "&a&lПРИНЯТЬ"),
    CONFIRMATION_DECLINE(Material.PINK_GLAZED_TERRACOTTA, "&c&lОТКЛОНИТЬ"),
    ;

    private final Material material;
    private String name;
    private List<String> lore;

    MenuItemType() {
        this(Material.AIR, "", "");
    }

    MenuItemType(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
        this.material = material;
        this.setName(name);
        this.setLore(Arrays.asList(lore));
    }

    @NotNull
    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @Override
    @NotNull
    public List<String> getLore() {
        return lore;
    }

    @Override
    public void setLore(@NotNull List<String> lore) {
        this.lore = Colorizer.apply(new ArrayList<>(lore));
    }
}
