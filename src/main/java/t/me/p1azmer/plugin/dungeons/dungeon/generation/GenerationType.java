package t.me.p1azmer.plugin.dungeons.dungeon.generation;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.dungeon.module.ModuleId;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum GenerationType {
    /**
     * Static - the position of this dungeon will always be the same
     */
    STATIC(
            ItemUtil.createCustomHead(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmY2JlOTIxN2Y4OWYyYzEyNDcxNjQ5YTk1NDVhNzVhYjFiMDdiNzZmNWQ4NjlhYjM2NDUxYmEzOTczNzIyMCJ9fX0="
            ),
            List.of(ModuleId.SPAWN, ModuleId.SCHEMATIC)
    ),
    /**
     * Dynamic - the position of the dungeon will always be
     * random based on the settings of the generator
     */
    DYNAMIC(
            ItemUtil.createCustomHead(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZlNmQxN2NlNDQyY2NlMzQ0YjBiZjU3MGI2YmZlNmEwYWRhZmFiZTZkMTE2ZDhiZjVkYTliNDA3NGM0MWExMSJ9fX0="
            ),
            new ArrayList<>()
    );
    /**
     * Updatable - the position of the dungeon will be static,
     * but its schematics will be created and deleted according to the timer settings
     * 11.04.24 - not tested
     */
//    UPDATABLE(
//            ItemUtil.createCustomHead(
//                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJlNjk2ZTc4MzcwMWIzYjVjOGE3MTkwMjJmZGFkMmRhMWE0YWRmMzM4YzA3NjM4ODIwMWU5MmE2NzA4NzAyYyJ9fX0="
//            ),
//            new ArrayList<>()
//    );

    private final ItemStack icon;
    private final List<String> moduleWhitelist;

    GenerationType(@NotNull ItemStack icon, @NotNull List<String> moduleWhitelist) {
        this.icon = icon;
        this.moduleWhitelist = moduleWhitelist;
    }

    public boolean isStatic() {
        return this == STATIC;
    }

    public boolean isDynamic() {
        return this == DYNAMIC;
    }

//    public boolean isUpdatable() {
//       return this == UPDATABLE;
//    }
}
