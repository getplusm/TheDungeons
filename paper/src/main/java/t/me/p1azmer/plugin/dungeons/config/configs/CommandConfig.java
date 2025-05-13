package t.me.p1azmer.plugin.dungeons.config.configs;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.groups.GroupInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@ConfigSerializable
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class CommandConfig extends ConfigHolder<CommandConfig> {

    Command adminCommand = Command.builder()
            .name("dungeons-a")
            .permissionCheck(false)
            .groupCheck(true)
            .group(GroupInfo.of("admin"))
            .build();

    public CommandConfig(File baseFilePath) {
        super(baseFilePath);
    }

    public CommandConfig() {
        this(null);
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @ConfigSerializable
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static class Command {
        String name = "command";
        @Builder.Default
        boolean permissionCheck = true;
        @Builder.Default
        boolean groupCheck = false;
        @Builder.Default
        String permissionAccess = "command.use";
        @Builder.Default
        List<String> aliases = new ArrayList<>();
        @Builder.Default
        GroupInfo group = GroupInfo.of("default");
    }
}
