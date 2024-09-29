package dev.rosewood.rosedisplays.manager;

import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import dev.rosewood.rosegarden.registry.RoseRegistry;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.Collection;
import java.util.List;

public class RegistryManager extends Manager {

    public RegistryManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public Collection<RoseRegistry<?>> getManagedRegistries() {
        return List.of(
                HologramType.REGISTRY,
                HologramProperties.REGISTRY,
                HologramPropertyTag.REGISTRY
        );
    }

}
