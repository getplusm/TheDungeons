package t.me.p1azmer.plugin.dungeons.api.settings;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;

public abstract class AbstractSettings implements IPlaceholderMap {
    protected Dungeon dungeon;
    protected PlaceholderMap placeholderMap;

    public AbstractSettings(@NotNull Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @NotNull
    public Dungeon dungeon() {
        return dungeon;
    }

    public DungeonPlugin plugin(){
        return this.dungeon().plugin();
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }
}
