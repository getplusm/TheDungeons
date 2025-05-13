package t.me.p1azmer.plugin.dungeons.integration.impl.access;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.integrations.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.settings.AccessSettings;

import java.util.Collection;
import java.util.Set;

public class AccessProSkill implements AccessHandler {

    @Override
    public boolean canAccess(@NotNull Dungeon dungeon, @NotNull Player player) {
        AccessSettings settings = dungeon.getSettingsContainer().getOrThrow(AccessSettings.class);

        PlayerData playerData = Fabled.getData(player);
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        Set<String> proSkillAPIAccessClasses = settings.getProSkillClasses();

        for (PlayerClass playerClass : playerClasses) {
            if (proSkillAPIAccessClasses.contains(playerClass.getData().getName())) {
                return true;
            }
        }
        return false;
    }
}
