package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;

public class MappedHologramProperty<T> extends HologramProperty<T> {

    protected MappedHologramProperty(String name, ArgumentHandler<T> argumentHandler) {
        super(name, argumentHandler);
    }

}
