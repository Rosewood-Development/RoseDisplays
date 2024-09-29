package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.registry.RoseKey;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import org.bukkit.persistence.PersistentDataType;

public final class MappedHologramProperty<T> extends HologramProperty<T> {

    MappedHologramProperty(RoseKey key, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        super(key, argumentHandler, persistentDataType);
    }

}
