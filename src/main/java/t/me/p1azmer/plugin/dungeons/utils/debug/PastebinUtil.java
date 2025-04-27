package t.me.p1azmer.plugin.dungeons.utils.debug;

import com.pastebin.api.PastebinClient;
import com.pastebin.api.Visibility;
import com.pastebin.api.request.PasteRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.ItemNbt;
import t.me.p1azmer.plugin.dungeons.DungeonAPI;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PastebinUtil {
    PastebinClient client = PastebinClient
            .builder()
            .developerKey("aL23vA-UXpKHvGuqL5_jJ4YIVZGY5Nrr")
            .build();

    public @NotNull String pasteAsync() {
        DungeonPlugin plugin = DungeonAPI.PLUGIN;
        Collection<Dungeon> dungeons = plugin.getDungeonManager().getDungeons();
        String content = String.join("\n", List.of(
                "Java version: " + System.getProperty("java.version"),
                "OS: " + System.getProperty("os.name"),
                "Running on: " + System.getProperty("os.arch"),
                " ",
                "Plugin version: " + plugin.getDescription().getVersion(),
                "Server version: " + plugin.getServer().getVersion() + " (bukkit:" + plugin.getServer().getBukkitVersion() + ")",
                "PLAZMER-ENGINE: " + getEngineVersion(plugin),
                " ",
                "Dungeons amount: " + dungeons.size(),
                " ",
                "Dungeons list:",
                dungeons.stream().map(dungeon -> String.join("\n", List.of(dungeon.getId() + " (" + dungeon.getName() + ")",
                        "Generation Type: " + dungeon.getGenerationSettings().getGenerationType().name(),
                        "Enabled: " + (dungeon.getSettings().isEnabled() ? "Enabled" : "Disabled"),
                        "Modules:\n" + dungeon.getModuleManager()
                                .getModules()
                                .stream()
                                .map(module -> "|> " + module.getName() + ": " + (dungeon.getModuleSettings().isEnabled(module) ? "Enabled" : "Disabled"))
                                .collect(Collectors.joining("\n")),
                        "Rewards:\n" + dungeon.getRewardCollection()
                                .stream()
                                .map(reward -> "|> " + reward.getId() + " (" + reward.getItem().getType().name() + "). Encode Status: " + (ItemNbt.compress(reward.getItem()) == null ? "Not Encoded" : "Encoded"))
                                .collect(Collectors.joining("\n"))
                ))).collect(Collectors.joining("\n-------\n")),
                " ",
                "Access handler:" + (plugin.getAccessHandler() == null ? "No access handler" : plugin.getAccessHandler().getClass().getSimpleName()),
                "Hologram handler:" + (plugin.getHologramHandler() == null ? "No hologram handler" : plugin.getHologramHandler().getClass().getSimpleName()),
                "Schematic handler:" + (plugin.getSchematicHandler() == null ? "No schematic handler" : plugin.getSchematicHandler().getClass().getSimpleName()),
                "Region handler: " + (plugin.getRegionHandler() == null ? "No region handler" : plugin.getRegionHandler().getClass().getSimpleName()),
                "Party handler: " + (plugin.getPartyHandler() == null ? "No party handler" : plugin.getPartyHandler().getClass().getSimpleName())
        ));

        return client.paste(PasteRequest.content(content)
                .name("Debug information at " + new SimpleDateFormat().format(new Date()))
                .visibility(Visibility.UNLISTED)
                .build());
    }

    private @NotNull String getEngineVersion(DungeonPlugin plugin) {
        Plugin engine = plugin.getPluginManager().getPlugin("PLAZMER-ENGINE");
        return engine == null ? "NOT INSTALLED" : engine.getDescription().getVersion();
    }
}
