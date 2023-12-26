package t.me.p1azmer.plugin.dungeons.editor;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

import static t.me.p1azmer.engine.utils.Colors.*;

public class EditorLocales extends t.me.p1azmer.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Editor.";

    public static final EditorLocale DUNGEON_EDITOR = builder(PREFIX + "Dungeon")
            .name("Dungeons")
            .build();
    public static final EditorLocale KEYS_EDITOR = builder(PREFIX + "Keys")
            .name("Keys")
            .build();
    public static final EditorLocale MOB_EDITOR = builder(PREFIX + "Mobs")
            .name("Mob Editor")
            .build();

    // dungeon

    public static final EditorLocale DUNGEON_OBJECT = builder(PREFIX + "Dungeon.Object")
            .name(Placeholders.DUNGEON_NAME + " &7(ID: &f" + Placeholders.DUNGEON_ID + "&7)")
            .click(LMB, "Configure")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale DUNGEON_CREATE = builder(PREFIX + "Dungeon.Create")
            .name("New Dungeon")
            .build();

    public static final EditorLocale DUNGEON_NAME = builder(PREFIX + "Dungeon.Change.Name")
            .name("Name")
            .text("Sets the displayed name of the dungeon", "Used in messages and menus")
            .emptyLine()
            .currentHeader()
            .current("Displayed Name", Placeholders.DUNGEON_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();

    public static final EditorLocale DUNGEON_KEYS = builder(PREFIX + "Dungeon.Change.Keys")
            .name("Attached Keys")
            .text("Specifies which keys can be used to open this dungeon.").emptyLine()
            .currentHeader()
            .current("ID List", Placeholders.DUNGEON_KEY_IDS)
            .emptyLine()
            .warningHeader().warning("If no keys are set, the dungeon can be opened without them!")
            .warning("If incorrect keys are provided, you won't be able to open the dungeon!")
            .emptyLine()
            .click(LMB, "Attach Key")
            .click(RMB, "Clear")
            .build();
    public static final EditorLocale DUNGEON_SCHEMATIC = builder(PREFIX + "Dungeon.Change.Schematics")
            .name("Schematics")
            .text("List of schematics that will be used").emptyLine()
            .warningHeader().warning("The schematics must contain the block specified in the configuration.",
                    "If the list is " + RED + "EMPTY" + GRAY + ", the dungeon won't work!")
            .emptyLine()
            .currentHeader()
            .current("List", "").text(Placeholders.DUNGEON_SCHEMATICS)
            .emptyLine()

            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear")
            .build();

    public static final EditorLocale DUNGEON_EFFECTS = builder(PREFIX + "Dungeon.Change.Effects")
            .name("Effects")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_REWARDS = builder(PREFIX + "Dungeon.Change.Rewards")
            .name("Rewards")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale DUNGEON_REGION = builder(PREFIX + "Dungeon.Change.Region")
            .name("Region")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS = builder(PREFIX + "Dungeon.Change.Settings")
            .name("Main Settings")
            .current("Enabled", Placeholders.DUNGEON_SETTINGS_ENABLED)
            .current("Chest material", Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL)
            .current("Minimal online", Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE)
            .current("Open type", Placeholders.DUNGEON_SETTINGS_OPEN_TYPE)
            .current("Underground", Placeholders.DUNGEON_SETTINGS_UNDERGROUND)
            .text("  and more in menu")
            .emptyLine()
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_PARTICLE = builder(PREFIX + "Dungeon.Change.Particles")
            .name("Particles")
            .text(RED + "Coming soon..")
            //.click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_PARTY = builder(PREFIX + "Dungeon.Party")
            .name("Party")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale DUNGEON_REBOOT = builder(PREFIX + "Dungeon.Reboot")
            .name("Reboot the dungeon")
            .text(ORANGE + "Reboot: reset the waiting time,", ORANGE + "delete the dungeon if it is", ORANGE + "spawned and start it again!").emptyLine()
            .click(SHIFT_RMB, "Restart")
            .build();

    public static final EditorLocale DUNGEON_WORLD = builder(PREFIX + "Dungeon.World")
            .name("Dungeon World")
            .text(LIGHT_PURPLE + Placeholders.DUNGEON_WORLD + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    // holograms
    public static final EditorLocale HOLOGRAM_SETTINGS = builder(PREFIX + "Dungeon.Settings.Hologram")
            .name("Hologram Settings")
            .click(LMB, "Navigate")
            .build();

    @NotNull
    public static EditorLocale HOLOGRAM_OBJECT = builder(PREFIX + "Dungeon.Hologram.Object")
            .name(LIGHT_PURPLE + Placeholders.DUNGEON_CHEST_STATE_NAME)
            .text("Sets the message that will be", "over the chest block in this state")
            .emptyLine()
            .currentHeader()
            .textRaw(Placeholders.EDITOR_HOLOGRAM_TEXT)
            .emptyLine()
            .click(LMB, "Add Line")
            .click(RMB, "Remove Line")
            .click(SHIFT_RMB, "Clear " + RED + "(No Undo)")
            .build();
    public static final EditorLocale DUNGEON_HOLOGRAM_Y_OFFSET = builder(PREFIX + "Dungeon.Change.Holograms.Offset.Y")
            .name("Hologram Y Offset")
            .text("Sets the Y offset for", "hologram location")
            .emptyLine()
            .currentHeader()
            .current("Y Offset", Placeholders.DUNGEON_HOLOGRAM_CHEST_OFFSET_Y)
            .emptyLine()
            .click(LMB, "Change")
            .build();

    // settings
    public static final EditorLocale DUNGEON_SETTINGS_ENABLE = builder(PREFIX + "Dungeon.Settings.Enabled")
            .name("Enabled dungeon")
            .text("Sets whether the", "dungeon is enabled or disabled")
            .currentHeader()
            .current("Enabled", Placeholders.DUNGEON_SETTINGS_ENABLED)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_OPEN_TYPE = builder(PREFIX + "Dungeon.Settings.Open_Type")
            .name("Chest Opening Type")
            .text("Sets the type of opening for the dungeon", GREEN + Dungeon.OpenType.CLICK.name() + GRAY + " - Opens the dungeon on click", GREEN + Dungeon.OpenType.TIMER.name() + GRAY + " - Opens the dungeon based on a timer").emptyLine()
            .currentHeader()
            .current("Type", Placeholders.DUNGEON_SETTINGS_OPEN_TYPE).emptyLine()
            .click(LMB, "Change").build();

    public static final EditorLocale DUNGEON_SETTINGS_CHEST_WAIT_TIME = builder(PREFIX + "Dungeon.Settings.Chest.Wait_Type")
            .name("Chest Waiting time")
            .text("Sets the time in seconds to wait before opening the chest")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_CHEST_WAIT_TIME)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_CLOSE_TIME = builder(PREFIX + "Dungeon.Settings.Chest.Close_Type")
            .name("Chest Closing time")
            .text("Sets the time in seconds for how long the active chest will be open")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_CHEST_CLOSE_TIME)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_OPEN_TIME = builder(PREFIX + "Dungeon.Settings.Chest.Open_Type")
            .name("Opening time")
            .text("Sets the time in seconds for the active chest to open")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_CHEST_OPEN_TIME)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_TIMER_REFRESH = builder(PREFIX + "Dungeon.Settings.Timer.Refresh")
            .name("Refreshing time")
            .text("Sets the dungeon reload time.", "That is, this is the frequency of appearance of the dungeon")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_REFRESH)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_LIMIT = builder(PREFIX + "Dungeon.Settings.Chest.Limit")
            .name("Chest block limit")
            .text("Sets a limit on the number of chest blocks")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_CHEST_BLOCK_LIMIT)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_SEPARATE_GUI = builder(PREFIX + "Dungeon.Settings.Separate.Gui")
            .name("Separate chest block gui")
            .text("Sets whether there will be a separate menu", "for each block of the chest or", "one menu for all blocks")
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_SEPARATE_CHEST_BLOCK)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CLICK_TIMER = builder(PREFIX + "Dungeon.Settings.Timer.Click")
            .name("Click Timer")
            .text("Sets whether the timer will be started", "when opening if the", "opening type is click").emptyLine()
            .currentHeader()
            .current("Use", Placeholders.DUNGEON_SETTINGS_CLICK_TIMER).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_BIG_CHEST = builder(PREFIX + "Dungeon.Settings.Chest.Big")
            .name("Big Chest")
            .text("Sets the chest (rewards menu) will be big.", "The large has 54 slots,", "and the small one has 27.").emptyLine()
            .currentHeader()
            .current("Big", Placeholders.DUNGEON_SETTINGS_BIG_CHEST).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_RANDOM_SLOTS = builder(PREFIX + "Dungeon.Settings.Random_Slots")
            .name("Random Slots")
            .text("Sets whether the items in the dungeon", "chest will be in random slots", "or will be filled gradually").emptyLine()
            .currentHeader()
            .current("Random", Placeholders.DUNGEON_SETTINGS_RANDOM_SLOTS).emptyLine()
            .click(LMB, "Change").build();

    public static final EditorLocale DUNGEON_SETTINGS_UNDERGROUND = builder(PREFIX + "Dungeon.Settings.Underground")
            .name("Underground")
            .text("Sets whether the dungeon", "will spawn underground or", "on the surface").emptyLine()
            .currentHeader()
            .current("Underground", Placeholders.DUNGEON_SETTINGS_UNDERGROUND).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_BLOCK_SEARCH_RADIUS = builder(PREFIX + "Dungeon.Settings.Blocks")
            .name("Blocks Search Radius")
            .text("Sets the radius at which", "chest blocks will be searched").emptyLine()
            .currentHeader()
            .current("Radius", Placeholders.DUNGEON_SETTINGS_BLOCKS_SIZE).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_MINIMAL_ONLINE = builder(PREFIX + "Dungeon.Settings.Minimal_Online")
            .name("Minimal Online")
            .text("Sets the minimum allowed online", "players for spawn").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE).emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_CHEST_MATERIAL = builder(PREFIX + "Dungeon.Settings.Chest.Material")
            .name("Chest Block")
            .text("Sets what material will be used as a chest", "that players will activate and open").emptyLine()
            .currentHeader()
            .current("Material", Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL).emptyLine()

            .click(LMB, "Change")
            .click(DRAG_DROP, "Quick change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_LET_PLAYER_WHEN_CLOSE = builder(PREFIX + "Dungeon.Settings.Let_Players")
            .name("Let players when close")
            .text("Sets whether the player will", "be able to enter the dungeon if it is still closed.", "&cBe careful with this setting,", "&cas if you set a 'false',", "&cthe plugin will push the player 3 blocks back").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE).emptyLine()

            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_USE_ONE_KEY_TO_OPEN_CHEST = builder(PREFIX + "Dungeon.Settings.Chest.One_Key")
            .name("One key for chests")
            .text("Sets whether only one key per chest", "will be used to use it.", "&eIf yes, then the player activates the chest", "&ewill be counted for all subsequent players").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_USE_ONE_KEY_FOR_CHEST).emptyLine()

            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_REGION_CLOSE_TIME = builder(PREFIX + "Dungeon.Settings.Region.Close_Time")
            .name("Region close time")
            .text("Sets how many seconds the dungeon will be closed to players.").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_REGION_CLOSE_TIME).emptyLine()

            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_REGION_OPEN_TIME = builder(PREFIX + "Dungeon.Settings.Region.Open_Time")
            .name("Region open time")
            .text("Sets the time in seconds for how long the open dungeon will be available.").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_REGION_OPEN_TIME).emptyLine()

            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_REGION_WAIT_TIME = builder(PREFIX + "Dungeon.Settings.Region.Wait_Time")
            .name("Region wait time")
            .text("Sets the waiting time before", "the dungeon appears in the world").emptyLine()
            .currentHeader()
            .current("Value", Placeholders.DUNGEON_SETTINGS_REGION_WAIT_TIME).emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_COMMAND_CLOSE = builder(PREFIX + "Dungeon.Settings.Command.Close")
            .name("Dungeon Close Commands")
            .text("Sets the commands that will be", "executed when the dungeon is closed").emptyLine()
            .currentHeader()
            .text(Placeholders.DUNGEON_SETTINGS_CLOSE_COMMANDS)
            .emptyLine()
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_COMMAND_OPEN = builder(PREFIX + "Dungeon.Settings.Command.Open")
            .name("Dungeon Open Commands")
            .text("Sets the commands that will be", "executed when the dungeon is opened").emptyLine()
            .currentHeader()
            .text(Placeholders.DUNGEON_SETTINGS_OPEN_COMMANDS)
            .emptyLine()
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_MOBS = builder(PREFIX + "Dungeon.Settings.Mobs")
            .name("Dungeon Mobs")
            .text("Sets which mobs will be spawned in", "the dungeon when the dungeon appears")
            .currentHeader()
            .text(GRAY + "(Mob Id: Mob count)", "", Placeholders.DUNGEON_SETTINGS_MOBS)
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear")
            .build();
    // effect

    public static final EditorLocale EFFECT_OBJECT = builder(PREFIX + "Dungeon.Change.EFFECT_OBJECT")
            .name(Placeholders.EFFECT_NAME)
            .currentHeader()
            .current("Duration", Placeholders.EFFECT_DURATION)
            .current("Amplifier", Placeholders.EFFECT_AMPLIFIER)
            .emptyLine()

            .click(LMB, "Change duration")
            .click(RMB, "Change amplifier")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale EFFECT_CREATE = builder(PREFIX + "EFFECT_CREATE")
            .name("Create Effect")
            .text("Create a new potion effect for the dungeon.").emptyLine()
            .click(LMB, "Create")
            .build();

    public static final EditorLocale EFFECT_SORT = builder(PREFIX + "EFFECT_SORT")
            .name("Effects Sorting")
            .text("Automatically sorts effects in the specified order.").emptyLine()

            .click("[Slot 1]", "by duration").click("[Slot 2]", "by amplifier")
            .click("[Slot 3]", "by name")
            .build();

    // region
    public static final EditorLocale REGION_NAME = builder(PREFIX + "Region.NAME")
            .name("Name")
            .currentHeader()
            .current("", Placeholders.REGION_NAME)

            .click(LMB, "Change")
            .emptyLine()
            .build();

    public static final EditorLocale REGION_ENABLED = builder(PREFIX + "Region.ENABLED")
            .name("Region status")
            .currentHeader()
            .current("Enabled", Placeholders.REGION_ENABLED)

            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_IGNORE_AIR_BLOCKS = builder(PREFIX + "Region.IGNORE_AIR_BLOCKS")
            .name("Region Schematic")
            .notes("Sets whether the dungeon", "schematic will replace air blocks or", "fill without air around the schematic")
            .currentHeader()
            .current("Ignore air blocks", Placeholders.REGION_IGNORE_AIR_BLOCKS)

            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_RADIUS = builder(PREFIX + "Region.RADIUS")
            .name("Radius")
            .currentHeader()
            .current("", Placeholders.REGION_RADIUS)

            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_FLAGS = builder(PREFIX + "Region.FLAGS")
            .name("Flags")
            .currentHeader()
            .current("List", "").text(Placeholders.REGION_FLAGS)
            .emptyLine()
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear").build();

    // rewards
    public static final EditorLocale REWARD_OBJECT = builder(PREFIX + "Reward.OBJECT")
            .name(Placeholders.REWARD_NAME + " &7(ID: &f" + Placeholders.REWARD_ID + "&7)")
            .text("Chance: &f" + Placeholders.REWARD_CHANCE + "%")
            .click(LMB, "Configure")
            .click(SHIFT_LMB, "Move Forward").click(SHIFT_RMB, "Move Backward")
            .click(DROP_KEY, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale REWARD_CREATE = builder(PREFIX + "Reward.CREATE")
            .name("Create Reward")
            .text("Create a new reward for the dungeon.").emptyLine()
            .click(LMB, "Manual Creation")
            .click(DRAG_DROP, "Quick Creation")
            .build();


    public static final EditorLocale REWARD_SORT = builder(PREFIX + "Reward.SORT")
            .name("Reward Sorting")
            .text("Automatically sorts rewards in the specified order.").emptyLine()

            .click("[Slot 1]", "by chance").click("[Slot 2]", "by type")
            .click("[Slot 3]", "by name")
            .build();
    public static final EditorLocale REWARD_NAME = builder(PREFIX + "Reward.Change.NAME")
            .name("Displayed Name")
            .text("Sets the displayed name of the reward.", "Used in menus and messages.").emptyLine()
            .currentHeader()
            .current("Displayed Name", Placeholders.REWARD_NAME).emptyLine()
            .warningHeader().warning("This is " + RED + "NOT" + GRAY + " the actual name of the reward!").emptyLine()
            .click(LMB, "Change").click(RMB, "Take from Item")
            .click(SHIFT_LMB, "Set on Item")
            .build();

    public static final EditorLocale REWARD_ITEM = builder(PREFIX + "Reward.Change.ITEM")
            .name("Item")
            .text("The item that will be added to the chest")
            .emptyLine()
            .click(DRAG_DROP, "Replace Item")
            .click(RMB, "Get a Copy")
            .build();

    public static final EditorLocale REWARD_CHANCE = builder(PREFIX + "Reward.Change.CHANCE")
            .name("Chance")
            .text("Sets the probability of the reward appearing in the chest.")
            .currentHeader()
            .current("Chance", Placeholders.REWARD_CHANCE + "%").emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale REWARD_LIMITS = builder(PREFIX + "Reward.Change.LIMITS")
            .name("Item Limits")
            .text("Determines the quantity of the item that will be in the Dungeon").emptyLine()
            .currentHeader()
            .current("Maximum", Placeholders.REWARD_MAX_AMOUNT)
            .current("Minimum", Placeholders.REWARD_MIN_AMOUNT).emptyLine()

            .click(LMB, "Set Maximum Quantity")
            .click(RMB, "Set Minimum Quantity")
            .build();

    // keys

    public static final EditorLocale KEY_OBJECT = builder(PREFIX + "Key.OBJECT")
            .name(Placeholders.KEY_NAME + GRAY + " (ID: " + BLUE + Placeholders.KEY_ID + GRAY + ")")
            .click(LMB, "Change")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale KEY_CREATE = builder(PREFIX + "Key.CREATE")
            .name("Create Key")
            .text("Create a new key for dungeons.").emptyLine()
            .click(LMB, "Create")
            .build();

    public static final EditorLocale KEY_NAME = builder(PREFIX + "Key.Change.NAME")
            .name("Displayed Name")
            .text("Sets the displayed name of the key.", "Used in menus and messages.").emptyLine()
            .currentHeader()
            .current("Displayed Name", Placeholders.KEY_NAME).emptyLine()
            .warningHeader().warning("This is " + RED + "NOT" + GRAY + " the actual name of the key!").emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale KEY_ITEM = builder(PREFIX + "Key.Change.ITEM")
            .name("Item")
            .text("Sets the physical item of the key.").emptyLine()
            .noteHeader().notes("Use an item with a predefined name, description, etc.").emptyLine()
            .click(DRAG_DROP, "Replace").click(RMB, "Get")
            .build();

    // mobs
    public static final EditorLocale MOB_OBJECT = builder(PREFIX + "Mob.OBJECT")
            .name(Placeholders.MOB_NAME + GRAY + " (&f" + Placeholders.MOB_ID + GRAY + ")")
            .click(LMB, "Edit").click(RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale MOB_CREATE = builder(PREFIX + "Mob.CREATE")
            .name("Create Mob")
            .build();

    public static final EditorLocale MOB_NAME = builder(PREFIX + "Mob.Change.NAME")
            .name("Display Name")
            .text("Sets mob display name and", "whether or not this name is", "always visible.").emptyLine()
            .currentHeader()
            .current("Name", Placeholders.MOB_NAME)
            .current("Is Visible", Placeholders.MOB_NAME_VISIBLE)

            .click(LMB, "Change Name")
            .click(RMB, "Toggle Visibility")
            .build();

    public static final EditorLocale MOB_ENTITY_TYPE = builder(PREFIX + "Mob.Change.ENTITY_TYPE")
            .name("Entity Type")
            .text("Sets mob entity type.").emptyLine()
            .currentHeader()
            .current("Type", Placeholders.MOB_ENTITY_TYPE)
            .emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale MOB_ATTRIBUTES = builder(PREFIX + "Mob.Change.ATTRIBUTES")
            .name("Mob Attributes")
            .text("Sets mob default attributes.").emptyLine()
            .currentHeader()
            .text(Placeholders.MOB_ATTRIBUTES_BASE)
            .emptyLine()
            .noteHeader().text("Base attribute with zero value means", "that default mob's attribute value", "won't be changed.")
            .emptyLine()

            .click(LMB, "Change")
            .click(SHIFT_LMB, "Clear All")
            .build();

    public static final EditorLocale MOB_POTIONS = builder(PREFIX + "Mob.Change.POTIONS")
            .name("Mob Potions")
            .text("Sets mob default potion effects.").emptyLine()
            .currentHeader()
            .current("Duration", "").text(Placeholders.MOB_POTION_EFFECT_DURATION)
            .current("Value", "").text(Placeholders.MOB_POTION_EFFECT_VALUE)
            .emptyLine()
            .click(LMB, "Change Duration")
            .click(RMB, "Change Value")
            .click(SHIFT_LMB, "Clear All")
            .build();

    public static final EditorLocale MOB_EQUIPMENT = builder(PREFIX + "Mob.Change.EQUIPMENT")
            .name("Mob Equipment")
            .text("Items to be equipped on a mob.").emptyLine()
            .warningHeader().warning("Boots → Legs → Chest → Head → Hand → Off Hand").emptyLine()
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale MOB_STYLES = builder(PREFIX + "Mob.Change.STYLES")
            .name("Mob Styles")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale MOB_SILENT = builder(PREFIX + "Mob.Change.SILENT")
            .name("Mob Silent")
            .currentHeader()
            .current("Silent", Placeholders.MOB_SILENT)
            .click(LMB, "Change")
            .build();

    public static final EditorLocale MOB_STYLE_OBJECT = builder(PREFIX + "Mob.STYLE_OBJECT")
            .name("Style Type: " + GREEN + Placeholders.MOB_STYLE_TYPE)
            .currentHeader()
            .current("Value", Placeholders.MOB_STYLE_VALUE).emptyLine()
            .click(LMB, "Change").click(RMB, "Remove")
            .build();

    public static final EditorLocale MOB_RIDER = builder(PREFIX + "Mob.Change.RIDER")
            .name("Mob Rider")
            .currentHeader()
            .current("Rider", Placeholders.MOB_RIDER_ID)
            .click(LMB, "Change")
            .click(RMB, "Clear")
            .build();

    // party
    public static final EditorLocale PARTY_ENABLED = builder(PREFIX + "Party.Change.Enabled")
            .name("Party Enabled")
            .currentHeader()
            .emptyLine()
            .current("Enabled", Placeholders.PARTY_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale PARTY_SIZE = builder(PREFIX + "Party.Change.Size")
            .name("Party Size")
            .currentHeader()
            .current("Size", Placeholders.PARTY_SIZE)
            .emptyLine()
            .click(LMB, "Change")
            .build();
}