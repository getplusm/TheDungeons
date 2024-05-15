package t.me.p1azmer.plugin.dungeons.announce.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.announce.AnnounceManager;
import t.me.p1azmer.plugin.dungeons.announce.Placeholders;
import t.me.p1azmer.plugin.dungeons.announce.editor.AnnounceMainEditor;

@Getter
@Setter
public class Announce extends AbstractConfigHolder<DungeonPlugin> implements Placeholder {
  private static final ItemStack defaultIcon = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0MTk5NmZkMjBjYTIxZDc5YWRmYzBlMTIwNTdiMmYyY2VhZGY3YjNjZjViYjVmOGE5MmZlMzQ2MDE2MWFjZCJ9fX0=");
  private ItemStack icon;
  private LangMessage message;
  private boolean global;

  private final PlaceholderMap placeholderMap;
  private final AnnounceManager manager;
  private AnnounceMainEditor editor;

  public Announce(@NotNull AnnounceManager manager, @NotNull JYML cfg) {
    super(manager.plugin(), cfg);
    this.manager = manager;

    this.message = new LangMessage(plugin(), "");
    this.icon = defaultIcon;

    this.placeholderMap = new PlaceholderMap()
      .add(Placeholders.ANNOUNCE_ID, this::getId)
      .add(Placeholders.ANNOUNCE_MESSAGES, () -> Colorizer.apply(String.join("\n", this.getMessage().normalizeLines())))
      .add(Placeholders.ANNOUNCE_GLOBAL, () -> LangManager.getBoolean(this.isGlobal()))
    ;
  }

  @Override
  public boolean load() {
    ItemStack item = this.cfg.getItemEncoded("Icon");
    if (item == null) item = defaultIcon;

    this.setIcon(item);
    this.setMessage(new LangMessage(plugin(), String.join("\n", this.cfg.getStringList("Messages"))));
    this.setGlobal(this.cfg.getBoolean("Global"));
    return true;
  }

  @Override
  protected void onSave() {
    this.cfg.setItemEncoded("Icon", this.getIcon());
    this.cfg.set("Messages", this.getMessage().asList());
    this.cfg.set("Global", this.isGlobal());
  }

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
  public LangMessage getMessageWithoutPrefix() {
    return new LangMessage(plugin(), "<! prefix:\"false\" !>" + this.getMessage().getRaw());
  }

  @Override
  public @NotNull PlaceholderMap getPlaceholders() {
    return this.placeholderMap;
  }
}