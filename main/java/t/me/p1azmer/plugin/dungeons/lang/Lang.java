package t.me.p1azmer.plugin.dungeons.lang;

import org.bukkit.Sound;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.CoreLang;
import t.me.p1azmer.plugin.dungeons.Placeholders;

public class Lang extends CoreLang {

    public static LangKey NOTIFY_SINGLE = LangKey.of("Messages.Notify.Single", "<! prefix:\"false\" !>" +
            "&6Dungeon appearance in: " + Placeholders.DUNGEON_WAIT_IN + " seconds!");
    public static LangKey NOTIFY_EVERY = LangKey.of("Messages.Notify.Every", "<! type:\"action_bar\" !>" +
            "&6Dungeon appearance in: " + Placeholders.DUNGEON_WAIT_IN + " seconds!");
    public static LangKey DUNGEON_SPAWN_NOTIFY = new LangKey("Messages.Notify.Spawn",
            "&cATTENTION! &6A new dungeon has appeared\n\n" +
                    "&fCoordinates: &6%location_world%, %location_x%, %location_y%, %location_z%\n\n" +
                    "&fRequired keys:\n" +
                    "%dungeon_key_names%"
    );

    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc", "Open the dungeon and key editor.");

    public static final LangKey COMMAND_DROP_USAGE = LangKey.of("Command.Drop.Usage", "<dungeon_id> <world> <x> <y> <z>");
    public static final LangKey COMMAND_DROP_DESC = LangKey.of("Command.Drop.Desc", "Place a dungeon at the specified location in the world.");
    public static final LangKey COMMAND_DROP_DONE = LangKey.of("Command.Drop.Done", "&7Dungeon &e" + Placeholders.DUNGEON_NAME + "&7 has been placed at coordinates &e" + Placeholders.Location.X + ", " + Placeholders.Location.Y + ", " + Placeholders.Location.Z + "&7 in world &e" + Placeholders.Location.WORLD + "&7.");
    public static final LangKey COMMAND_KEY_DESC = LangKey.of("Command.Key.Desc", "Manage player keys.");
    public static final LangKey COMMAND_KEY_USAGE = LangKey.of("Command.Key.Usage", "[help]");

    public static final LangKey COMMAND_KEY_GIVE_USAGE = LangKey.of("Command.Key.Give.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_DESC = LangKey.of("Command.Key.Give.Desc", "Give a key to a player.");
    public static final LangKey COMMAND_KEY_GIVE_DONE = LangKey.of("Command.Key.Give.Done", "&7Given &ex" + Placeholders.GENERIC_AMOUNT + "&7 keys of type &e" + Placeholders.KEY_NAME + "&7 to player &e" + Placeholders.Player.NAME + "&7.");
    public static final LangKey COMMAND_KEY_GIVE_NOTIFY = LangKey.of("Command.Key.Give.Notify", "&7You have received &ex" + Placeholders.GENERIC_AMOUNT + "&7 keys of type &e" + Placeholders.KEY_NAME + "&7!");

    public static final LangKey COMMAND_KEY_GIVE_ALL_USAGE = LangKey.of("Command.Key.GiveAll.Usage", "<key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DESC = LangKey.of("Command.Key.GiveAll.Desc", "Give a key to all online players.");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DONE = LangKey.of("Command.Key.GiveAll.Done", "&7Given &ex" + Placeholders.GENERIC_AMOUNT + "&7 keys of type &e" + Placeholders.KEY_NAME + "&7 to all players.");

    public static final LangKey COMMAND_KEY_TAKE_USAGE = LangKey.of("Command.Key.Take.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_TAKE_DESC = LangKey.of("Command.Key.Take.Desc", "Take a key from a player.");
    public static final LangKey COMMAND_KEY_TAKE_DONE = LangKey.of("Command.Key.Take.Done", "&7Taken &ex" + Placeholders.GENERIC_AMOUNT + "&7 keys of type &e" + Placeholders.KEY_NAME + "&7 from player &e" + Placeholders.Player.NAME + "&7.");
    public static final LangKey COMMAND_KEY_TAKE_NOTIFY = LangKey.of("Command.Key.Take.Notify", "&7You have lost &cx" + Placeholders.GENERIC_AMOUNT + " " + Placeholders.KEY_NAME + "&7.");

    public static final LangKey COMMAND_KEY_SET_USAGE = LangKey.of("Command.Key.Set.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_SET_DESC = LangKey.of("Command.Key.Set.Desc", "Set the number of keys for a player.");
    public static final LangKey COMMAND_KEY_SET_DONE = LangKey.of("Command.Key.Set.Done", "&7The number of keys of type &e" + Placeholders.KEY_NAME + "&7 for player &e" + Placeholders.Player.NAME + "&7 has been changed to &ex" + Placeholders.GENERIC_AMOUNT + "&7.");
    public static final LangKey COMMAND_KEY_SET_NOTIFY = LangKey.of("Command.Key.Set.Notify", "&7The number of your keys of type &e" + Placeholders.KEY_NAME + "&7 has been changed to &ex" + Placeholders.GENERIC_AMOUNT + "&7.");

    public static final LangKey DUNGEON_ERROR_INVALID = LangKey.of("Dungeon.Error.Invalid", "&cInvalid dungeon!");
    public static final LangKey DUNGEON_ERROR_EXISTS = LangKey.of("Dungeon.Error.Exists", "&cA dungeon with that ID already exists!");

    public static final LangKey DUNGEON_KEY_ERROR_INVALID = LangKey.of("Dungeon.Key.Error.Invalid", "&cInvalid key!");
    public static final LangKey DUNGEON_KEY_ERROR_EXISTS = LangKey.of("Dungeon.Key.Error.Exists", "&cA key with that ID already exists!");


    public static final LangKey DUNGEON_OPEN_ERROR_NO_KEY = LangKey.of("Dungeon.Open.Error.NoKey",
            "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
                    "\n&c&lOops!" +
                    "\n&7You don't have the key for this Dungeon!");
    public static final LangKey DUNGEON_OPEN_ERROR_NO_HOLD_KEY = LangKey.of("Dungeon.Open.Error.NoHoldKey",
            "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
                    "\n&c&lOops!" +
                    "\n&7You must be holding the key to open the Dungeon!");


    public static final LangKey EDITOR_ENTER_DISPLAY_NAME = LangKey.of("Editor.Enter.DisplayName", "&7Enter &a[Displayed Name]");
    public static final LangKey EDITOR_ENTER_SCHEMATIC = LangKey.of("Editor.Enter.Schematic", "&7Enter &a[Dungeon Schematic]");
    public static final LangKey EDITOR_DUNGEON_ENTER_BLOCK_HOLOGRAM_TEXT = LangKey.of("Editor.Dungeon.Enter.Block.Hologram.Text", "&7Enter &a[Text]");

    public static final LangKey EDITOR_DUNGEON_ENTER_ID = LangKey.of("Editor.Dungeon.Enter.Id", "&7Enter &a[Dungeon ID]");
    public static final LangKey EDITOR_DUNGEON_ENTER_KEY_ID = LangKey.of("Editor.Dungeon.Enter.KeyId", "&7Enter &a[Key Identifier]");

    public static final LangKey EDITOR_REWARD_ENTER_ID = LangKey.of("Editor.Reward.Enter.Id", "&7Enter &a[Reward Identifier]");
    public static final LangKey EDITOR_REWARD_ENTER_CHANCE = LangKey.of("Editor.Reward.Enter.Chance", "&7Enter &a[Chance]");
    public static final LangKey EDITOR_REWARD_ENTER_WIN_LIMIT_AMOUNT = LangKey.of("Editor.Reward.Enter.WinLimit.Amount", "&7Enter &a[Quantity Limit]");
    public static final LangKey EDITOR_REWARD_ENTER_WIN_LIMIT_COOLDOWN = LangKey.of("Editor.Reward.Enter.WinLimit.Cooldown", "&7Enter &a[Cooldown in Seconds]");
    public static final LangKey EDITOR_REWARD_ERROR_CREATE_EXIST = LangKey.of("Editor.Reward.Error.Create.Exist", "&cA reward with this identifier already exists!");
}
