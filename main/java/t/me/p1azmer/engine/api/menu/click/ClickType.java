package t.me.p1azmer.engine.api.menu.click;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public enum ClickType {

    LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT,
    DROP_KEY, SWAP_KEY,
    NUMBER_1,
    NUMBER_2,
    NUMBER_3,
    NUMBER_4,
    NUMBER_5,
    NUMBER_6,
    NUMBER_7,
    NUMBER_8,
    NUMBER_9,
    ;

    @NotNull
    public static ClickType from(@NotNull InventoryClickEvent e) {
        if (e.getClick() == org.bukkit.event.inventory.ClickType.DROP) return DROP_KEY;
        //if (e.getClick() == org.bukkit.event.inventory.ClickType.) return SWAP_KEY;
        if (e.getHotbarButton() >= 0) {
            int hotbarButton = e.getHotbarButton();
            ClickType result;
            switch (hotbarButton) {
                case 0:
                    result = NUMBER_1;
                    break;
                case 1:
                    result = NUMBER_2;
                    break;
                case 2:
                    result = NUMBER_3;
                    break;
                case 3:
                    result = NUMBER_4;
                    break;
                case 4:
                    result = NUMBER_5;
                    break;
                case 5:
                    result = NUMBER_6;
                    break;
                case 6:
                    result = NUMBER_7;
                    break;
                case 7:
                    result = NUMBER_8;
                    break;
                case 8:
                    result = NUMBER_9;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + hotbarButton);
            }
            return result;
        }

        if (e.isShiftClick()) {
            return e.isLeftClick() ? SHIFT_LEFT : SHIFT_RIGHT;
        }
        return e.isLeftClick() ? LEFT : RIGHT;
    }
}