package t.me.p1azmer.plugin.dungeons.config.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.config.serializers.ComponentSerializer;

import java.io.File;

@Getter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class Lang extends ConfigHolder<Lang> {

    CommandsMessages commandsMessages = new CommandsMessages();
    Messages messages = new Messages();
    MapMessages mapMessages = new MapMessages();


    public Lang(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .build());
    }

    private Lang() {
        this(null);
    }

    @Getter
    @ConfigSerializable
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static final class CommandsMessages {
        Component noPermissionMessage = Component.text("У Вас нет прав");
        Component invalidSyntaxMessage = Component.text("Неправильный формат: для выполнения команды не требуются аргументы");
        Component invalidSenderMessage = Component.text("Команда только для игроков");

        Component successReloaded = Component.text("Конфигурация плагина успешно перезагружена!", NamedTextColor.GREEN);
        Component emptyPreparedMaps = Component.text("Нет подготовленных карт!", NamedTextColor.RED);
        Component alreadyInMap = Component.text("Вы уже находитесь в игре! Чтобы выйти, используйте /race quit", NamedTextColor.RED);
        Component notInMap = Component.text("Вы не в игре!", NamedTextColor.RED);
        Component notExistingMap = Component.text("Такой карты нет!", NamedTextColor.RED);
        Component mapIsNotPrepared = Component.text("Карта еще не готова для игры. Ожидайте!", NamedTextColor.RED);
    }

    @Getter
    @ConfigSerializable
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static final class Messages {
        Component teleportedBackToCheckpoint = Component.text("Вы успешно перемещены на контрольную точку!", NamedTextColor.GREEN);
    }

    @Getter
    @ConfigSerializable
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static final class MapMessages {
        Component emptyWinnerName = Component.text("Никого", NamedTextColor.RED);
        Component winnerNotFoundName = Component.text("Не найден", NamedTextColor.RED);
        Notify notify = new Notify();
        Prepare prepare = new Prepare();
        Finish finish = new Finish();

        @Getter
        @ConfigSerializable
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        public static final class Notify {
            Component mapReadyToPlay = Component.text("%map_name% готова для игры! (%min_players%/%max_players%) Нажмите, чтобы присоединиться к игре", NamedTextColor.GOLD)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/race join %map_name%"));

            Component mapEndingResults = Component.text("Игра на карте %map_name% завершилась. Вот итоги гонки:", NamedTextColor.GOLD)
                    .append(Component.newline())
                    .append(Component.text("Первое место: %1_place_nickname% финишировал за %1_place_minutes%:%1_place_seconds%:%1_place_milliseconds%"))
                    .append(Component.newline())
                    .append(Component.text("Второе место: %2_place_nickname% финишировал за %2_place_minutes%:%2_place_seconds%:%2_place_milliseconds%"))
                    .append(Component.newline())
                    .append(Component.text("Третье место: %3_place_nickname% финишировал за %3_place_minutes%:%3_place_seconds%:%3_place_milliseconds%"));
        }

        @Getter
        @ConfigSerializable
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        public static final class Prepare {
            Component playerJoin = Component.text("%player_name% присоединился к игре (%current%/%min% max %max%)", NamedTextColor.GREEN);
            Component playerLeft = Component.text("%player_name% покинул игру (%current%/%min% max %max%)", NamedTextColor.RED);
        }

        @Getter
        @ConfigSerializable
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        public static final class Finish {
            Component playerFinished = Component.text("%player_name% финишировал #%place% за %minutes%:%seconds%:%milliseconds%", NamedTextColor.AQUA);
            Component selfFinished = Component.text("Вы финишировали #%place% за %minutes%:%seconds%:%milliseconds%! Перемещение обратно через 3 секунды.", NamedTextColor.GREEN);
        }
    }
}
