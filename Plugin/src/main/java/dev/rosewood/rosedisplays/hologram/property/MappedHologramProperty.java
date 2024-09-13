package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import org.bukkit.persistence.PersistentDataType;

public final class MappedHologramProperty<T> extends HologramProperty<T> {

    MappedHologramProperty(String name, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        super(name, argumentHandler, persistentDataType);
    }

}
