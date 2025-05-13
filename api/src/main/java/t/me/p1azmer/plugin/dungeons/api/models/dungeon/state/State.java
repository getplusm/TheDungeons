package t.me.p1azmer.plugin.dungeons.api.models.dungeon.state;

public enum State {
    FREEZE, // стадия заморозки/бездействия

    CHECK_LOCATION, // стадия проверки данжа перед отправкой на prepare
    GENERATION, // подготовка данжа к спавну (генерация структуры)
    WAITING_OPEN, // стадия открытия. Дополнительная стадия для ожидания
    OPENED, // стадия когда данж уже открыт
    DELETING, // удаление данжа. Очистка структуры и т.п

    CANCELLED, // стадия отмены
    REBOOTED;


    public State next() {
        if (ordinal() == values().length - 1) return FREEZE;
        return values()[ordinal() + 1];
    }

    public State previous() {
        if (ordinal() -1 < 0) return FREEZE;
        return values()[ordinal() - 1];
    }
}
