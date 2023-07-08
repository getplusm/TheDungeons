package t.me.p1azmer.engine.api.editor;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.menu.AbstractMenu;

@Deprecated
public interface EditorHolder<P extends NexPlugin<P>, C extends Enum<C>> {

    @NotNull AbstractMenu<?> getEditor();
}
