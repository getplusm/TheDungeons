package t.me.p1azmer.engine.lang;

import t.me.p1azmer.engine.api.lang.LangKey;

public class CoreLang {

    public static final LangKey CORE_COMMAND_USAGE     = new LangKey("Core.Command.Usage", "&7Используйте: &c/%command_label% &6%command_usage%");
    public static final LangKey CORE_COMMAND_HELP_LIST = new LangKey("Core.Command.Help.List",
            "&6&m              &6&l[ &e&l%plugin_name_localized% &7- &e&lКоманды &6&l]&6&m              &7\n" +
                    "&7\n" +
                    "&7\n" +
                    "&6\u25aa &e/%command_label% &6%command_usage% &7- %command_description%\n" +
                    "&7\n");
    public static final LangKey CORE_COMMAND_HELP_DESC   = new LangKey("Core.Command.Help.Desc", "Страница помощи.");
    public static final LangKey CORE_COMMAND_EDITOR_DESC = new LangKey("Core.Command.Editor.Desc", "Меню настройки.");
    public static final LangKey CORE_COMMAND_ABOUT_DESC  = new LangKey("Core.Command.About.Desc", "Чу-чуть информации о плагине.");
    public static final LangKey CORE_COMMAND_RELOAD_DESC = new LangKey("Core.Command.Reload.Desc", "Перезагрузить плагин.");
    public static final LangKey CORE_COMMAND_RELOAD_DONE = new LangKey("Core.Command.Reload.Done", "Успешно перезагружено!");

    public static final LangKey TIME_DAY  = new LangKey("Time.Day", "%s%д.");
    public static final LangKey TIME_HOUR = new LangKey("Time.Hour", "%s%ч.");
    public static final LangKey TIME_MIN = new LangKey("Time.Min", "%s%мин.");
    public static final LangKey TIME_SEC = new LangKey("Time.Sec", "%s%сек.");
    public static final LangKey OTHER_FREE  = new LangKey("Other.Free", "&6Бесплатно");
    public static final LangKey OTHER_YES = new LangKey("Other.Yes", "&aДа");
    public static final LangKey OTHER_NO   = new LangKey("Other.No", "&cНет");
    public static final LangKey OTHER_ANY   = new LangKey("Other.Any", "Любой");
    public static final LangKey OTHER_NONE     = new LangKey("Other.None", "Никто");
    public static final LangKey OTHER_NEVER     = new LangKey("Other.Never", "Никогда");
    public static final LangKey OTHER_ONE_TIMED = new LangKey("Other.OneTimed", "Одноразовый");
    public static final LangKey OTHER_UNLIMITED = new LangKey("Other.Unlimited", "Безлимитный");
    public static final LangKey OTHER_INFINITY  = new LangKey("Other.Infinity", "\u221e");

    public static final LangKey ERROR_PLAYER_INVALID = new LangKey("Error.Player.Invalid", "&cИгрок не найден.");
    public static final LangKey ERROR_WORLD_INVALID   = new LangKey("Error.World.Invalid", "&cМир не найден.");
    public static final LangKey ERROR_NUMBER_INVALID  = new LangKey("Error.Number.Invalid", "&7%num% &cнедействительный номер.");
    public static final LangKey ERROR_PERMISSION_DENY = new LangKey("Error.Permission.Deny", "&cТебе низя!");
    public static final LangKey ERROR_ITEM_INVALID = new LangKey("Error.Item.Invalid", "&cВы должны держать предмет!");
    public static final LangKey ERROR_TYPE_INVALID   = new LangKey("Error.Type.Invalid", "Недопустимый тип. Доступные: %types%");
    public static final LangKey ERROR_COMMAND_SELF   = new LangKey("Error.Command.Self", "Нельзя использовать на себе.");
    public static final LangKey ERROR_COMMAND_SENDER = new LangKey("Error.Command.Sender", "Консоль, уйди, это для игроков.");
    @Deprecated
    public static final LangKey ERROR_INTERNAL       = new LangKey("Error.Internal", "&cВнутренняя ошибка!");

    public static final LangKey NOT_DONATER       = new LangKey("Player.Not.Donater", "&cВашего ранга недостаточно, чтобы использовать это!");

    public static final LangKey CANT_ADD_ITEM_AND_DROP = LangKey.of("Player.Cant.Add.Item.DungeonReward", "&cВнимание! Ваш инвентарь полон и некоторые предметы лежат возле вас на земле!");

    public static final LangKey EDITOR_TIP_EXIT = LangKey.of("Editor.Tip.Exit", "<! prefix:\"false\" !> <? showText:\"&7Нажми или введи &f#exit\" run_command:\"/#exit\" ?>&bНажми, чтобы выйти из &dРежима настройки</>");
    public static final LangKey EDITOR_TITLE_DONE             = LangKey.of("Editor.Title.Done", "&a&lУспешно!");
    public static final LangKey EDITOR_TITLE_EDIT           = LangKey.of("Editor.Title.Edit", "&a&l< Режим настройки >");
    public static final LangKey EDITOR_TITLE_ERROR          = LangKey.of("Editor.Title.Error", "&c&lОшибка!");
    public static final LangKey EDITOR_ERROR_NUMBER_GENERIC = LangKey.of("Editor.Error.Number.Generic", "&7Неверный номер!");
    public static final LangKey EDITOR_ERROR_NUMBER_NOT_INT = LangKey.of("Editor.Error.Number.NotInt", "&7Номер должен быть &cЧислом&7!");
    public static final LangKey EDITOR_ERROR_ENUM           = LangKey.of("Editor.Error.Enum", "&7Неверный тип! Смотри в чат.");

    public static LangKey EDITOR_WRITE_NAME = LangKey.of("Editor.Tip.Write.Name", "Введите отображаемое название");
    public static LangKey EDITOR_WRITE_INTEGER = LangKey.of("Editor.Tip.Write.Integer", "Введите число");
    public static LangKey EDITOR_CREATE_EXIST = LangKey.of("Editor.Exist", "Такой ID уже существует!");
    public static LangKey EDITOR_CREATE_TIP = LangKey.of("Editor.Tip.Create", "Введите уникальный ID");
}