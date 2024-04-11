package t.me.p1azmer.plugin.dungeons.mob;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.dungeons.DungeonPlugin;
import t.me.p1azmer.plugin.dungeons.mob.kill.MobKillReward;

public class MobListener extends AbstractListener<DungeonPlugin> {
    private final MobManager manager;

    public MobListener(@NotNull MobManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!this.manager.isCustomMob(entity)) return;

        e.getDrops().clear();
        e.setDroppedExp(0);

        this.manager.getMobs().remove(entity);
        Player killer = entity.getKiller();
        if (killer != null) {

            MobKillReward killReward = MobManager.getMobKillReward(entity);

            if (killReward != null) {
                killReward.reward(killer);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMobCombust(EntityCombustEvent e) {
        Entity entity = e.getEntity();
        if (this.manager.isCustomMob(entity)) {
            if (e instanceof EntityCombustByEntityEvent ec) {
                if (this.manager.isCustomMob(ec.getCombuster())) {
                    e.setCancelled(true);
                }
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity2(PlayerInteractAtEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickupItems(EntityPickupItemEvent event) {
        if (this.manager.isCustomMob(event.getEntity()))
            event.setCancelled(true);
    }

    private boolean canInteract(@NotNull PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof LivingEntity livingEntity && this.manager.getMobs().isAlly(livingEntity)) return true;

        return entity instanceof Player || entity instanceof Vehicle || !this.manager.isCustomMob(entity);
    }
}
