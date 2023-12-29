package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.plugin.dungeons.Placeholders;
import t.me.p1azmer.plugin.dungeons.api.Announce;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnounceModule extends AbstractModule {

    public AnnounceModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        return aBoolean -> true;
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean onActivate() {
        return true;
    }

    @Override
    public void update() {
        Location location = this.dungeon().getLocation();
        if (location == null || location.getWorld() == null) return;

        Announce announce = this.dungeon().getAnnounceSettings().getAnnounce(this.dungeon().getStage());
        List<LangMessage> messages = new ArrayList<>(announce.getMessage(this.dungeon().getSelfTick().get()));
        if (messages.isEmpty()) {
            return;
        }
        messages = messages.stream().map(langMessage -> langMessage
                .replace(this.dungeon().replacePlaceholders())
                .replace(this.replacePlaceholders())
                .replace(Placeholders.forLocation(location))
        ).toList();

        if (announce.isGlobal()) {
            messages.forEach(LangMessage::broadcast);
        } else {
            messages.forEach(message -> location.getWorld().getPlayers().forEach(message::send));
        }
        super.update();
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }
}
