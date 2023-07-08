package t.me.p1azmer.engine.api.menu.click;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;

public interface ItemClick {

    void click(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event);
}