package t.me.p1azmer.engine.api.lang;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.hooks.Hooks;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.MessageUtil;
import t.me.p1azmer.engine.utils.Placeholders;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.message.NexParser;
import t.me.p1azmer.engine.utils.regex.RegexUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LangMessage {

    @Deprecated
    private static final Pattern PATTERN_MESSAGE_FULL = Pattern.compile("((\\{message:)+(.+?)\\}+(.*?))");
    @Deprecated
    private static final Map<String, Pattern> PATTERN_MESSAGE_PARAMS = new HashMap<>();

    private static final Pattern PATTERN_OPTIONS = Pattern.compile("<\\!(.*?)\\!>");
    //private static final Pattern PATTERN_NO_PREFIX = Pattern.compile("<noprefix>");
    //private static final Pattern PATTERN_SOUND = Pattern.compile("sound" + OPTION_PATTERN);

    static {
        for (String parameter : new String[]{"type", "prefix", "sound", "fadeIn", "stay", "fadeOut"}) {
            PATTERN_MESSAGE_PARAMS.put(parameter, Pattern.compile("~+(" + parameter + ")+?:+(.*?);"));
        }
    }

    enum Option {
        PREFIX("prefix"),
        SOUND("sound"),
        TYPE("type"),
        PLACEHOLDER_API("papi");

        private final Pattern pattern;

        Option(@NotNull String name) {
            this.pattern = Pattern.compile(name + NexParser.OPTION_PATTERN);
        }

        @NotNull
        public Pattern getPattern() {
            return pattern;
        }
    }

    private final NexPlugin<?> plugin;

    private String msgRaw;
    private String msgLocalized;
    private OutputType type = OutputType.CHAT;
    private boolean hasPrefix = true;
    private Sound sound;
    private int[] titleTimes = new int[3];

    public LangMessage(@NotNull NexPlugin<?> plugin, @NotNull String raw) {
        this.plugin = plugin;
        this.setRaw(raw);
    }

    LangMessage(@NotNull LangMessage from) {
        this.plugin = from.plugin;
        this.msgRaw = from.getRaw();
        this.msgLocalized = from.getLocalized();
        this.type = from.type;
        this.hasPrefix = from.hasPrefix;
        this.sound = from.sound;
        this.titleTimes = Arrays.copyOf(from.titleTimes, from.titleTimes.length);
    }

    @Deprecated
    void setArguments(@NotNull String msg) {
        Matcher matcher = RegexUtil.getMatcher(PATTERN_MESSAGE_FULL, msg);
        if (!RegexUtil.matcherFind(matcher)) return;

        // String with only args
        String msgRaw = matcher.group(0);
        String msgParams = matcher.group(3).trim();
        this.msgLocalized = msg.replace(msgRaw, "");

        for (Map.Entry<String, Pattern> entryParams : PATTERN_MESSAGE_PARAMS.entrySet()) {
            Matcher matcherParam = RegexUtil.getMatcher(entryParams.getValue(), msgParams);
            if (!RegexUtil.matcherFind(matcherParam)) continue;

            String paramName = entryParams.getKey();
            String paramValue = matcherParam.group(2);//.stripLeading();
            if ("type".equals(paramName)) {
                this.type = StringUtil.getEnum(paramValue, OutputType.class).orElse(OutputType.CHAT);
            } else if ("prefix".equals(paramName)) {
                this.hasPrefix = Boolean.parseBoolean(paramValue);
            } else if ("sound".equals(paramName)) {
                this.sound = StringUtil.getEnum(paramValue, Sound.class).orElse(null);
            } else if ("fadeIn".equals(paramName)) {
                this.titleTimes[0] = StringUtil.getInteger(paramValue, -1);
            } else if ("stay".equals(paramName)) {
                this.titleTimes[1] = StringUtil.getInteger(paramValue, -1);
                if (this.titleTimes[1] < 0) this.titleTimes[1] = Short.MAX_VALUE;
            } else if ("fadeOut".equals(paramName)) {
                this.titleTimes[2] = StringUtil.getInteger(paramValue, -1);
            }
        }
    }

    void setOptions(@NotNull String msg) {
        Matcher matcher = RegexUtil.getMatcher(PATTERN_OPTIONS, msg);
        if (!RegexUtil.matcherFind(matcher)) return;

        // String with only args
        String matchFull = matcher.group(0);
        String matchOptions = matcher.group(1).trim();
        this.msgLocalized = msg.replace(matchFull, "");

        for (Option option : Option.values()) {
            Matcher matcherParam = RegexUtil.getMatcher(option.getPattern(), matchOptions);
            if (!RegexUtil.matcherFind(matcherParam)) continue;

            String optionValue = matcherParam.group(1);//.stripLeading();
            switch (option) {
                case TYPE:
                    String[] split = optionValue.split(":");
                    this.type = StringUtil.getEnum(split[0], OutputType.class).orElse(OutputType.CHAT);
                    if (this.type == OutputType.TITLES) {
                        this.titleTimes[0] = split.length >= 2 ? StringUtil.getInteger(split[1], -1) : -1;
                        this.titleTimes[1] = split.length >= 3 ? StringUtil.getInteger(split[2], -1, true) : -1;
                        this.titleTimes[2] = split.length >= 4 ? StringUtil.getInteger(split[3], -1) : -1;

                        if (this.titleTimes[1] < 0) this.titleTimes[1] = Short.MAX_VALUE;
                    }
                    break;
                case PREFIX:
                    this.hasPrefix = Boolean.parseBoolean(optionValue);
                    break;
                case SOUND:
                    this.sound = StringUtil.getEnum(optionValue, Sound.class).orElse(null);
                    break;
            }
        }
    }

    @NotNull
    public String getRaw() {
        return this.msgRaw;
    }

    public void setRaw(@NotNull String msgRaw) {
        this.msgRaw = msgRaw;
        this.setLocalized(this.replaceDefaults().apply(this.getRaw()));
        this.setArguments(this.getLocalized());
        this.setOptions(this.getLocalized());
    }

    @NotNull
    public String getLocalized() {
        return this.msgLocalized;
    }

    @NotNull
    @Deprecated
    public String getMsg() {
        return this.msgLocalized;
    }

    private void setLocalized(@NotNull String msgLocalized) {
        this.msgLocalized = Colorizer.apply(msgLocalized);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public LangMessage replace(@NotNull String var, @NotNull Object replacer) {
        if (this.isEmpty()) return this;
        return this.replace(str -> str.replace(var, String.valueOf(replacer)));
    }

    @NotNull
    public LangMessage replace(@NotNull Predicate<String> predicate, @NotNull BiConsumer<String, List<String>> replacer) {
        if (this.isEmpty()) return this;

        LangMessage msgCopy = new LangMessage(this);
        List<String> replaced = new ArrayList<>();
        msgCopy.asList().forEach(line -> {
            if (predicate.test(line)) {
                replacer.accept(line, replaced);
                return;
            }
            replaced.add(line);
        });
        msgCopy.setLocalized(String.join("\\n", replaced));
        return msgCopy;
    }

    @NotNull
    public LangMessage replace(@NotNull UnaryOperator<String> replacer) {
        if (this.isEmpty()) return this;

        LangMessage msgCopy = new LangMessage(this);
        msgCopy.setLocalized(replacer.apply(msgCopy.getLocalized()));
        return msgCopy;
    }

    public boolean isEmpty() {
        return (this.type == OutputType.NONE || this.getLocalized().isEmpty());
    }

    public void broadcast() {
        if (this.isEmpty()) return;

        Bukkit.getServer().getOnlinePlayers().forEach(this::send);
        this.send(Bukkit.getServer().getConsoleSender());
    }

    public void send(@NotNull CommandSender sender) {
        if (this.isEmpty()) return;

        if (this.sound != null && sender instanceof Player) {
            Player player = (Player) sender;
            MessageUtil.sound(player, this.sound);
        }

        if (this.type == OutputType.CHAT) {
            String prefix = hasPrefix ? plugin.getConfigManager().pluginPrefix : "";
            this.asList().forEach(line -> {
                if (NexParser.contains(line)) {
                    MessageUtil.sendCustom(sender, prefix + line);
                } else MessageUtil.sendWithJson(sender, prefix + line);
            });
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (this.type == OutputType.ACTION_BAR) {
                MessageUtil.sendActionBar(player, this.getLocalized());
            } else if (this.type == OutputType.ITEMS) {
                MessageUtil.sendItemMessage(player, this.getLocalized());
            } else if (this.type == OutputType.TITLES) {
                List<String> list = this.asList();
                String title = list.size() > 0 ? NexParser.toPlainText(list.get(0)) : "";
                String subtitle = list.size() > 1 ? NexParser.toPlainText(list.get(1)) : "";
                if (Hooks.hasPlaceholderAPI()) {
                    if (PlaceholderAPI.containsPlaceholders(title))
                        title = PlaceholderAPI.setPlaceholders((Player) sender, title);
                    if (PlaceholderAPI.containsPlaceholders(subtitle))
                        subtitle = PlaceholderAPI.setPlaceholders((Player) sender, subtitle);
                }
                player.sendTitle(title, subtitle, this.titleTimes[0], this.titleTimes[1], this.titleTimes[2]);
            }
        }
    }

    @NotNull
    public List<String> asList() {
        return this.isEmpty() ? Collections.emptyList() : Stream.of(this.getLocalized().split("\\\\n"))
                .filter(f -> !f.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Replaces a raw '\n' new line splitter with a system one.
     *
     * @return A string with a system new line splitters.
     */
    @NotNull
    public String normalizeLines() {
        return String.join("\n", this.asList());
    }

    @NotNull
    private UnaryOperator<String> replaceDefaults() {
        return str -> {
            for (Map.Entry<String, String> entry : this.plugin.getLangManager().getPlaceholders().entrySet()) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
            return Placeholders.PLUGIN.replacer(plugin).apply(str);
        };
    }

    public enum OutputType {
        ITEMS, CHAT, ACTION_BAR, TITLES, NONE,
    }
}