package t.me.p1azmer.engine.api.manager;

import org.jetbrains.annotations.NotNull;

public interface ILogger {

    void info(@NotNull String msg);

    void warn(@NotNull String msg);

    void error(@NotNull String msg);
}
