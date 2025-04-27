package t.me.p1azmer.plugin.dungeons.lang;

import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.EngineLang;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;

import static t.me.p1azmer.engine.utils.Colors2.*;

public class Lang extends EngineLang {
    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc",
            "Open the dungeon and key editor.");

    public static final LangKey COMMAND_DROP_USAGE = LangKey.of("Command.Drop.Usage",
            "<dungeon_id> <world> <x> <y> <z>");
    public static final LangKey COMMAND_DROP_DESC = LangKey.of("Command.Drop.Desc",
            "Place a dungeon at the specified location in the world.");
    public static final LangKey COMMAND_DROP_DONE = LangKey.of("Command.Drop.Done",
            GRAY + "Dungeon " + YELLOW + Placeholders.DUNGEON_NAME + GRAY + " has been placed at coordinates " + YELLOW + Placeholders.LOCATION_X + "," +
                    " " + Placeholders.LOCATION_Y + "," +
                    " " + Placeholders.LOCATION_Z + GRAY + " in world " + YELLOW + Placeholders.LOCATION_WORLD + GRAY + ".");
    public static final LangKey COMMAND_DROP_ERROR = LangKey.of("Command.Drop.Error",
            RED + "An error occurred while trying to spawn a " + LIGHT_PURPLE + Placeholders.DUNGEON_NAME + RED + "! See the error in the console");


    public static final LangKey COMMAND_DEL_USAGE = LangKey.of("Command.Delete.Usage",
            "<dungeon_id>");
    public static final LangKey COMMAND_DEL_DESC = LangKey.of("Command.Delete.Desc",
            "Despawn a specific or all dungeon.");
    public static final LangKey COMMAND_DEL_DONE = LangKey.of("Command.Delete.Done",
            GRAY + "Dungeon " + YELLOW + Placeholders.DUNGEON_NAME + GRAY + " has been despawned");
    public static final LangKey COMMAND_KEY_DESC = LangKey.of("Command.Key.Desc",
            "Manage player keys.");
    public static final LangKey COMMAND_KEY_USAGE = LangKey.of("Command.Key.Usage",
            "[help]");


    public static final LangKey COMMAND_KEY_GIVE_USAGE = LangKey.of("Command.Key.Give.Usage",
            "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_DESC = LangKey.of("Command.Key.Give.Desc",
            "Give a key to a player.");
    public static final LangKey COMMAND_KEY_GIVE_DONE = LangKey.of("Command.Key.Give.Done",
            GRAY + "Given " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " to player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + ".");
    public static final LangKey COMMAND_KEY_GIVE_NOTIFY = LangKey.of("Command.Key.Give.Notify",
            GRAY + "You have received " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + "!");
    public static final LangKey COMMAND_KEY_GIVE_ALL_USAGE = LangKey.of("Command.Key.GiveAll.Usage",
            "<key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DESC = LangKey.of("Command.Key.GiveAll.Desc",
            "Give a key to all online players.");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DONE = LangKey.of("Command.Key.GiveAll.Done",
            GRAY + "Given " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " to all players.");


    public static final LangKey COMMAND_KEY_TAKE_USAGE = LangKey.of("Command.Key.Take.Usage",
            "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_TAKE_DESC = LangKey.of("Command.Key.Take.Desc",
            "Take a key from a player.");
    public static final LangKey COMMAND_KEY_TAKE_DONE = LangKey.of("Command.Key.Take.Done",
            GRAY + "Taken " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " from player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + ".");
    public static final LangKey COMMAND_KEY_TAKE_NOTIFY = LangKey.of("Command.Key.Take.Notify",
            GRAY + "You have lost #fd5e5ex" + Placeholders.GENERIC_AMOUNT + " " + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + ".");


    public static final LangKey COMMAND_KEY_SET_USAGE = LangKey.of("Command.Key.Set.Usage",
            "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_SET_DESC = LangKey.of("Command.Key.Set.Desc",
            "Set the number of keys for a player.");
    public static final LangKey COMMAND_KEY_SET_DONE = LangKey.of("Command.Key.Set.Done",
            GRAY + "The number of keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " for player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + " has been changed to " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + ".");
    public static final LangKey COMMAND_KEY_SET_NOTIFY = LangKey.of("Command.Key.Set.Notify",
            GRAY + "The number of your keys of type " + YELLOW + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " has been changed to " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + ".");


    public static final LangKey DUNGEON_ERROR_INVALID = LangKey.of("Dungeon.Error.Invalid",
            RED + "Invalid dungeon!");
    public static final LangKey DUNGEON_ERROR_EXISTS = LangKey.of("Dungeon.Error.Exists",
            RED + "A dungeon with that ID already exists!");


    public static final LangKey DUNGEON_KEY_ERROR_INVALID = LangKey.of("Dungeon.Key.Error.Invalid",
            RED + "Invalid key!");
    public static final LangKey DUNGEON_KEY_ERROR_EXISTS = LangKey.of("Dungeon.Key.Error.Exists",
            RED + "A key with that ID already exists!");


    public static final LangKey DUNGEON_OPEN_ERROR_NO_KEY = LangKey.of("Dungeon.Open.Error.NoKey",
            "<! type:\"titles:20:80:20\" !>" +
                    Colors2.RED + BOLD + "\nOops!" +
                    Colors2.GRAY + "\nYou don't have the key for this Dungeon!");
    public static final LangKey DUNGEON_OPEN_ERROR_NO_HOLD_KEY = LangKey.of("Dungeon.Open.Error.NoHoldKey",
            "<! type:\"titles:20:80:20\" !>" +
                    Colors2.RED + BOLD + "\nOops!" +
                    Colors2.GRAY + "\nYou must be holding the key to open the Dungeon!");

    // editor
    public static final LangKey EDITOR_ENTER_DISPLAY_NAME = LangKey.of("Editor.Enter.DisplayName",
            GRAY + "Enter " + GREEN + "[Displayed Name]");
    public static final LangKey EDITOR_GENERIC_ENTER_NAME = LangKey.of("Editor.Generic.Enter.Name",
            GRAY + "Enter " + GREEN + "[Display Name]");
    public static final LangKey EDITOR_ENTER_SCHEMATIC = LangKey.of("Editor.Enter.Schematic",
            GRAY + "Enter " + GREEN + "[Dungeon Schematic]");
    public static final LangKey EDITOR_ACCESS_PSAPI_ENTER_CLASS = LangKey.of("Editor.Enter.Access.PSAPI.Class",
            GRAY + "Enter " + GREEN + "[ProSkillAPI (Fabled) Class]");
    public static final LangKey EDITOR_ACCESS_ENTER_NOT_ACCESS_MESSAGE = LangKey.of("Editor.Enter.Access.Not_Access_Message",
            GRAY + "Enter " + GREEN + "[Not Access Message (Kyori miniMessage)]");
    public static final LangKey EDITOR_DUNGEON_ENTER_HOLOGRAM_TEXT = LangKey.of("Editor.Dungeon.Enter.Hologram.Text",
            GRAY + "Enter " + GREEN + "[Text]");
    public static final LangKey EDITOR_DUNGEON_ENTER_HOLOGRAM_OFFSET = LangKey.of("Editor.Dungeon.Enter.Hologram.Offset",
            GRAY + "Enter " + GREEN + "[Offset Value]");

    public static final LangKey EDITOR_DUNGEON_ENTER_ID = LangKey.of("Editor.Dungeon.Enter.Id",
            GRAY + "Enter " + GREEN + "[Dungeon ID]");
    public static final LangKey EDITOR_DUNGEON_ENTER_KEY_ID = LangKey.of("Editor.Dungeon.Enter.KeyId",
            GRAY + "Enter " + GREEN + "[Key Identifier]");

    public static final LangKey EDITOR_REWARD_ENTER_ID = LangKey.of("Editor.Reward.Enter.Id",
            GRAY + "Enter " + GREEN + "[Reward Identifier]");
    public static final LangKey EDITOR_REWARD_ENTER_CHANCE = LangKey.of("Editor.Reward.Enter.Chance",
            GRAY + "Enter " + GREEN + "[Chance]");
    public static final LangKey EDITOR_REWARD_ENTER_UNI_LIMIT = LangKey.of("Editor.Reward.Enter.UniLimit",
            GRAY + "Enter " + GREEN + "[Min] [Max]");
    public static final LangKey EDITOR_REWARD_ENTER_UNI_AMOUNT = LangKey.of("Editor.Reward.Enter.UniAmount",
            GRAY + "Enter " + GREEN + "[Min] [Max]");
    public static final LangKey EDITOR_REWARD_ERROR_CREATE_EXIST = LangKey.of("Editor.Reward.Error.Create.Exist",
            RED + "A reward with this identifier already exists!");
    public static final LangKey EDITOR_DUNGEON_ERROR_MATERIAL_NOT_FOUND = LangKey.of("Editor.Dungeon.Error.Blocks.Material.Not_Found",
            RED + "Material not found, try again!");
    public static final LangKey EDITOR_DUNGEON_ERROR_ACCESS_PSAPI_CLASS_NOT_FOUND = LangKey.of("Editor.Dungeon.Error.Access.PSAPI.Class.Not_Found",
            RED + "Class not found, try again!");
    public static final LangKey EDITOR_REWARD_ENTER_COMMAND = LangKey.of("Editor.Reward.Enter.Command",
            GRAY + "Enter " + GREEN + "[Command]");

    public static final LangKey EDITOR_EFFECT_ENTER_TYPE = LangKey.of("Editor.Effect.Enter.Type",
            GRAY + "Enter " + GREEN + "[PotionEffect Type]");
    public static final LangKey EDITOR_EFFECT_ENTER_DURATION = LangKey.of("Editor.Effect.Enter.Duration",
            GRAY + "Enter " + GREEN + "[Duration]");
    public static final LangKey EDITOR_EFFECT_ENTER_AMPLIFIER = LangKey.of("Editor.Effect.Enter.Amplifier",
            GRAY + "Enter " + GREEN + "[Amplifier]");

    public static final LangKey EDITOR_DUNGEON_WRITE_CHEST_BLOCK_MATERIAL = LangKey.of("Editor.Dungeon.Enter.Blocks.Material",
            GRAY + "Enter " + GREEN + "[Chest-Block Material]");
    public static final LangKey EDITOR_DUNGEON_WRITE_POSITIVE_VALUE = LangKey.of("Editor.Dungeon.Write.Positive_Value",
            GREEN + "Write positive value!");
    public static final LangKey EDITOR_DUNGEON_WRITE_VALUE = LangKey.of("Editor.Dungeon.Write.Value",
            GREEN + GRAY + "Enter " + GREEN + "value" + GRAY + "...");

    public static final LangKey EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT = LangKey.of("Messages.Error.Dungeon.Editor.Value.Not_Correct",
            RED + "Value is not correct!");
    public static final LangKey DUNGEON_BACKTRACK_PLAYER_WHEN_CLOSE = LangKey.of("Messages.Dungeon.Backtrack_When_Close",
            RED + "You cannot enter the dungeon while it is closed!");
    public static final LangKey DUNGEON_BACKTRACK_PLAYER_WHEN_NOT_PARTY = LangKey.of("Messages.Dungeon.Backtrack_Not_Party",
            RED + "To enter this dungeon, you need to be " + GREEN + Placeholders.PARTY_SIZE + RED + " players or more" + BOLD + "!");
    public static final LangKey EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_VALID = LangKey.of("Messages.Error.Dungeon.Editor.Schematic.Not_Valid",
            RED + "The schematic you specified does not contain a chest block!");
    public static final LangKey EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_CONTAINS_CHEST = LangKey.of("Messages.Error.Dungeon.Editor.Schematic.Not_Contains_Chest",
            RED + "The schematic you specified has empty Chest Material (" + t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL + ")");
    public static final LangKey Editor_Dungeon_Enter_World = new LangKey("Editor.Dungeon.Enter.World",
            GRAY + "Enter " + GREEN + "world name" + GRAY + "...");
    public static final LangKey Editor_Dungeon_World_Not_Found = new LangKey("Editor.Dungeon.Error.World.Not_Found",
            RED + "World not found!");

    public static final LangKey Editor_Mob_Enter_Create = new LangKey("Editor.Mob.Enter.Create",
            GRAY + "Enter " + GREEN + "unique " + GRAY + "mob " + GREEN + "identifier" + GRAY + "...");
    public static final LangKey Editor_Mob_Enter_Type = new LangKey("Editor.Mob.Enter.Type",
            GRAY + "Enter " + GREEN + "entity type" + GRAY + "...");
    public static final LangKey Editor_Mob_Enter_Attribute = new LangKey("Editor.Mob.Enter.Attribute",
            GRAY + "Enter " + GREEN + "attribute " + GRAY + "and " + GREEN + "value" + GRAY + "...");
    public static final LangKey Editor_Mob_Enter_Id = new LangKey("Editor.Mob.Enter.Id",
            GRAY + "Enter " + GREEN + "mob id " + GRAY + "and" + GREEN + "amount of" + GRAY + "...");
    public static final LangKey Editor_Mob_Enter_Potion = new LangKey("Editor.Mob.Enter.Potion",
            GRAY + "Enter " + GREEN + "potion " + GRAY + "and " + GREEN + "value" + GRAY + "...");
    public static final LangKey Editor_Mov_Enter_Style = new LangKey("Editor.Mob.Enter.Style",
            GRAY + "Enter " + GREEN + "value" + GRAY + "...");
    public static final LangKey Editor_Mob_Error_Exist = new LangKey("Editor.Mob.Error.Exist",
            RED + "Mob already exists!");

    public static final LangKey Editor_Announce_Enter_Create = new LangKey("Editor.Announce.Enter.Create",
            GRAY + "Enter " + GREEN + "unique " + GRAY + "announce " + GREEN + "identifier" + GRAY + "...");
    public static final LangKey Editor_Announce_Error_Exist = new LangKey("Editor.Announce.Error.Exist",
            RED + "Announce already exists!");

    public static final LangKey Editor_Enter_Text = LangKey.of("Editor.Enter.Text",
            GRAY + "Enter " + GREEN + "text " + GRAY + "or " + GREEN + "leave empty" + GRAY + "...");
    public static final LangKey Editor_Enter_Announce_And_Time = LangKey.of("Editor.Announce_And_Time.Enter",
            GRAY + "Enter " + GREEN + "announce id " + GRAY + "and " + GREEN + "time [1,2,..]" + GRAY + "...");
    public static final LangKey Editor_Announce_And_Time_Error = new LangKey("Editor.Announce_And_Time.Error",
            RED + "Announce not found!");
}
