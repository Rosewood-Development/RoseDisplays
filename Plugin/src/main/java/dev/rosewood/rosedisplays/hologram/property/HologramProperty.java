package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.registry.RoseKey;
import dev.rosewood.rosegarden.registry.RoseKeyed;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import java.util.Objects;
import org.bukkit.persistence.PersistentDataType;

public sealed class HologramProperty<T> implements RoseKeyed permits MappedHologramProperty {

    private final RoseKey key;
    private final ArgumentHandler<T> argumentHandler;
    private final PersistentDataType<?, T> persistentDataType;

    HologramProperty(RoseKey key, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        this.key = key;
        this.argumentHandler = argumentHandler;
        this.persistentDataType = persistentDataType;
    }

    public RoseKey key() {
        return this.key;
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
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return "HologramProperty[" +
                "key=" + this.key + ", " +
                "type=" + this.getValueType() +
                "]";
    }

}
