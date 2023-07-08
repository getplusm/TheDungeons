package t.me.p1azmer.plugin.dungeons.dungeon;

public enum DungeonState {
    FREEZE,
    WAITING,
    /**
     * Call this state to spawn dungeon
     */
    PREPARE,
    OPEN,
    CLOSED,
    CANCEL;

    public boolean isFreeze() {
        return this.equals(FREEZE);
    }

    public boolean isWaiting() {
        return this.equals(WAITING);
    }

    public boolean isPrepare() {
        return this.equals(PREPARE);
    }

    public boolean isOpen() {
        return this.equals(OPEN);
    }

    public boolean isClosed() {
        return this.equals(CLOSED);
    }

    public boolean isCanceled() {
        return this.equals(CANCEL);
    }
}
