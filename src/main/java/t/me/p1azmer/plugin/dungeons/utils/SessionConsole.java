package t.me.p1azmer.plugin.dungeons.utils;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.formatting.text.Component;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SessionConsole extends LocalSession implements Actor {

    private final DungeonPlugin plugin;

    public SessionConsole(DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey() {
            @Override
            public @NotNull String getName() {
                return "PLAZMER-CONSOLE";
            }

            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return UUID.randomUUID();
            }
        };
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public void checkPermission(String permission) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            plugin.getLogger().info(part);
        }
    }

    @Override
    public void printDebug(String msg) {
        for (String part : msg.split("\n")) {
            plugin.getLogger().info(part);
        }
    }

    @Override
    public void print(String msg) {
        for (String part : msg.split("\n")) {
            plugin.getLogger().info(part);
        }
    }

    @Override
    public void printError(String msg) {
        for (String part : msg.split("\n")) {
            plugin.getLogger().severe(part);
        }
    }

    @Override
    public void print(Component component) {
    }

    @Override
    public boolean canDestroyBedrock() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public File openFileOpenDialog(String[] extensions) {
        return null;
    }

    @Override
    public File openFileSaveDialog(String[] extensions) {
        return null;
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {

    }

    @Override
    public Locale getLocale() {
        return Locale.ROOT;
    }


    public boolean runAction(Runnable ifFree, boolean checkFree, boolean async) {
        return false;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    public Map<String, Object> getRawMeta() {
        return null;
    }
}
