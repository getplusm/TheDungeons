package t.me.p1azmer.engine.api.manager;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.AbstractMenu;

@Deprecated
public interface IEditable {

    @NotNull AbstractMenu<?> getEditor();
}