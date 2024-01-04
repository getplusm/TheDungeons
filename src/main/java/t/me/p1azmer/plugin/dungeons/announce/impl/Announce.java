package t.me.p1azmer.plugin.dungeons.announce.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.announce.AnnounceManager;
import t.me.p1azmer.plugin.dungeons.announce.editor.AnnounceMainEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Announce extends AbstractConfigHolder<DungeonPlugin> implements IPlaceholderMap {
    private ItemStack icon;
    private List<LangMessage> messages;
    private boolean global;

    private final PlaceholderMap placeholderMap;
    private final AnnounceManager manager;
    private AnnounceMainEditor editor;

    public Announce(@NotNull AnnounceManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.messages = new ArrayList<>();
        this.icon = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0MTk5NmZkMjBjYTIxZDc5YWRmYzBlMTIwNTdiMmYyY2VhZGY3YjNjZjViYjVmOGE5MmZlMzQ2MDE2MWFjZCJ9fX0=");

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.ANNOUNCE_ID, this::getId)
                .add(Placeholders.ANNOUNCE_MESSAGES, () -> Colorizer.apply(String.join("\n", this.getMessagesRaw())))
                .add(Placeholders.ANNOUNCE_GLOBAL, () -> LangManager.getBoolean(this.isGlobal()))
        ;
    }

    @Override
    public boolean load() {
        ItemStack item = this.cfg.getItemEncoded("Icon");
        if (item == null)
            item = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0MTk5NmZkMjBjYTIxZDc5YWRmYzBlMTIwNTdiMmYyY2VhZGY3YjNjZjViYjVmOGE5MmZlMzQ2MDE2MWFjZCJ9fX0=");
        this.setIcon(item);
        this.setMessages(this.cfg.getStringList("Messages"));
        this.setGlobal(this.cfg.getBoolean("Global"));
        return true;
    }

    @Override
    protected void onSave() {
        this.cfg.setItemEncoded("Icon", this.getIcon());
        this.cfg.set("Messages", this.getMessagesRaw());
        this.cfg.set("Global", this.isGlobal());
    }

//    public static Announce read(@NotNull DungeonPlugin plugin, @NotNull JYML cfg, @NotNull String path) {
//        List<LangMessage> messages = cfg.getStringList(path + ".Messages").stream().map(message -> new LangMessage(plugin, message)).collect(Collectors.toList());
//        boolean global = cfg.getBoolean(path + ".Global");
//        int[] times = cfg.getIntArray(path + ".Times");
//        return new Announce(messages, global, times);
//    }
//
//    public void write(@NotNull JYML cfg, @NotNull String path) {
//        cfg.set(path + ".Messages", this.getMessagesRaw());
//        cfg.set(path + ".Global", this.isGlobal());
//        cfg.setIntArray(path + ".Times", this.getTimes());
//    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public AnnounceMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new AnnounceMainEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    @NotNull
    public List<String> getMessagesRaw() {
        return messages.stream().map(LangMessage::getRaw).collect(Collectors.toList());
    }

    @NotNull
    public List<LangMessage> getMessages() {
        return messages;
    }

    public boolean isGlobal() {
        return global;
    }

    @NotNull
    public List<LangMessage> getMessage() {
        return getMessages();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public AnnounceManager manager() {
        return this.manager;
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = icon;
    }

    public void setMessages(@NotNull List<String> messages) {
        this.messages = new ArrayList<>(messages).stream().map(message -> new LangMessage(plugin, message)).collect(Collectors.toList());
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }
}
