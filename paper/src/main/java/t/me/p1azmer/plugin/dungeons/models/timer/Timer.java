package t.me.p1azmer.plugin.dungeons.models.timer;

import com.cjcrafter.foliascheduler.ServerImplementation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.state.State;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.api.models.module.container.ModuleContainer;
import t.me.p1azmer.plugin.dungeons.settings.StateSettings;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Timer {
    Dungeon dungeon;
    @NonFinal Instant switchTime;

    public void tick(ServerImplementation syncScheduler) {
        State state = dungeon.getState();
        tickState(state);
        tickModules();
    }

    public Duration getBetween() {
        return Duration.between(Instant.now(), switchTime);
    }

    private void tickState(State state) {
        StateSettings stateSettings = dungeon.getSettingsContainer().getOrThrow(StateSettings.class);
        if (switchTime == null) {
            Duration duration = stateSettings.getStateDuration(state.name());
            switchTime = Instant.now().plus(duration);
        }

        Duration between = getBetween();
        dungeon.setStateSwitchTime(between);
        if (between.toSeconds() <= 0) {
            State nextState = state.next();
            dungeon.setState(nextState);
            Duration duration = stateSettings.getStateDuration(nextState.name());
            switchTime = Instant.now().plus(duration);
        }
    }

    private void tickModules() {
        ModuleContainer moduleContainer = dungeon.getModuleContainer();
        for (AbstractModule module : moduleContainer.getAll()) {
            module.tick();
        }
    }
}