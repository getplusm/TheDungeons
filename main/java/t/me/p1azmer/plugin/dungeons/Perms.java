package t.me.p1azmer.plugin.dungeons;

import t.me.p1azmer.engine.api.server.JPermission;

public class Perms {
    private static final String PREFIX         = "dungeons.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission PLUGIN  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all the plugin functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all the plugin commands.");

    public static final JPermission COMMAND_RELOAD          = new JPermission(PREFIX_COMMAND + "reload", "Access to the 'reload' sub-command.");
    public static final JPermission COMMAND_EDITOR          = new JPermission(PREFIX_COMMAND + "editor", "Access to the 'editor' sub-command.");
    public static final JPermission COMMAND_DROP            = new JPermission(PREFIX_COMMAND + "drop", "Access to the 'drop' sub-command.");
    public static final JPermission COMMAND_KEY             = new JPermission(PREFIX_COMMAND + "key", "Access to the 'key' sub-command (without sub-commands).");
    public static final JPermission COMMAND_KEY_GIVE        = new JPermission(PREFIX_COMMAND + "key.give", "Access to the 'key give' command.");
    public static final JPermission COMMAND_KEY_TAKE        = new JPermission(PREFIX_COMMAND + "key.take", "Access to the 'key take' command.");
    public static final JPermission COMMAND_KEY_SET         = new JPermission(PREFIX_COMMAND + "key.set", "Access to the 'key set' command.");

    static {
        PLUGIN.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_RELOAD, COMMAND_EDITOR, COMMAND_DROP,
                COMMAND_KEY, COMMAND_KEY_GIVE, COMMAND_KEY_SET, COMMAND_KEY_TAKE);
    }
}
