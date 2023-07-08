package t.me.p1azmer.engine.api.editor;

import org.jetbrains.annotations.NotNull;

public interface InputHandler {

    boolean handle(@NotNull InputWrapper wrapper);
}