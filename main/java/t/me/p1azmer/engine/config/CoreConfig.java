package t.me.p1azmer.engine.config;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.EngineCore;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreConfig {

    @Deprecated
    private static Map<String, String> LOCALE_WORLD_NAMES;


    @NotNull
    @Deprecated
    public static String getWorldName(@NotNull String world) {
        return LOCALE_WORLD_NAMES.getOrDefault(world, world);
    }

    public static void load(@NotNull DungeonPlugin engine) {
        JYML cfg = engine.getConfig();

        cfg.addMissing("Locale.World_Names.world", "World");
        cfg.addMissing("Locale.World_Names.world_nether", "Nether");
        cfg.addMissing("Locale.World_Names.world_the_end", "The End");

        LOCALE_WORLD_NAMES = new HashMap<>();
        cfg.getSection("Locale.World_Names").forEach(world -> {
            String name = StringUtil.color(cfg.getString("Locale.World_Names." + world, world));
            LOCALE_WORLD_NAMES.put(world, name);
        });
    }

    @NotNull
    public static UUID getIdForSkullTexture(@NotNull String base64) {
        JYML cfg = EngineCore.get().getConfig();

        UUID uuid;
        String idRaw = cfg.getString("Head_Texture_Cache." + base64, UUID.randomUUID().toString());
        try {
            uuid = UUID.fromString(idRaw);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }

        cfg.addMissing("Head_Texture_Cache." + base64, uuid.toString());
        cfg.saveChanges();

        return uuid;
    }
}