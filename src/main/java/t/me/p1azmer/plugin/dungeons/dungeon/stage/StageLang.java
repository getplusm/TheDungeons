package t.me.p1azmer.plugin.dungeons.dungeon.stage;

import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.plugin.dungeons.lang.Lang;

import static t.me.p1azmer.engine.utils.Colors2.GRAY;

public class StageLang extends Lang {
    public static final LangKey FREEZE = LangKey.of(
            "Stage.Freeze.Description",
            GRAY + "The freezing stage.\n" +
                    GRAY + "All the dungeons that are\n" +
                    GRAY + "at this stage are waiting for their turn"
    );
    public static final LangKey CHECK = LangKey.of(
            "Stage.Check.Description",
            GRAY + "The module check stage.\n" +
                    GRAY + "Location generation & etc."
    );
    public static final LangKey PREPARE = LangKey.of(
            "Stage.Preapre.Description",
            GRAY + "The stage of preparation\n" +
                    GRAY + "for creation in the world.\n" +
                    GRAY + "Here you can notify players that\n" +
                    GRAY + "the dungeon will soon be available in the world"
    );
    public static final LangKey CLOSED = LangKey.of(
            "Stage.Closed.Description",
            GRAY + "The stage when the dungeon\n" +
                    GRAY + "has just spawned and\n" +
                    GRAY + "is closed to entry"
    );
    public static final LangKey WAITING_PLAYERS = LangKey.of(
            "Stage.Waiting_Players.Description",
            GRAY + "The waiting stage,\n" +
                    GRAY + "when players can enter and run around\n" +
                    GRAY + "the dungeon without additional modules"
    );
    public static final LangKey OPENING = LangKey.of(
            "Stage.Opening.Description",
            GRAY + "The stage of the opening of the dungeon,\n" +
                    GRAY + "when the chests will be filled"
    );
    public static final LangKey OPENED = LangKey.of(
            "Stage.Opened.Description",
            GRAY + "The dungeon is open,\n" +
                    GRAY + "the modules are activated,\n" +
                    GRAY + "we are playing"
    );
    public static final LangKey DELETING = LangKey.of(
            "Stage.Deleting.Description",
            GRAY + "Module cleaning stage"
    );
    public static final LangKey CANCELLED = LangKey.of(
            "Stage.Cancelled.Description",
            GRAY + "Called when something goes wrong"
    );
    public static final LangKey REBOOTED = LangKey.of(
            "Stage.Rebooted.Description",
            GRAY + "Rebooting the dungeon"
    );
}
