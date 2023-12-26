package t.me.p1azmer.plugin.dungeons.dungeon.modules.impl;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.plugin.dungeons.config.Config;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.modules.AbstractModule;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class BossBarModule extends AbstractModule {
    private BossBar bossBar;

    public BossBarModule(@NotNull Dungeon dungeon, @NotNull String id) {
        super(dungeon, id, true);
    }

    @Override
    protected Predicate<Boolean> onLoad() {
        this.bossBar = Bukkit.createBossBar(Colorizer.apply(this.dungeon().replacePlaceholders().apply(Config.BOSSBAR_TITLE.get())), Config.BOSSBAR_COLOR.get(), Config.BOSSBAR_STYLE.get()); // rewrite for dungeon self bossbar
        return aBoolean -> dungeon().getStage().isPrepare();
    }

    @Override
    protected void onShutdown() {
        if (this.bossBar != null) {
            CompletableFuture.runAsync(this.bossBar::removeAll);
            this.bossBar = null;
        }
    }

    @Override
    public boolean onActivate() {
        if (this.bossBar == null) return false;

        this.dungeon().getWorld().getPlayers().forEach(this.bossBar::addPlayer);
        return true;
    }

    @Override
    public void update() {
        if (this.bossBar == null) return;
//        this.bossBar.setProgress(this.dungeon().getNextStageTime() / 100F);

    }

    @Override
    public boolean onDeactivate() {
        CompletableFuture.runAsync(this.bossBar::removeAll);
        return true;
    }
}
