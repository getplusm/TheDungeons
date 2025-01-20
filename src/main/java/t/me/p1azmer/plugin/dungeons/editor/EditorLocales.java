package t.me.p1azmer.plugin.dungeons.editor;

import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.chest.type.OpenType;

import static t.me.p1azmer.engine.utils.Colors.BLUE;
import static t.me.p1azmer.engine.utils.Colors.GREEN;
import static t.me.p1azmer.engine.utils.Colors.LIGHT_PURPLE;
import static t.me.p1azmer.engine.utils.Colors.ORANGE;
import static t.me.p1azmer.engine.utils.Colors.RED;
import static t.me.p1azmer.engine.utils.Colors2.BOLD;
import static t.me.p1azmer.engine.utils.Colors2.GRAY;
import static t.me.p1azmer.engine.utils.Colors2.LIGHT_GRAY;
import static t.me.p1azmer.engine.utils.Colors2.WHITE;
import static t.me.p1azmer.engine.utils.Colors2.YELLOW;

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

    public static final EditorLocale ANNOUNCE_EDITOR = builder(PREFIX + "Announce.Editor")
            .name("Announce Editor")
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
            .text(Placeholders.DUNGEON_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
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
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ENABLED)
            .current("Minimal online", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE)
            .text("  and more in menu")
            .emptyLine()
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_PARTICLE = builder(PREFIX + "Dungeon.Change.Particles")
            .name("Particles")
            .text(RED + "Coming soon..")
            .click(LMB, "Navigate")
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
    // chest block
    public static final EditorLocale CHEST_BLOCK_SETTINGS = builder(PREFIX + "Dungeon.Settings.Chest.Settings")
            .name("Chest Block Settings")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale CHEST_BLOCK_STATE_OBJECT = builder(PREFIX + "Dungeon.Settings.Chest.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.chest.Placeholders.DUNGEON_CHEST_STATE_NAME)
            .text("Sets the duration of", "this state in seconds")
            .emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.dungeon.chest.Placeholders.EDITOR_STATE_TIME + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_USE_ONE_KEY_TO_OPEN_CHEST = builder(PREFIX + "Dungeon.Settings.Chest.One_Key")
            .name("One key for chests")
            .text("Sets whether only one key per chest", "will be used to use it.", "&eIf yes, then the player activates the chest", "&ewill be counted for all subsequent players").emptyLine()
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_USE_ONE_KEY_FOR_CHEST).emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_BIG_CHEST = builder(PREFIX + "Dungeon.Settings.Chest.Menu.Big")
            .name("Big Chest")
            .text("Sets the chest (rewards menu) will be big.", "The large has 54 slots,", "and the small one has 27.").emptyLine()
            .currentHeader()
            .current("Big", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_BIG_CHEST).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_RANDOM_SLOTS = builder(PREFIX + "Dungeon.Settings.Menu.Random_Slots")
            .name("Random Slots")
            .text("Sets whether the items in the dungeon", "chest will be in random slots", "or will be filled gradually").emptyLine()
            .currentHeader()
            .current("Random", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_RANDOM_SLOTS).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_OPEN_TYPE = builder(PREFIX + "Dungeon.Settings.Chest.Open_Type")
            .name("Chest Opening Type")
            .text("Sets the type of opening for the dungeon", GREEN + OpenType.CLICK.name() + GRAY + " - Opens the dungeon on click", GREEN + OpenType.TIMER.name() + GRAY + " - Opens the dungeon based on a timer").emptyLine()
            .currentHeader()
            .current("Type", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_OPEN_TYPE).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_SEPARATE_GUI = builder(PREFIX + "Dungeon.Settings.Chest.Menu.Separate")
            .name("Separate chest block gui")
            .text("Sets whether there will be a separate menu", "for each block of the chest or", "one menu for all blocks")
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_SEPARATE_CHEST_BLOCK)
            .emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_LIMIT = builder(PREFIX + "Dungeon.Settings.Chest.Limit")
            .name("Chest block limit")
            .text("Sets a limit on the number of chest blocks")
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_CHEST_BLOCK_LIMIT)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CHEST_MATERIAL = builder(PREFIX + "Dungeon.Settings.Chest.Chest.Material")
            .name("Chest Block")
            .text("Sets what material will be used as a chest", "that players will activate and open").emptyLine()
            .currentHeader()
            .current("Material", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_CHEST_MATERIAL).emptyLine()
            .click(LMB, "Change")
            .click(DRAG_DROP, "Quick change")
            .build();
    // modules
    public static final EditorLocale MODULES_SETTINGS = builder(PREFIX + "Dungeon.Modules.Settings")
            .name("Modules Settings")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale MODULE_OBJECT = builder(PREFIX + "Dungeon.Module.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.modules.Placeholders.MODULE_ID)
            .text(
                    "Sets whether this module is enabled/disabled",
                    "",
                    "The RED item means that the module cannot be",
                    "enabled/disabled based on the",
                    "settings of your generation type"
            )
            .emptyLine()
            .currentHeader()
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.modules.Placeholders.EDITOR_MODULE_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    // commands
    public static final EditorLocale COMMANDS_SETTINGS = builder(PREFIX + "Dungeon.Modules.Commands.Settings")
            .name("Commands Settings")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale COMMANDS_OBJECT = builder(PREFIX + "Dungeon.Modules.Commands.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_NAME)
            .text("Sets the commands for the given stage,",
                    "which will be executed as soon as the dungeon moves to it")
            .emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_COMMANDS)
            .emptyLine()
            .click(LMB, "Add Command")
            .click(SHIFT_RMB, "Clear " + RED + "(No Undo)")
            .build();
    // announce
    public static final EditorLocale ANNOUNCE_SETTINGS = builder(PREFIX + "Dungeon.Modules.Announce.Settings")
            .name("Announce Settings")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale ANNOUNCE_MODULE_OBJECT = builder(PREFIX + "Dungeon.Modules.Announce.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_NAME)
            .text(
                    "Preview:",
                    GRAY + "AnnounceId [Time]",
                    t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_ANNOUNCES)
            .emptyLine()
            .click(LMB, "Add")
            .click(SHIFT_LMB, "Clear " + RED + "(No Undo)")
            .build();
    // holograms
    public static final EditorLocale HOLOGRAM_SETTINGS = builder(PREFIX + "Dungeon.Modules.Holograms.Settings")
            .name("Hologram Settings")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale HOLOGRAM_OBJECT = builder(PREFIX + "Dungeon.Modules.Holograms.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.chest.Placeholders.DUNGEON_CHEST_STATE_NAME)
            .text("Sets the message that will be", "over the chest block in this state")
            .emptyLine()
            .currentHeader()
            .textRaw(Placeholders.EDITOR_HOLOGRAM_TEXT)
            .emptyLine()
            .click(LMB, "Add Line")
            .click(SHIFT_RMB, "Clear " + RED + "(No Undo)")
            .build();
    public static final EditorLocale DUNGEON_HOLOGRAM_Y_OFFSET = builder(PREFIX + "Dungeon.Modules.Holograms.Offset.Y")
            .name("Hologram Y Offset")
            .text("Sets the Y offset for", "hologram location")
            .emptyLine()
            .currentHeader()
            .current("Y Offset", Placeholders.DUNGEON_HOLOGRAM_CHEST_OFFSET_Y)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    // stages
    public static final EditorLocale STAGES_SETTINGS = builder(PREFIX + "Dungeon.Stages.Settings")
            .name("Stage Settings")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale STAGES_OBJECT = builder(PREFIX + "Dungeon.Stages.Object")
            .name(LIGHT_PURPLE + t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_NAME)
            .text(
                    "Sets the time period for",
                    "this stage of the dungeon",
                    "",
                    WHITE + "Description:",
                    t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_DESCRIPTION
            )
            .emptyLine()
            .currentHeader()
            .current("Time", t.me.p1azmer.plugin.dungeons.dungeon.stage.Placeholders.EDITOR_STAGE_TIME)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    // schematics
    public static final EditorLocale SCHEMATICS_SETTINGS = builder(PREFIX + "Dungeon.Modules.Schematics.Settings")
            .name("Schematics Settings")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale SCHEMATICS_LIST = builder(PREFIX + "Dungeon.Modules.Schematics.List")
            .name("List of Schematics")
            .text("Sets the list of schematics to be", "randomly selected for the dungeon.",
                    RED + "You cannot leave the list empty if", RED + "you have this module enabled!")
            .emptyLine()
            .currentHeader()
            .text(Placeholders.SCHEMATICS_LIST)
            .emptyLine()
            .click(LMB, "Add Schematic")
            .click(SHIFT_LMB, "Clear " + RED + "(No Undo)")
            .build();
    public static final EditorLocale SCHEMATICS_IGNORE_AIR = builder(PREFIX + "Dungeon.Modules.Schematics.Ignore_Air")
            .name("Ignore Air Blocks")
            .text("Sets whether the schematic space will replace", "air with blocks from the schematic.", "If set false, the schematic will only fill", "with air without ignoring other blocks")
            .emptyLine()
            .currentHeader()
            .current("Enabled", Placeholders.SCHEMATICS_IGNORE_AIR)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_UNDERGROUND = builder(PREFIX + "Dungeon.Modules.Schematics.Underground")
            .name("Underground")
            .text("Sets whether the dungeon", "will spawn underground or", "on the surface").emptyLine()
            .currentHeader()
            .current("Underground", Placeholders.SCHEMATICS_UNDERGROUND).emptyLine()
            .click(LMB, "Change").build();
    // main settings
    public static final EditorLocale DUNGEON_SETTINGS_ENABLE = builder(PREFIX + "Dungeon.Settings.Enabled")
            .name("Enabled dungeon")
            .text("Sets whether the", "dungeon is enabled or disabled")
            .currentHeader()
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ENABLED)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_CLICK_TIMER = builder(PREFIX + "Dungeon.Settings.Click")
            .name("Click Timer")
            .text("Sets whether the timer will be started", "when opening if the", "opening type is click").emptyLine()
            .currentHeader()
            .current("Use", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_CLICK_TIMER).emptyLine()
            .click(LMB, "Change").build();
    public static final EditorLocale DUNGEON_SETTINGS_MINIMAL_ONLINE = builder(PREFIX + "Dungeon.Settings.Minimal_Online")
            .name("Minimal Online")
            .text("Sets the minimum allowed online", "players for spawn").emptyLine()
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_MINIMAL_ONLINE).emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale DUNGEON_SETTINGS_LET_PLAYER_WHEN_CLOSE = builder(PREFIX + "Dungeon.Settings.Let_Players")
            .name("Let players when close")
            .text("Sets whether the player will", "be able to enter the dungeon if it is still closed.", "&cBe careful with this setting,", "&cas if you set a 'false',", "&cthe plugin will push the player 3 blocks back").emptyLine()
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_LET_PLAYERS_WHEN_CLOSE).emptyLine()

            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_MOBS = builder(PREFIX + "Dungeon.Settings.Mobs")
            .name("Dungeon Mobs")
            .text("Sets which mobs will be spawned in", "the dungeon when the dungeon appears")
            .currentHeader()
            .text(GRAY + "(Mob Id: Mob count)", "", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_MOBS)
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear")
            .build();
    // generation settings
    public static final EditorLocale GENERATION_SETTINGS = builder(PREFIX + "Dungeon.Settings.Generation")
            .name("Generation Settings")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_GENERATION_TYPE = builder(PREFIX + "Dungeon.Settings.Generation.Type")
            .name("Type of Generation")
            .text("Sets what type of generation the dungeon will have")
            .currentHeader()
            .current("Type", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_GENERATION_TYPE)
            .text(
                    "",
                    WHITE + "Static " + GRAY + "-" + LIGHT_GRAY + " the position of this dungeon will always be the same",
                    WHITE + "Dynamic " + GRAY + "-" + LIGHT_GRAY + " the position of the dungeon will always be",
                    LIGHT_GRAY + "random based on the settings of the generator",
                    RED + "(not ready) " + WHITE + " Updatable " + GRAY + "-" + LIGHT_GRAY + " the position of the dungeon will be static,",
                    LIGHT_GRAY + "but its schematics will be created and deleted according to the timer settings"
            )
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_GENERATION_LOCATION = builder(PREFIX + "Dungeon.Settings.Generation.Location")
            .name("Generation Location")
            .text(
                    "Set the location to be used if",
                    "the generation type is " + YELLOW + "STATIC"
            )
            .currentHeader()
            .current("Location", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_GENERATION_LOCATION)
            .emptyLine()
            .click(LMB, "Change to your location")
            .click(SHIFT_RMB, "Clear")
            .build();
    // access settings
    public static final EditorLocale ACCESS_SETTINGS = builder(PREFIX + "Dungeon.Settings.Access")
            .name("Access Settings")
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ACCESS_ENABLED)
            .current("ProSkillAPI Classes", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ACCESS_PSAPI_CLASSES)
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_ACCESS_ENABLED = builder(PREFIX + "Dungeon.Settings.Access.Enabled")
            .name("Enabled")
            .text(
                    "If the parameters are configured",
                    "and the setting is enabled, then",
                    "the plugin will check the data and",
                    "do not allow players to enter the dungeon region",
                    "without the necessary class"
            )
            .currentHeader()
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ACCESS_ENABLED)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_ACCESS_NOT_ACCESS_MESSAGE = builder(PREFIX + "Dungeon.Settings.Access.Not_Access_Message")
            .name("Not Access Message")
            .text("The message about the lack of access")
            .emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ACCESS_NOT_ACCESS_MESSAGE)
            .emptyLine()
            .click(LMB, "Change")
            .build();
    public static final EditorLocale DUNGEON_SETTINGS_ACCESS_PSAPI_CLASSES = builder(PREFIX + "Dungeon.Settings.Access.PSAPI.Class")
            .name("ProSkillAPI (Fabled) Classes")
            .text(
                    "List of classes that are",
                    "necessary to enter the dungeon"
            )
            .emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.dungeon.settings.Placeholders.DUNGEON_SETTINGS_ACCESS_PSAPI_CLASSES)
            .emptyLine()
            .text("(" + WHITE + LMB + GRAY + " to add)")
            .text("(" + WHITE + RMB + GRAY + " to remove all)")
            .build();
    // effect

    public static final EditorLocale EFFECT_OBJECT = builder(PREFIX + "Dungeon.Modules.Effects.Object")
            .name(t.me.p1azmer.plugin.dungeons.dungeon.effect.Placeholders.EFFECT_NAME)
            .currentHeader()
            .current("Duration", t.me.p1azmer.plugin.dungeons.dungeon.effect.Placeholders.EFFECT_DURATION)
            .current("Amplifier", t.me.p1azmer.plugin.dungeons.dungeon.effect.Placeholders.EFFECT_AMPLIFIER)
            .emptyLine()

            .click(LMB, "Change duration")
            .click(RMB, "Change amplifier")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale EFFECT_CREATE = builder(PREFIX + "Dungeon.Modules.Effects.Create")
            .name("Create Effect")
            .text("Create a new potion effect for the dungeon.").emptyLine()
            .click(LMB, "Create")
            .build();

    public static final EditorLocale EFFECT_SORT = builder(PREFIX + "Dungeon.Modules.Effects.Sort")
            .name("Effects Sorting")
            .text("Automatically sorts effects in the specified order.").emptyLine()

            .click("[Slot 1]", "by duration").click("[Slot 2]", "by amplifier")
            .click("[Slot 3]", "by name")
            .build();

    // region
    public static final EditorLocale REGION_NAME = builder(PREFIX + "Dungeon.Modules.Region.Name")
            .name("Name")
            .currentHeader()
            .current("", t.me.p1azmer.plugin.dungeons.dungeon.region.Placeholders.REGION_NAME)
            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_ENABLED = builder(PREFIX + "Dungeon.Modules.Region.Enabled")
            .name("Region status")
            .currentHeader()
            .current("Enabled", t.me.p1azmer.plugin.dungeons.dungeon.region.Placeholders.REGION_ENABLED)
            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_RADIUS = builder(PREFIX + "Dungeon.Modules.Region.Radius")
            .name("Radius")
            .currentHeader()
            .current("", t.me.p1azmer.plugin.dungeons.dungeon.region.Placeholders.REGION_RADIUS)
            .click(LMB, "Change")
            .emptyLine()
            .build();
    public static final EditorLocale REGION_FLAGS = builder(PREFIX + "Dungeon.Modules.Region.Flags")
            .name("Flags")
            .currentHeader()
            .current("List", "")
            .text(t.me.p1azmer.plugin.dungeons.dungeon.region.Placeholders.REGION_FLAGS)
            .emptyLine()
            .click(LMB, "Add")
            .click(SHIFT_RMB, "Clear").build();
    // rewards
    public static final EditorLocale REWARD_OBJECT = builder(PREFIX + "Dungeon.Modules.Reward.Object")
            .name(t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_ID)
            .text("Chance: &f" + t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_CHANCE + "%")
            .emptyLine()
            .click(LMB, "Configure")
            .click(SHIFT_LMB, "Move Forward")
            .click(SHIFT_RMB, "Move Backward")
            .click(DROP_KEY, "Delete " + RED + "(No Undo)")
            .build();
    public static final EditorLocale REWARD_CREATE = builder(PREFIX + "Dungeon.Modules.Reward.Create")
            .name("Create Reward")
            .text("Create a new reward for the dungeon.")
            .emptyLine()
            .click(LMB, "Manual Creation")
            .click(DRAG_DROP, "Quick Creation")
            .build();
    public static final EditorLocale REWARD_SORT = builder(PREFIX + "Dungeon.Modules.Reward.Sort")
            .name("Reward Sorting")
            .text("Automatically sorts rewards in the specified order.").emptyLine()

            .click("[Slot 1]", "by chance").click("[Slot 2]", "by type")
            .click("[Slot 3]", "by name")
            .build();
    public static final EditorLocale REWARD_ITEM = builder(PREFIX + "Dungeon.Modules.Reward.Item")
            .name("Item")
            .text("The item that will be added to the chest")
            .emptyLine()
            .click(DRAG_DROP, "Replace Item")
            .click(RMB, "Get a Copy")
            .build();
    public static final EditorLocale REWARD_CHANCE = builder(PREFIX + "Dungeon.Modules.Reward.Chance")
            .name("Chance")
            .text("Sets the chance of adding this item to the chest")
            .currentHeader()
            .current("Chance", t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_CHANCE + "%" + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REWARD_AMOUNT = builder(PREFIX + "Dungeon.Modules.Reward.Amount")
            .name("Item Amount")
            .text("Sets the amount of the item.",
                    "The final amount will be within these values")
            .currentHeader()
            .current("Min", t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_MIN_AMOUNT + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Max", t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_MAX_AMOUNT + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REWARDS_LIMITS = builder(PREFIX + "Dungeon.Modules.Rewards.Limits")
            .name("Reward Limit")
            .text("Sets limits on the amount of items in the chest.",
                    "The final amount will be within these values")
            .currentHeader()
            .current("Min", t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_LIMIT_MIN + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Max", t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_LIMIT_MAX + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REWARD_COMMANDS = builder(PREFIX + "Reward.Commands")
            .name("Commands")
            .text("All of the following commands will", "be executed from the " + WHITE + "console", "when the chest is opened.")
            .emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.dungeon.reward.Placeholders.REWARD_COMMANDS)
            .emptyLine()
            .text(YELLOW + BOLD + "Placeholders:")
            .current(EngineUtils.PLACEHOLDER_API, "All of them.")
            .current(Placeholders.PLAYER_NAME, "For player name.")
            .emptyLine()
            .text("(" + WHITE + LMB + GRAY + " to add)")
            .text("(" + WHITE + RMB + GRAY + " to remove all)")
            .build();
    // keys
    public static final EditorLocale KEY_OBJECT = builder(PREFIX + "Dungeon.Modules.Key.Object")
            .name(t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME + GRAY + " (ID: " + BLUE + t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_ID + GRAY + ")")
            .click(LMB, "Change")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale KEY_CREATE = builder(PREFIX + "Dungeon.Modules.Key.Create")
            .name("Create Key")
            .text("Create a new key for dungeons.").emptyLine()
            .click(LMB, "Create")
            .build();

    public static final EditorLocale KEY_NAME = builder(PREFIX + "Dungeon.Modules.Key.Name")
            .name("Displayed Name")
            .text("Sets the displayed name of the key.", "Used in menus and messages.").emptyLine()
            .currentHeader()
            .current("Displayed Name", t.me.p1azmer.plugin.dungeons.key.Placeholders.KEY_NAME).emptyLine()
            .warningHeader().warning("This is " + RED + "NOT" + GRAY + " the actual name of the key!").emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale KEY_ITEM = builder(PREFIX + "Dungeon.Modules.Key.Item")
            .name("Item")
            .text("Sets the physical item of the key.").emptyLine()
            .noteHeader().notes("Use an item with a predefined name, description, etc.").emptyLine()
            .click(DRAG_DROP, "Replace").click(RMB, "Get")
            .build();

    // mobs
    public static final EditorLocale MOB_OBJECT = builder(PREFIX + "Dungeon.Modules.Mobs.Object")
            .name(t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_NAME + GRAY + " (&f" + t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_ID + GRAY + ")")
            .click(LMB, "Edit").click(RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale MOB_CREATE = builder(PREFIX + "Dungeon.Modules.Mobs.Create")
            .name("Create Mob")
            .build();

    public static final EditorLocale MOB_NAME = builder(PREFIX + "Dungeon.Modules.Mobs.Name")
            .name("Display Name")
            .text("Sets mob display name and", "whether or not this name is", "always visible.").emptyLine()
            .currentHeader()
            .current("Name", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_NAME)
            .current("Is Visible", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_NAME_VISIBLE)

            .click(LMB, "Change Name")
            .click(RMB, "Toggle Visibility")
            .build();

    public static final EditorLocale MOB_ENTITY_TYPE = builder(PREFIX + "Dungeon.Modules.Mobs.Entity_Type")
            .name("Entity Type")
            .text("Sets mob entity type.").emptyLine()
            .currentHeader()
            .current("Type", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_ENTITY_TYPE)
            .emptyLine()
            .click(LMB, "Change")
            .build();

    public static final EditorLocale MOB_ATTRIBUTES = builder(PREFIX + "Dungeon.Modules.Mobs.Attributes")
            .name("Mob Attributes")
            .text("Sets mob default attributes.").emptyLine()
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_ATTRIBUTES_BASE)
            .emptyLine()
            .noteHeader().text("Base attribute with zero value means", "that default mob's attribute value", "won't be changed.")
            .emptyLine()

            .click(LMB, "Change")
            .click(SHIFT_LMB, "Clear All")
            .build();

    public static final EditorLocale MOB_POTIONS = builder(PREFIX + "Dungeon.Modules.Mobs.Potions")
            .name("Mob Potions")
            .text("Sets mob default potion effects.").emptyLine()
            .currentHeader()
            .current("Duration", "").text(t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_POTION_EFFECT_DURATION)
            .current("Value", "").text(t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_POTION_EFFECT_VALUE)
            .emptyLine()
            .click(LMB, "Change Duration")
            .click(RMB, "Change Value")
            .click(SHIFT_LMB, "Clear All")
            .build();

    public static final EditorLocale MOB_EQUIPMENT = builder(PREFIX + "Dungeon.Modules.Mobs.Equipment")
            .name("Mob Equipment")
            .text("Items to be equipped on a mob.").emptyLine()
            .warningHeader().warning("Boots → Legs → Chest → Head → Hand → Off Hand").emptyLine()
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale MOB_STYLES = builder(PREFIX + "Dungeon.Modules.Mobs.Styles")
            .name("Mob Styles")
            .click(LMB, "Navigate")
            .build();

    public static final EditorLocale MOB_SILENT = builder(PREFIX + "Dungeon.Modules.Mobs.Silent")
            .name("Mob Silent")
            .currentHeader()
            .current("Silent", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_SILENT)
            .click(LMB, "Change")
            .build();

    public static final EditorLocale MOB_STYLE_OBJECT = builder(PREFIX + "Dungeon.Modules.Mobs.Style_Object")
            .name("Style Type: " + GREEN + t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_STYLE_TYPE)
            .currentHeader()
            .current("Value", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_STYLE_VALUE).emptyLine()
            .click(LMB, "Change").click(RMB, "Remove")
            .build();

    public static final EditorLocale MOB_RIDER = builder(PREFIX + "Dungeon.Modules.Mobs.Rider")
            .name("Mob Rider")
            .currentHeader()
            .current("Rider", t.me.p1azmer.plugin.dungeons.mob.Placeholders.MOB_RIDER_ID)
            .click(LMB, "Change")
            .click(RMB, "Clear")
            .build();

    // party
    public static final EditorLocale PARTY_ENABLED = builder(PREFIX + "Dungeon.Modules.Party.Enabled")
            .name("Party Enabled")
            .currentHeader()
            .emptyLine()
            .current("Enabled", Placeholders.PARTY_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale PARTY_SIZE = builder(PREFIX + "Dungeon.Modules.Party.Size")
            .name("Party Size")
            .currentHeader()
            .current("Size", Placeholders.PARTY_SIZE)
            .emptyLine()
            .click(LMB, "Change")
            .build();

    // announce

    public static final EditorLocale ANNOUNCE_OBJECT = builder(PREFIX + "Announce.Object")
            .name(t.me.p1azmer.plugin.dungeons.announce.Placeholders.ANNOUNCE_ID)
            .click(LMB, "Edit")
            .click(RMB, "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale ANNOUNCE_MESSAGES = builder(PREFIX + "Announce.Messages")
            .name("Messages")
            .text("Sets the text of the announcement.")
            .currentHeader()
            .text(t.me.p1azmer.plugin.dungeons.announce.Placeholders.ANNOUNCE_MESSAGES)
            .emptyLine()
            .click(LMB, "Add Line")
            .click(RMB, "Clear Lines " + RED + "(No Undo)")
            .build();
    public static final EditorLocale ANNOUNCE_GLOBAL = builder(PREFIX + "Announce.Global")
            .name("Global Announce")
            .text("If false, then the announcement", "will be only in the world set in the dungeon")
            .text(t.me.p1azmer.plugin.dungeons.announce.Placeholders.ANNOUNCE_GLOBAL + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale ANNOUNCE_ICON = builder(PREFIX + "Announce.Icon")
            .name("Icon in List Menu")
            .click(DRAG_DROP, "Replace")
            .build();

    public static final EditorLocale ANNOUNCE_CREATE = builder(PREFIX + "Announce.Create")
            .name("New Announce")
            .build();
}