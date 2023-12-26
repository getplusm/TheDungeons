package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.function.Predicate;

public class HologramModule extends AbstractModule {
    private ChestModule chestModule;
    public HologramModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.chestModule = dungeon().getModuleManager().getModule(ChestModule.class).orElse(null);

        return aBoolean -> this.chestModule != null && !this.chestModule.getChests().isEmpty() && dungeon().getLocation() != null;
    }

    @Override
    protected void onShutdown() {
        if (this.chestModule != null){
            this.chestModule = null;
        }
    }

    @Override
    public boolean onActivate() {
        Location location = this.dungeon().getLocation();
        if (location == null) return super.activate();

        this.dungeonManager().plugin().getHologramHandler().create(this.dungeon(), this.chestModule);
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.dungeonManager().plugin().getHologramHandler().delete(this.dungeon());
        return true;
    }
}
