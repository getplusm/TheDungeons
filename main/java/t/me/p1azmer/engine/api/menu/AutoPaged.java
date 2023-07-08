package t.me.p1azmer.engine.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.api.menu.item.ItemOptions;
import t.me.p1azmer.engine.api.menu.item.MenuItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface AutoPaged<I> {

    int[] getObjectSlots();

    @NotNull List<I> getObjects(@NotNull Player player);

    @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull I object);

    @NotNull ItemClick getObjectClick(@NotNull I object);

    @Deprecated
    @NotNull default Comparator<I> getObjectSorter() {
        return (o1, o2) -> 0;
    }

    @NotNull
    default List<MenuItem> getItemsForPage(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        List<MenuItem> items = new ArrayList<>();
        List<I> origin = this.getObjects(player);

        int limit = this.getObjectSlots().length;
        int pages = (int) Math.ceil((double) origin.size() / (double) limit);
        viewer.setPages(pages);
        viewer.finePage();

        int skip = (viewer.getPage() - 1) * limit;

        List<I> list = new ArrayList<>(origin.stream().skip(skip).limit(limit).collect(Collectors.toList()));
        int count = 0;
        for (I object : list) {
            ItemStack item = this.getObjectStack(player, object);
            ItemOptions options = ItemOptions.personalWeak(player);
            MenuItem menuItem = new MenuItem(item, 100, options, this.getObjectSlots()[count++]);
            menuItem.setClick(this.getObjectClick(object));
            items.add(menuItem);
        }

        return items;
    }
}