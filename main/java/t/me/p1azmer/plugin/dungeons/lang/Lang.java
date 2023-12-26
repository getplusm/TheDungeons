package t.me.p1azmer.plugin.dungeons.lang;

import org.bukkit.Sound;
import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.EngineLang;
import t.me.p1azmer.plugin.dungeons.Placeholders;

import static t.me.p1azmer.engine.utils.Colors.*;

public class Lang extends EngineLang {

    public static LangKey NOTIFY_SINGLE = LangKey.of("Messages.Notify.Single", "<! type:\"action_bar\" !>#faebf0Dungeon " + Placeholders.DUNGEON_NAME + " #faebf0dungeon will appear in: #ffe4e1" + Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME + " #faebf0seconds!");
    public static LangKey NOTIFY_EVERY = LangKey.of("Messages.Notify.Every", "<! type:\"action_bar\" !>#faebf0Dungeon " + Placeholders.DUNGEON_NAME + " #faebf0dungeon will appear in: #ffe4e1" + Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME + " #faebf0seconds!");
    public static LangKey DUNGEON_SPAWN_NOTIFY = LangKey.of("Messages.Notify.Spawn", """
            <! prefix:"false" !>
                        
            #c71585Attention \u2757
            #fcf2f8At the coordinates: #ffb6ad\u2690 %location_world%, %location_x%, %location_y%, %location_z%
            #fcf2f8A dungeon has appeared #ffb6ad%dungeon_name%
            #fcf2f8In order to open it you need
            #ffe4e0\u25aa #c71585%dungeon_key_names%
                        
            """
    );


    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc", "Open the dungeon and key editor.");

    public static final LangKey COMMAND_DROP_USAGE = LangKey.of("Command.Drop.Usage", "<dungeon_id> <world> <x> <y> <z>");
    public static final LangKey COMMAND_DROP_DESC = LangKey.of("Command.Drop.Desc", "Place a dungeon at the specified location in the world.");
    public static final LangKey COMMAND_DROP_DONE = LangKey.of("Command.Drop.Done", GRAY + "Dungeon " + YELLOW + Placeholders.DUNGEON_NAME + GRAY + " has been placed at coordinates " + YELLOW + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z + GRAY + " in world " + YELLOW + Placeholders.LOCATION_WORLD + GRAY + ".");
    public static final LangKey COMMAND_DROP_ERROR = LangKey.of("Command.Drop.Error", RED + "An error occurred while trying to spawn a " + LIGHT_PURPLE + Placeholders.DUNGEON_NAME + RED + "! See the error in the console");

    public static final LangKey COMMAND_DEL_USAGE = LangKey.of("Command.Delete.Usage", "<dungeon_id>");
    public static final LangKey COMMAND_DEL_DESC = LangKey.of("Command.Delete.Desc", "Despawn a specific or all dungeon.");
    public static final LangKey COMMAND_DEL_DONE = LangKey.of("Command.Delete.Done", GRAY + "Dungeon " + YELLOW + Placeholders.DUNGEON_NAME + GRAY + " has been despawned");
    public static final LangKey COMMAND_KEY_DESC = LangKey.of("Command.Key.Desc", "Manage player keys.");
    public static final LangKey COMMAND_KEY_USAGE = LangKey.of("Command.Key.Usage", "[help]");

    public static final LangKey COMMAND_KEY_GIVE_USAGE = LangKey.of("Command.Key.Give.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_DESC = LangKey.of("Command.Key.Give.Desc", "Give a key to a player.");
    public static final LangKey COMMAND_KEY_GIVE_DONE = LangKey.of("Command.Key.Give.Done", GRAY + "Given " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + " to player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + ".");
    public static final LangKey COMMAND_KEY_GIVE_NOTIFY = LangKey.of("Command.Key.Give.Notify", GRAY + "You have received " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + "!");

    public static final LangKey COMMAND_KEY_GIVE_ALL_USAGE = LangKey.of("Command.Key.GiveAll.Usage", "<key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DESC = LangKey.of("Command.Key.GiveAll.Desc", "Give a key to all online players.");
    public static final LangKey COMMAND_KEY_GIVE_ALL_DONE = LangKey.of("Command.Key.GiveAll.Done", GRAY + "Given " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + " to all players.");

    public static final LangKey COMMAND_KEY_TAKE_USAGE = LangKey.of("Command.Key.Take.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_TAKE_DESC = LangKey.of("Command.Key.Take.Desc", "Take a key from a player.");
    public static final LangKey COMMAND_KEY_TAKE_DONE = LangKey.of("Command.Key.Take.Done", GRAY + "Taken " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + " keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + " from player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + ".");
    public static final LangKey COMMAND_KEY_TAKE_NOTIFY = LangKey.of("Command.Key.Take.Notify", GRAY + "You have lost #fd5e5ex" + Placeholders.GENERIC_AMOUNT + " " + Placeholders.KEY_NAME + GRAY + ".");

    public static final LangKey COMMAND_KEY_SET_USAGE = LangKey.of("Command.Key.Set.Usage", "<player> <key> <amount> [-s]");
    public static final LangKey COMMAND_KEY_SET_DESC = LangKey.of("Command.Key.Set.Desc", "Set the number of keys for a player.");
    public static final LangKey COMMAND_KEY_SET_DONE = LangKey.of("Command.Key.Set.Done", GRAY + "The number of keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + " for player " + YELLOW + Placeholders.PLAYER_NAME + GRAY + " has been changed to " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + ".");
    public static final LangKey COMMAND_KEY_SET_NOTIFY = LangKey.of("Command.Key.Set.Notify", GRAY + "The number of your keys of type " + YELLOW + Placeholders.KEY_NAME + GRAY + " has been changed to " + YELLOW + "x" + Placeholders.GENERIC_AMOUNT + GRAY + ".");

    public static final LangKey DUNGEON_ERROR_INVALID = LangKey.of("Dungeon.Error.Invalid", RED + "Invalid dungeon!");
    public static final LangKey DUNGEON_ERROR_EXISTS = LangKey.of("Dungeon.Error.Exists", RED + "A dungeon with that ID already exists!");

    public static final LangKey DUNGEON_KEY_ERROR_INVALID = LangKey.of("Dungeon.Key.Error.Invalid", RED + "Invalid key!");
    public static final LangKey DUNGEON_KEY_ERROR_EXISTS = LangKey.of("Dungeon.Key.Error.Exists", RED + "A key with that ID already exists!");


    public static final LangKey DUNGEON_OPEN_ERROR_NO_KEY = LangKey.of("Dungeon.Open.Error.NoKey",
            "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
                    "\n#fd5e5e&lOops!" +
                    "\n#d4d9d8You don't have the key for this Dungeon!");
    public static final LangKey DUNGEON_OPEN_ERROR_NO_HOLD_KEY = LangKey.of("Dungeon.Open.Error.NoHoldKey",
            "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
                    "\n#fd5e5e&lOops!" +
                    "\n#d4d9d8You must be holding the key to open the Dungeon!");


    // editor
    public static final LangKey EDITOR_GENERIC_ENTER_CURRENCY = LangKey.of("Editor.Generic.Enter.Currency", "&7Enter &a[Currency]");
    public static final LangKey EDITOR_GENERIC_ENTER_NAME = LangKey.of("Editor.Generic.Enter.Name", "&7Enter &a[Display Name]");
    public static final LangKey EDITOR_GENERIC_ENTER_DESCRIPTION = LangKey.of("Editor.Generic.Enter.Description", "&7Enter &a[Description]");
    public static final LangKey EDITOR_GENERIC_ENTER_COMMAND = LangKey.of("Editor.Generic.Enter.Command", "&7Enter &a[Command]");
    public static final LangKey EDITOR_GENERIC_ENTER_NUMBER = LangKey.of("Editor.Generic.Enter.Number", "&7Enter &a[Number]");
    public static final LangKey EDITOR_GENERIC_ENTER_PERCENT = LangKey.of("Editor.Generic.Enter.Percent", "&7Enter &a[Percent Amount]");
    public static final LangKey EDITOR_GENERIC_ENTER_SECONDS = LangKey.of("Editor.Generic.Enter.Seconds", "&7Enter &a[Seconds Amount]");
    public static final LangKey EDITOR_ENTER_DISPLAY_NAME = LangKey.of("Editor.Enter.DisplayName", GRAY + "Enter " + GREEN + "[Displayed Name]");
    public static final LangKey EDITOR_ENTER_SCHEMATIC = LangKey.of("Editor.Enter.Schematic", GRAY + "Enter " + GREEN + "[Dungeon Schematic]");
    public static final LangKey EDITOR_DUNGEON_ENTER_HOLOGRAM_TEXT = LangKey.of("Editor.Dungeon.Enter.Hologram.Text", GRAY + "Enter " + GREEN + "[Text]");
    public static final LangKey EDITOR_DUNGEON_ENTER_HOLOGRAM_OFFSET = LangKey.of("Editor.Crate.Enter.Hologram.Offset", GRAY + "Enter " + GREEN + "[Offset Value]");
    public static final LangKey EDITOR_DUNGEON_ENTER_MINIMAL_ONLINE = LangKey.of("Editor.Crate.Enter.Minimal_Online", GRAY + "Enter " + GREEN + "[Online Value]");

    public static final LangKey EDITOR_DUNGEON_ENTER_ID = LangKey.of("Editor.Dungeon.Enter.Id", GRAY + "Enter " + GREEN + "[Dungeon ID]");
    public static final LangKey EDITOR_DUNGEON_ENTER_KEY_ID = LangKey.of("Editor.Dungeon.Enter.KeyId", GRAY + "Enter " + GREEN + "[Key Identifier]");

    public static final LangKey EDITOR_REWARD_ENTER_ID = LangKey.of("Editor.Reward.Enter.Id", GRAY + "Enter " + GREEN + "[Reward Identifier]");
    public static final LangKey EDITOR_REWARD_ENTER_CHANCE = LangKey.of("Editor.Reward.Enter.Chance", GRAY + "Enter " + GREEN + "[Chance]");
    public static final LangKey EDITOR_REWARD_ENTER_MAX_AMOUNT = LangKey.of("Editor.Reward.Enter.Max.Amount", GRAY + "Enter " + GREEN + "[Maximal Limit]");
    public static final LangKey EDITOR_REWARD_ENTER_MIN_AMOUNT = LangKey.of("Editor.Reward.Enter.Min.Amount", GRAY + "Enter " + GREEN + "[Minimal Limit]");
    public static final LangKey EDITOR_REWARD_ERROR_CREATE_EXIST = LangKey.of("Editor.Reward.Error.Create.Exist", RED + "A reward with this identifier already exists!");
    public static final LangKey EDITOR_REWARD_ERROR_LIMIT_MAX = LangKey.of("Editor.Reward.Error.Limit.Max_Equals_Min", RED + "The maximum quantity cannot be less than or equal to less");
    public static final LangKey EDITOR_REWARD_ERROR_LIMIT_MIN = LangKey.of("Editor.Reward.Error.Limit.Min_Equals_Max", RED + "The minimum quantity cannot be higher than the maximum");
    public static final LangKey EDITOR_DUNGEON_ERROR_MATERIAL_NOT_FOUND = LangKey.of("Editor.Dungeon.Error.Blocks.Material.Not_Found", RED + "Material not found, try again!");

    public static final LangKey EDITOR_DUNGEON_ENTER_FLAG = LangKey.of("Editor.Dungeon.Enter.Region.Flag", GRAY + "Enter " + GREEN + "[Flag allow/deny]");

    public static final LangKey EDITOR_EFFECT_ENTER_TYPE = LangKey.of("Editor.Effect.Enter.Type", GRAY + "Enter " + GREEN + "[PotionEffect Type]");
    public static final LangKey EDITOR_EFFECT_ENTER_DURATION = LangKey.of("Editor.Effect.Enter.Duration", GRAY + "Enter " + GREEN + "[Duration]");
    public static final LangKey EDITOR_EFFECT_ENTER_AMPLIFIER = LangKey.of("Editor.Effect.Enter.Amplifier", GRAY + "Enter " + GREEN + "[Amplifier]");

    public static final LangKey EDITOR_DUNGEON_WRITE_BLOCKS_AMOUNT = LangKey.of("Editor.Dungeon.Enter.Blocks.Amount", GRAY + "Enter " + GREEN + "[Blocks Amount]");
    public static final LangKey EDITOR_DUNGEON_WRITE_CHEST_BLOCK_MATERIAL = LangKey.of("Editor.Dungeon.Enter.Blocks.Material", GRAY + "Enter " + GREEN + "[Chest-Block Material]");
    public static final LangKey EDITOR_DUNGEON_WRITE_POSITIVE_VALUE = LangKey.of("Editor.Dungeon.Write.Positive_Value", GREEN + "Write positive value!");
    public static final LangKey EDITOR_DUNGEON_WRITE_VALUE = LangKey.of("Editor.Dungeon.Write.Value", GREEN + "&7Enter &avalue&7...");

    public static final LangKey ERROR_GENERATOR_LOCATION_NOT_FOUND = LangKey.of("Messages.Error.Generator.Location.Not_Found", RED + "Location cannot generate for '%dungeon_id%'!");
    public static final LangKey ERROR_RANDOM_SPAWN = LangKey.of("Messages.Error.Dungeon.Spawn.Random", RED + "Dungeon cannot spawn, see error in console!");
    public static final LangKey ERROR_DUNGEON_LOCATION_IS_EMPTY = LangKey.of("Messages.Error.Dungeon.Location.Empty", RED + "The dungeon location is empty or has not been created");
    public static final LangKey EDITOR_DUNGEON_ERROR_VALUE_IS_NOT_CORRECT = LangKey.of("Messages.Error.Dungeon.Editor.Value.Not_Correct", RED + "Value is not correct!");
    public static final LangKey DUNGEON_BACKTRACK_PLAYER_WHEN_CLOSE = LangKey.of("Messages.Dungeon.Backtrack_When_Close", RED + "You cannot enter the dungeon while it is closed!");
    public static final LangKey DUNGEON_BACKTRACK_PLAYER_WHEN_NOT_PARTY = LangKey.of("Messages.Dungeon.Backtrack_Not_Party", RED + "To enter this dungeon, you need to be " + GREEN + Placeholders.PARTY_SIZE + RED + " players or more" + BOLD + "!");
    public static final LangKey EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_VALID = LangKey.of("Messages.Error.Dungeon.Editor.Schematic.Not_Valid", RED + "The schematic you specified does not contain a chest block!");
    public static final LangKey EDITOR_DUNGEON_ERROR_SCHEMATIC_NOT_CONTAINS_CHEST = LangKey.of("Messages.Error.Dungeon.Editor.Schematic.Not_Contains_Chest", RED + "The schematic you specified has empty Chest Material (" + Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL + ")");
    public static final LangKey Editor_Dungeon_Enter_World = new LangKey("Editor.Dungeon.Enter.World", "&7Enter &aworld name&7...");
    public static final LangKey Editor_Dungeon_World_Not_Found = new LangKey("Editor.Dungeon.Error.World.Not_Found", "&cWorld not found!");

    public static final LangKey Editor_Mob_Enter_Create = new LangKey("Editor.Mob.Enter.Create", "&7Enter &aunique &7mob &aidentifier&7...");
    public static final LangKey Editor_Mob_Enter_Type = new LangKey("Editor.Mob.Enter.Type", "&7Enter &aentity type&7...");
    public static final LangKey Editor_Mob_Enter_Attribute = new LangKey("Editor.Mob.Enter.Attribute", "&7Enter &aattribute &7and &avalue&7...");
    public static final LangKey Editor_Mob_Enter_Id = new LangKey("Editor.Mob.Enter.Id", "&7Enter &amob id &7and&aamount of&7...");
    public static final LangKey Editor_Mob_Enter_Potion = new LangKey("Editor.Mob.Enter.Potion", "&7Enter &apotion &7and &avalue&7...");
    public static final LangKey Editor_Mov_Enter_Style = new LangKey("Editor.Mob.Enter.Style", "&7Enter &avalue&7...");
    public static final LangKey Editor_Mob_Error_Exist = new LangKey("Editor.Mob.Error.Exist", "&cMob already exists!");

    public static final LangKey Editor_Hologram_Text = LangKey.of("Editor.Hologram.Enter.Text", "&7Enter &atext &7or &aleave empty&7...");
}
