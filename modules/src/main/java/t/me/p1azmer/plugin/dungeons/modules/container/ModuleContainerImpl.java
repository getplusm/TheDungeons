package t.me.p1azmer.plugin.dungeons.modules.container;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.UnmodifiableView;
import t.me.p1azmer.plugin.dungeons.api.integrations.IntegrationFactory;
import t.me.p1azmer.plugin.dungeons.api.models.dungeon.Dungeon;
import t.me.p1azmer.plugin.dungeons.api.models.generator.LocationGenerator;
import t.me.p1azmer.plugin.dungeons.api.models.module.AbstractModule;
import t.me.p1azmer.plugin.dungeons.api.models.module.container.ModuleContainer;
import t.me.p1azmer.plugin.dungeons.core.common.container.ClassContainer;
import t.me.p1azmer.plugin.dungeons.modules.AnnounceModule;
import t.me.p1azmer.plugin.dungeons.modules.SchematicModule;
import t.me.p1azmer.plugin.dungeons.modules.SpawnModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ModuleContainerImpl extends ClassContainer<AbstractModule> implements ModuleContainer {

    public ModuleContainerImpl(Dungeon dungeon, LocationGenerator locationGenerator, IntegrationFactory integrationFactory) {
        super(Maps.newConcurrentMap());

        loadAll(dungeon, locationGenerator, integrationFactory);
    }

    private void loadAll(Dungeon dungeon, LocationGenerator locationGenerator, IntegrationFactory integrationFactory) {
        container.putAll(Map.of(
                SpawnModule.class, new SpawnModule(dungeon, locationGenerator),
                SchematicModule.class, new SchematicModule(dungeon, integrationFactory.getSchematicHandler()),
                AnnounceModule.class, new AnnounceModule(dungeon)
        ));
    }

    @Override
    public Optional<AbstractModule> getOptional(Class<AbstractModule> clazz) {
        return Optional.ofNullable(container.get(clazz));
    }

    @Override
    public @UnmodifiableView Collection<AbstractModule> getAll() {
        return container.values();
    }
}
