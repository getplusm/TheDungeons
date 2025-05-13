package t.me.p1azmer.plugin.dungeons.api.models.module;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractModule {

    boolean updatable;
    @NonFinal transient LocalState state = null;
    @Getter transient final Logger log = Logger.getLogger(getClass().getSimpleName());

    public void tick() {
        updateState();
    }

    private void updateState() {
        boolean accessed = isCanEnabled().test(true);
        if (!accessed) {
            return;
        }

        if (state == null) state = LocalState.INITIALIZE;
        else state = state.next();
        try {
            switch (state) {
                case INITIALIZE: {
                    initialize();
                }
                case UPDATE: {
                    if (!updatable) return;
                    update();
                }
                case STARTUP: {
                    startup();
                }
                case SHUTDOWN: {
                    if (isCanDisabled().test(true)) {
                        shutdown();
                    }
                }
            }
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.log(Level.SEVERE, "Got an exception while tick module", exception);
        }
    }

    public abstract Predicate<Boolean> isCanEnabled();

    public abstract Predicate<Boolean> isCanDisabled();

    public abstract void initialize();

    public void update() {
        state = LocalState.UPDATE;
    }

    public abstract void startup();

    public abstract void shutdown();

    private enum LocalState {
        INITIALIZE, UPDATE, STARTUP, SHUTDOWN;

        public LocalState next() {
            if (ordinal() + 1 >= values().length) return INITIALIZE;
            return values()[ordinal() + 1];
        }
    }
}
