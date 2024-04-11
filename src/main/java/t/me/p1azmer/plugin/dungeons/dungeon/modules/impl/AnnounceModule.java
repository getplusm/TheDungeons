package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.plugin.dungeons.announce.impl.Announce;
import t.me.p1azmer.plugin.dungeons.dungeon.Placeholders;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.AnnounceSettings;
import t.me.p1azmer.plugin.dungeons.dungeon.stage.DungeonStage;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class AnnounceModule extends AbstractModule {

    public AnnounceModule(
            @NotNull Dungeon dungeon,
            @NotNull String id
    ) {
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
    public CompletableFuture<Boolean> onActivate(boolean force) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void update() {
        super.update();
        Location location = this.getDungeon().getLocation().orElse(null);
        AnnounceSettings announceSettings = this.getDungeon().getAnnounceSettings();
        DungeonStage dungeonStage = this.getDungeon().getStage();
        int time = this.getDungeon().getSelfTick().get();
        Set<Announce> announces = announceSettings.getAnnounces(dungeonStage, time);

        for (Announce announce : announces) {
            LangMessage langMessage = announce.getMessageWithoutPrefix()
                    .replace(this.getDungeon().replacePlaceholders())
                    .replace(announce.replacePlaceholders())
                    .replace(this.replacePlaceholders());

            if (location != null)
                langMessage = langMessage
                        .replace(Placeholders.forLocation(location));

            if (announce.isGlobal()) langMessage.broadcast();
            else if (location != null) {
                List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
                players.forEach(langMessage::send);
                debug("Message broadcasted: " + langMessage.getLocalized());
            }
        }
    }

    @Override
    public boolean onDeactivate(boolean force) {
        return true;
    }
}
