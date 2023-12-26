package t.me.p1azmer.plugin.dungeons.commands;

import t.me.p1azmer.engine.api.command.CommandFlag;

public class CommandFlags {

    public static final CommandFlag<Boolean> SILENT = CommandFlag.booleanFlag("s");
    public static final CommandFlag<Boolean> RANDOM = CommandFlag.booleanFlag("r");
}