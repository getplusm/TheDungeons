package t.me.p1azmer.plugin.dungeons.core.common.config.interfaces;

import t.me.p1azmer.plugin.dungeons.core.common.config.ConfigHolder;
import t.me.p1azmer.plugin.dungeons.core.common.container.IClassContainer;

public interface IConfigContainer extends IClassContainer<ConfigHolder<?>> {

    default void reload() {
        getContainer().values().forEach(ConfigHolder::reload);
    }

}

