package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.dungeons.api.handler.hologram.HologramHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.Optional;
import java.util.function.Predicate;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class HologramModule extends AbstractModule {
    final HologramHandler handler;

    ChestModule chestModule;

    public HologramModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
        this.handler = plugin().getHologramHandler();
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        return aBoolean -> {
            Optional<ChestModule> module = this.getManager().getModule(ChestModule.class);
            this.chestModule = module.orElse(null);
            return handler != null && this.chestModule != null && !this.chestModule.getChests().isEmpty();
        };
    }

    @Override
    protected void onShutdown() {
        this.chestModule = null;
    }

    @Override
    public boolean onActivate(boolean force) {
        this.debug("Starting initialize holograms");
        handler.create(this.getDungeon(), this.chestModule);

        return true;
    }

    @Override
    public boolean onDeactivate(boolean force) {
        if (handler != null) handler.delete(this.getDungeon());
        return true;
    }
}
