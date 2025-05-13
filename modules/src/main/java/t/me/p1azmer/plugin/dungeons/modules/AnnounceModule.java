package t.me.p1azmer.plugin.dungeons.modules;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.core.models.Announce;
import t.me.p1azmer.plugin.dungeons.settings.AnnounceSettings;

import java.time.Duration;
import java.util.function.Predicate;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnnounceModule extends AbstractModule {

    Dungeon dungeon;

    public AnnounceModule(Dungeon dungeon) {
        super(true);
        this.dungeon = dungeon;
    }

    @Override
    public Predicate<Boolean> isCanEnabled() {
        return aBoolean -> true;
    }

    @Override
    public Predicate<Boolean> isCanDisabled() {
        return aBoolean -> true;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void startup() {
    }

    @Override
    public void update() {
        super.update();

        State state = dungeon.getState();
        Duration duration = dungeon.getStateSwitchTime();
        long durationSeconds = duration.toSeconds();
        Location location = dungeon.getLocation();
        AnnounceSettings settings = dungeon.getSettingsContainer().getOrThrow(AnnounceSettings.class);
        for (AnnounceSettings.AnnounceConfig announceConfig : settings.getAnnounces()) {
            if (!announceConfig.getState().equals(state)) continue;

            Announce announce = announceConfig.getAnnounce();
            int notifyTimeInSeconds = announceConfig.getNotifyTimeInSeconds();
            Component text = announce.getText();
            if (durationSeconds == notifyTimeInSeconds) {
                if (announce.isGlobal()) {
                    Bukkit.broadcast(text);
                    return;
                }

                if (location != null) {
                    World world = location.getWorld();
                    world.getPlayers().forEach(player -> player.sendMessage(text));
                }
            }
        }
    }

    @Override
    public void shutdown() {
    }
}
