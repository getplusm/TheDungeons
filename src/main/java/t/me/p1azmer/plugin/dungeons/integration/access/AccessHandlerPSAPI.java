package t.me.p1azmer.plugin.dungeons.integration.access;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import t.me.p1azmer.plugin.dungeons.api.handler.access.AccessHandler;
import t.me.p1azmer.plugin.dungeons.dungeon.impl.Dungeon;
import t.me.p1azmer.plugin.dungeons.dungeon.settings.impl.AccessSettings;

import java.util.Collection;
import java.util.Set;

public class AccessHandlerPSAPI implements AccessHandler {
    @Override
    public void setup() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean allowedToEnterDungeon(@NotNull Dungeon dungeon, @NotNull Player player) {
        AccessSettings accessSettings = dungeon.getAccessSettings();
        if (!accessSettings.isEnabled()) return true;

        PlayerData playerData = Fabled.getData(player);
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        Set<String> proSkillAPIAccessClasses = accessSettings.getProSkillAPIAccessClasses();

        return playerClasses.stream().anyMatch(playerClass -> {
            return proSkillAPIAccessClasses.contains(playerClass.getData().getName());
        });
    }
}
