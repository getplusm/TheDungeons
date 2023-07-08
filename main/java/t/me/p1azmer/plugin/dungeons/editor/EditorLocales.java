package t.me.p1azmer.plugin.dungeons.editor;

import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.Dungeon;

public class EditorLocales extends t.me.p1azmer.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Editor.DungeonEditorType."; // Old version compatibility

    public static final EditorLocale DUNGEON_EDITOR = builder(PREFIX + "EDITOR_DUNGEON")
            .name("Dungeons")
            .text("Create and manage your dungeons here!").breakLine()
            .actionsHeader().action("LMB", "Open").build();
    public static final EditorLocale KEYS_EDITOR = builder(PREFIX + "EDITOR_KEYS")
            .name("Keys")
            .text("Create and manage your keys here!").breakLine()
            .actionsHeader().action("LMB", "Open").build();

    public static final EditorLocale DUNGEON_OBJECT = builder(PREFIX + "DUNGEON_OBJECT")
            .name(Placeholders.DUNGEON_NAME + " &7(ID: &f" + Placeholders.DUNGEON_ID + "&7)")
            .actionsHeader()
            .action("LMB", "Configure")
            .action("RMB+SHIFT", "Delete " + RED + "(No Undo)").build();

    public static final EditorLocale DUNGEON_CREATE = builder(PREFIX + "DUNGEON_CREATE")
            .name("Create Dungeon")
            .text("Create a new dungeon.")
            .actionsHeader().action("LMB", "Create").build();

    public static final EditorLocale DUNGEON_NAME = builder(PREFIX + "DUNGEON_CHANGE_NAME")
            .name("Name")
            .text("Sets the displayed name of the dungeon.", "Used in messages and menus.").breakLine()
            .currentHeader().current("Displayed Name", Placeholders.DUNGEON_NAME).breakLine()
            .actionsHeader().action("LMB", "Change").build();

    public static final EditorLocale DUNGEON_KEYS = builder(PREFIX + "DUNGEON_CHANGE_KEYS")
            .name("Attached Keys")
            .text("Specifies which keys can be used to open this dungeon.").breakLine()
            .currentHeader().current("ID", Placeholders.DUNGEON_KEY_IDS).breakLine()
            .warningHeader().warning("If no keys are set, the dungeon can be opened without them!")
            .warning("If incorrect keys are provided, you won't be able to open the dungeon!").breakLine()
            .actionsHeader().action("LMB", "Attach Key").action("RMB", "Clear List")
            .build();

    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_OPEN = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_OPEN")
            .name("Hologram - Stage=Opening")
            .text("Sets the hologram text when the dungeon is in the opening stage.").breakLine()
            .noteHeader()
            .action("You can specify the time until the dungeon opens", "")
            .action("using the placeholder", "dungeon_open_in")
            .breakLine()
            .current("Opening Text", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_OPEN.toString()).breakLine()
            .actionsHeader().action("LMB", "Add Text").action("RMB+SHIFT", "Clear")
            .build();
    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_CLOSE = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_CLOSE")
            .name("Hologram - Stage=Closure")
            .text("Sets the hologram text when the dungeon is open.").breakLine()
            .noteHeader()
            .action("You can specify the time until the dungeon closes", "")
            .action("using the placeholder", "dungeon_hologram_text_close")
            .breakLine()
            .current("Closure Text", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_CLOSE).breakLine()
            .actionsHeader().action("LMB", "Add Text").action("RMB+SHIFT", "Clear")
            .build();

    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_WAIT = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_WAIT")
            .name("Hologram - Stage=Waiting")
            .text("Sets the hologram text when the dungeon is in the waiting stage for opening (CLICK opening mode).").breakLine()
            .current("Waiting Text", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_WAIT).breakLine()
            .actionsHeader().action("LMB", "Add Text").action("RMB+SHIFT", "Clear")
            .build();

    public static final EditorLocale DUNGEON_SCHEMATIC = builder(PREFIX + "DUNGEON_CHANGE_SCHEMATIC")
            .name("Schematics")
            .text("List of schematics that will be used").breakLine()
            .warningHeader().warning("The schematic must contain the block specified in the configuration.").breakLine()
            .warningHeader().warning("If the list is " + RED + "EMPTY" + GRAY + ", the dungeon won't work!").breakLine()
            .currentHeader().current("List", "").text(Placeholders.DUNGEON_SCHEMATICS)
            .breakLine()
            .actionsHeader().action("LMB", "Add").action("RMB+SHIFT", "Clear").build();

    public static final EditorLocale DUNGEON_OPEN_TYPE = builder(PREFIX + "DUNGEON_CHANGE_OPEN_TYPE")
            .name("Opening Type")
            .text("Sets the type of opening for the dungeon", Dungeon.OpenType.CLICK.name() + " - Opens the dungeon on click", Dungeon.OpenType.TIMER.name() + " - Opens the dungeon based on a timer").breakLine()
            .currentHeader().current("", Placeholders.DUNGEON_OPEN_TYPE).breakLine()
            .actionsHeader().action("LMB", "Change").build();

    public static final EditorLocale DUNGEON_REWARDS = builder(PREFIX + "DUNGEON_CHANGE_REWARDS")
            .name("Rewards")
            .text("Create and manage your rewards here!").breakLine()
            .actionsHeader().action("LMB", "Open")
            .build();

    public static final EditorLocale REWARD_OBJECT = builder(PREFIX + "REWARD_OBJECT")
            .name(Placeholders.REWARD_NAME + " &7(ID: &f" + Placeholders.REWARD_ID + "&7)")
            .text("Chance: &f" + Placeholders.REWARD_CHANCE + "%")
            .actionsHeader().action("LMB", "Configure")
            .action("LMB+SHIFT", "Move Forward").action("RMB+SHIFT", "Move Backward")
            .action("[Q/Drop] key", "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale REWARD_CREATE = builder(PREFIX + "REWARD_CREATE")
            .name("Create Reward")
            .text("Create a new reward for the dungeon.").breakLine()
            .actionsHeader().action("LMB", "Manual Creation")
            .action("Insert Item", "Quick Creation")
            .build();


    public static final EditorLocale REWARD_SORT = builder(PREFIX + "REWARD_SORT")
            .name("Reward Sorting")
            .text("Automatically sorts rewards in the specified order.").breakLine()
            .actionsHeader()
            .action("[Slot 1]", "by chance").action("[Slot 2]", "by type")
            .action("[Slot 3]", "by name")
            .build();
    public static final EditorLocale REWARD_NAME = builder(PREFIX + "REWARD_CHANGE_NAME")
            .name("Displayed Name")
            .text("Sets the displayed name of the reward.", "Used in menus and messages.").breakLine()
            .currentHeader().current("Displayed Name", Placeholders.REWARD_NAME).breakLine()
            .warningHeader().warning("This is " + RED + "NOT" + GRAY + " the actual name of the reward!").breakLine()
            .actionsHeader().action("LMB", "Change").action("RMB", "Take from Item")
            .action("LMB+SHIFT", "Set on Item")
            .build();

    public static final EditorLocale REWARD_ITEM = builder(PREFIX + "REWARD_CHANGE_ITEM")
            .name("Item")
            .text("The item that will be added to the chest").breakLine()
            .actionsHeader().action("Insert Item", "Replace Item").action("RMB", "Get a Copy")
            .build();

    public static final EditorLocale REWARD_CHANCE = builder(PREFIX + "REWARD_CHANGE_CHANCE")
            .name("Chance")
            .text("Sets the probability of the reward appearing in the chest.")
            .currentHeader().current("Chance", Placeholders.REWARD_CHANCE + "%").breakLine()
            .actionsHeader().action("LMB", "Change")
            .build();

    public static final EditorLocale REWARD_BROADCAST = builder(PREFIX + "REWARD_CHANGE_BROADCAST")
            .name("Notification")
            .text("Sets whether there will be a notification", "when a player finds this item", "in the dungeon.").breakLine()
            .currentHeader().current("Enabled", Placeholders.REWARD_BROADCAST).breakLine()
            .actionsHeader().action("LMB", "Toggle")
            .build();

    public static final EditorLocale REWARD_LIMITS = builder(PREFIX + "REWARD_CHANGE_LIMITS")
            .name("Item Limits")
            .text("Determines the quantity of the item that will be in the Dungeon").breakLine()
            .currentHeader()
            .current("Maximum", Placeholders.REWARD_MAX_AMOUNT)
            .current("Minimum", Placeholders.REWARD_MIN_AMOUNT).breakLine()
            .actionsHeader()
            .action("LMB", "Set Maximum Quantity")
            .action("RMB", "Set Minimum Quantity")
            .build();

    public static final EditorLocale KEY_OBJECT = builder(PREFIX + "KEY_OBJECT")
            .name(Placeholders.KEY_NAME + GRAY + " (ID: " + BLUE + Placeholders.KEY_ID + GRAY + ")")
            .actionsHeader().action("LMB", "Change")
            .action("RMB+SHIFT", "Delete " + RED + "(No Undo)")
            .build();

    public static final EditorLocale KEY_CREATE = builder(PREFIX + "KEY_CREATE")
            .name("Create Key")
            .text("Create a new key for dungeons.").breakLine()
            .actionsHeader().action("LMB", "Create")
            .build();

    public static final EditorLocale KEY_NAME = builder(PREFIX + "KEY_CHANGE_NAME")
            .name("Displayed Name")
            .text("Sets the displayed name of the key.", "Used in menus and messages.").breakLine()
            .currentHeader().current("Displayed Name", Placeholders.KEY_NAME).breakLine()
            .warningHeader().warning("This is " + RED + "NOT" + GRAY + " the actual name of the key!").breakLine()
            .actionsHeader().action("LMB", "Change")
            .build();

    public static final EditorLocale KEY_ITEM = builder(PREFIX + "KEY_CHANGE_ITEM")
            .name("Item")
            .text("Sets the physical item of the key.").breakLine()
            .noteHeader().notes("Use an item with a predefined name, description, etc.").breakLine()
            .actionsHeader().action("Insert Item", "Replace").action("RMB", "Get")
            .build();
    /**
     * Russian
     */

//    public static final EditorLocale DUNGEON_EDITOR = builder(PREFIX + "EDITOR_DUNGEON")
//            .name("Данжи")
//            .text("Создавайте свои данжи и управляйте ими здесь!").breakLine()
//            .actionsHeader().action("ЛКМ", "Открыть").build();
//
//    public static final EditorLocale KEYS_EDITOR = builder(PREFIX + "EDITOR_KEYS")
//            .name("Ключи")
//            .text("Создавайте свои ключи и управляйте ими здесь!").breakLine()
//            .actionsHeader().action("ЛКМ", "Открыть").build();
//
//    public static final EditorLocale DUNGEON_OBJECT = builder(PREFIX + "DUNGEON_OBJECT")
//            .name(Placeholders.DUNGEON_NAME + " &7(ID: &f" + Placeholders.DUNGEON_ID + "&7)")
//            .actionsHeader()
//            .action("ЛКМ", "Настроить")
//            .action("ПКМ+ШИФТ", "Удалить " + RED + "(Нет отмены)").build();
//
//    public static final EditorLocale DUNGEON_CREATE = builder(PREFIX + "DUNGEON_CREATE")
//            .name("Создать данж")
//            .text("Создать новый данж.")
//            .actionsHeader().action("ЛКМ", "Создать").build();
//
//    public static final EditorLocale DUNGEON_NAME = builder(PREFIX + "DUNGEON_CHANGE_NAME")
//            .name("Название")
//            .text("Задает отображаемое имя данжа.", "Оно используется в сообщениях и меню.").breakLine()
//            .currentHeader().current("Отображаемое имя", Placeholders.DUNGEON_NAME).breakLine()
//            .actionsHeader().action("ЛКМ", "Изменить").build();
//
//    public static final EditorLocale DUNGEON_KEYS = builder(PREFIX + "DUNGEON_CHANGE_KEYS")
//            .name("Прикрепленные ключи")
//            .text("Устанавливает, какие ключи", "можно использовать для открытия этого данжа.").breakLine()
//            .currentHeader().current("ИД", Placeholders.DUNGEON_KEY_IDS).breakLine()
//            .warningHeader().warning("Если ключи не установлены, данж можно открыть и без них!")
//            .warning("Если предоставлены неверные ключи, вы не сможете открыть данж!").breakLine()
//            .actionsHeader().action("ЛКМ", "Прикрепить ключ").action("ПКМ", "Очистить список")
//            .build();
//
//    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_OPEN = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_OPEN")
//            .name("Голограмма. Стадия=Открытие")
//            .text("Устанавливает текст голограммы", "когда идет стадия ожидания открытия").breakLine()
//            .noteHeader()
//            .action("Вы можете указать время до открытия Данжа", "")
//            .action("Используя плейсхолдер", "dungeon_open_in")
//            .breakLine()
//            .current("Текст открытия", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_OPEN.toString()).breakLine()
//            .actionsHeader().action("ЛКМ", "Добавить текст").action("ПКМ+ШИФТ", "Очистить")
//            .build();
//    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_CLOSE = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_CLOSE")
//            .name("Голограмма. Стадия=Закрытие")
//            .text("Устанавливает текст голограммы", "когда данж открыт").breakLine()
//            .noteHeader()
//            .action("Вы можете указать время до закрытия Данжа", "")
//            .action("Используя плейсхолдер", "dungeon_hologram_text_close")
//            .breakLine()
//            .current("Текст закрытия", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_CLOSE).breakLine()
//            .actionsHeader().action("ЛКМ", "Добавить текст").action("ПКМ+ШИФТ", "Очистить")
//            .build();
//
//    public static final EditorLocale DUNGEON_BLOCK_HOLOGRAM_WAIT = builder(PREFIX + "DUNGEON_CHANGE_BLOCK_HOLOGRAM_WAIT")
//            .name("Голограмма. Стадия=Ожидания")
//            .text("Устанавливает текст голограммы", "когда данж ожидает открытия", "в режиме открытия CLICK").breakLine()
//            .current("Текст ождиания", "").text(Placeholders.DUNGEON_HOLOGRAM_TEXT_CLOSE).breakLine()
//            .actionsHeader().action("ЛКМ", "Добавить текст").action("ПКМ+ШИФТ", "Очистить")
//            .build();
//
//    public static final EditorLocale DUNGEON_SCHEMATIC = builder(PREFIX + "DUNGEON_CHANGE_SCHEMATIC")
//            .name("Схематики")
//            .text("Список схематик,", "который будет появляться").breakLine()
//            .warningHeader().warning("Схематика должна содержать содержать блок, который указан в конфиге").breakLine()
//            .warningHeader().warning("Если список " + RED + "ПУСТ" + GRAY + ", то данж не будет работать!").breakLine()
//            .currentHeader().current("Список", "").text(Placeholders.DUNGEON_SCHEMATICS)
//            .breakLine()
//            .actionsHeader().action("ЛКМ", "Добавить").action("ПКМ+ШИФТ", "Очистить").build();
//
//    public static final EditorLocale DUNGEON_OPEN_TYPE = builder(PREFIX + "DUNGEON_CHANGE_OPEN_TYPE")
//            .name("Тип открытия")
//            .text("Устанавливает тип открытия данжа", Dungeon.OpenType.CLICK.name() + " - Откроет Данж по клику", Dungeon.OpenType.TIMER.name() + " - Откроет Данж по таймеру").breakLine()
//            .currentHeader().current("", Placeholders.DUNGEON_OPEN_TYPE).breakLine()
//            .actionsHeader().action("ЛКМ", "Изменить").build();
//
//    public static final EditorLocale DUNGEON_REWARDS = builder(PREFIX + "DUNGEON_CHANGE_REWARDS")
//            .name("Награды")
//            .text("Создавайте награды и управляйте ими здесь!").breakLine()
//            .actionsHeader().action("ЛКМ", "Открыть")
//            .build();
//
//    public static final EditorLocale REWARD_OBJECT = builder(PREFIX + "REWARD_OBJECT")
//            .name(Placeholders.REWARD_NAME + " &7(ID: &f" + Placeholders.REWARD_ID + "&7)")
//            .text("Шанс: &f" + Placeholders.REWARD_CHANCE + "%")
//            .actionsHeader().action("ЛКМ", "Настройка")
//            .action("ЛКМ+ШИФТ", "Передвинуть вперед").action("ПКМ+ШИФТ", "Передвинуть назад")
//            .action("[Q/Дроп] клавиша", "Удалить " + RED + "(Нет отмены)")
//            .build();
//
//    public static final EditorLocale REWARD_CREATE = builder(PREFIX + "REWARD_CREATE")
//            .name("Создать награду")
//            .text("Создает новую награду для данжа.").breakLine()
//            .actionsHeader().action("ЛКМ", "Ручное создание")
//            .action("Вложите предмет", "Быстрое создание")
//            .build();
//
//    public static final EditorLocale REWARD_SORT = builder(PREFIX + "REWARD_SORT")
//            .name("Сортировка наград")
//            .text("Автоматически сортирует награды в", "указанном порядке.").breakLine()
//            .actionsHeader()
//            .action("[Слот 1]", "по шансу").action("[Слот 2]", "по типу")
//            .action("[Слот 3]", "по имени")
//            .build();
//
//    public static final EditorLocale REWARD_NAME = builder(PREFIX + "REWARD_CHANGE_NAME")
//            .name("Отображаемое название")
//            .text("Устанавливает отображаемое имя награды.", "Оно используется в меню и сообщениях.").breakLine()
//            .currentHeader().current("Отображаемое имя", Placeholders.REWARD_NAME).breakLine()
//            .warningHeader().warning("Это " + RED + "НЕ" + GRAY + " фактическое название награды!").breakLine()
//            .actionsHeader().action("ЛКМ", "Изменить").action("ПКМ", "Взять с предмета")
//            .action("ЛКМ+ШИФТ", "Установить на предмет")
//            .build();
//
//    public static final EditorLocale REWARD_ITEM = builder(PREFIX + "REWARD_CHANGE_ITEM")
//            .name("Предмет")
//            .text("Предмет который будет добавлен в сундук").breakLine()
//            .actionsHeader().action("Вложите предмет", "Заменить предмет").action("ПКМ", "Получить копию")
//            .build();
//
//    public static final EditorLocale REWARD_CHANCE = builder(PREFIX + "REWARD_CHANGE_CHANCE")
//            .name("Шанс")
//            .text("Устанавливает вероятность попадания награды в сундук.")
//            .currentHeader().current("Шанс", Placeholders.REWARD_CHANCE + "%").breakLine()
//            .actionsHeader().action("ЛКМ", "Изменить")
//            .build();
//
//    public static final EditorLocale REWARD_BROADCAST = builder(PREFIX + "REWARD_CHANGE_BROADCAST")
//            .name("Уведомление")
//            .text("Устанавливает, будет ли оповещение о том,", "что игрок нашел этот предмет", "в данже.").breakLine()
//            .currentHeader().current("Включено", Placeholders.REWARD_BROADCAST).breakLine()
//            .actionsHeader().action("ЛКМ", "Переключить")
//            .build();
//
//    public static final EditorLocale REWARD_LIMITS = builder(PREFIX + "REWARD_CHANGE_LIMITS")
//            .name("Количество предмета")
//            .text("Определяет количество предмета которое будет в Данже").breakLine()
//            .currentHeader()
//            .current("Максимальное", Placeholders.REWARD_MAX_AMOUNT)
//            .current("Минимальное", Placeholders.REWARD_MIN_AMOUNT).breakLine()
//            .actionsHeader()
//            .action("ЛКМ", "Установить макс. количество")
//            .action("ПКМ", "Установить мин. количество")
//            .build();
//
//    public static final EditorLocale KEY_OBJECT = builder(PREFIX + "KEY_OBJECT")
//            .name(Placeholders.KEY_NAME + GRAY + " (ID: " + BLUE + Placeholders.KEY_ID + GRAY + ")")
//            .actionsHeader().action("ЛКМ", "Изменить")
//            .action("ПКМ+ШИФТ", "Удалить " + RED + "(Нет отмены)")
//            .build();
//
//    public static final EditorLocale KEY_CREATE = builder(PREFIX + "KEY_CREATE")
//            .name("Создать ключ")
//            .text("Создает новый ключ для данжей.").breakLine()
//            .actionsHeader().action("ЛКМ", "Создать")
//            .build();
//
//    public static final EditorLocale KEY_NAME = builder(PREFIX + "KEY_CHANGE_NAME")
//            .name("Отображаемое название")
//            .text("Задает отображаемое имя ключа.", "Оно используется в меню и сообщениях.").breakLine()
//            .currentHeader().current("отображаемое имя", Placeholders.KEY_NAME).breakLine()
//            .warningHeader().warning("Это " + RED + "НЕ" + GRAY + " фактическое название ключа!").breakLine()
//            .actionsHeader().action("ЛКМ", "Изменить")
//            .build();
//
//    public static final EditorLocale KEY_ITEM = builder(PREFIX + "KEY_CHANGE_ITEM")
//            .name("Предмет")
//            .text("Устанавливает физический предмет ключа.").breakLine()
//            .noteHeader().notes("Используйте предмет с заранее заданным именем, описанием и т.д.").breakLine()
//            .actionsHeader().action("Вложите предмет", "Заменить").action("ПКМ", "Получить")
//            .build();
}