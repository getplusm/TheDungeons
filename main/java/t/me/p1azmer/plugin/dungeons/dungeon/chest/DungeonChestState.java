package t.me.p1azmer.plugin.dungeons.dungeon.chest;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.dungeons.Placeholders;

public enum DungeonChestState implements IPlaceholderMap {

    WAITING,
    COOLDOWN,
    OPENED,
    CLOSED,
    DELETED,
    ;

    public boolean isOpen() {
        return this.equals(OPENED);
    }

    public boolean isCooldown(){
        return this.equals(COOLDOWN);
    }

    public boolean isClosed() {
        return this.equals(CLOSED);
    }

    public boolean isDeleted(){
        return this.equals(DELETED);
    }

    public boolean isWaiting(){
        return this.equals(WAITING);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return new PlaceholderMap().add(Placeholders.DUNGEON_CHEST_STATE_NAME, this::name);
    }
}
