package t.me.p1azmer.plugin.dungeons.api;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Announce implements IPlaceholderMap {
    private List<LangMessage> messages;
    private int[] times;
    private boolean global;
    private final PlaceholderMap placeholderMap;

    public Announce(@NotNull List<LangMessage> messages,
                    boolean global,
                    int[] times
    ) {
        this.messages = messages;
        this.global = global;
        this.times = times;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.ANNOUNCE_GLOBAL, () -> LangManager.getBoolean(this.isGlobal()))
        ;
    }

    public static Announce read(@NotNull DungeonPlugin plugin, @NotNull JYML cfg, @NotNull String path) {
        List<LangMessage> messages = cfg.getStringList(path + ".Messages").stream().map(message -> new LangMessage(plugin, message)).collect(Collectors.toList());
        boolean global = cfg.getBoolean(path + ".Global");
        int[] times = cfg.getIntArray(path + ".Times");
        return new Announce(messages, global, times);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Messages", this.getMessagesRaw());
        cfg.set(path + ".Global", this.isGlobal());
        cfg.setIntArray(path + ".Times", this.getTimes());
    }

    @NotNull
    public List<String> getMessagesRaw() {
        return messages.stream().map(LangMessage::getRaw).collect(Collectors.toList());
    }

    @NotNull
    public List<LangMessage> getMessages() {
        return messages;
    }

    public int[] getTimes() {
        return times;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setMessages(@NotNull DungeonPlugin plugin, @NotNull List<String> messages) {
        this.messages = new ArrayList<>(messages).stream().map(message->new LangMessage(plugin, "<! prefix:\"false\" !>"+message)).collect(Collectors.toList());
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    @NotNull
    public List<LangMessage> getMessage(int time) {
        return Arrays.stream(this.times).anyMatch(value -> value == time) ? getMessages() : Collections.emptyList();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
