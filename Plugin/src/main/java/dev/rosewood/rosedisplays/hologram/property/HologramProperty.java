package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import java.util.Objects;
import org.bukkit.persistence.PersistentDataType;

public sealed class HologramProperty<T> permits MappedHologramProperty {

    private final String name;
    private final ArgumentHandler<T> argumentHandler;
    private final PersistentDataType<?, T> persistentDataType;

    HologramProperty(String name, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        this.name = name;
        this.argumentHandler = argumentHandler;
        this.persistentDataType = persistentDataType;
    }

    public String getName() {
        return this.name;
    }

    public ArgumentHandler<T> getArgumentHandler() {
        return this.argumentHandler;
    }

    public Class<T> getValueType() {
        return this.argumentHandler.getHandledType();
    }

    public PersistentDataType<?, T> getPersistentDataType() {
        return this.persistentDataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        HologramProperty<?> that = (HologramProperty<?>) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "HologramProperty[" +
                "name=" + this.name + ", " +
                "type=" + this.getValueType() +
                "]";
    }

}
