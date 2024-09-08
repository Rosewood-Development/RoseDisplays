package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import java.util.Objects;

public class HologramProperty<T> {

    private final String name;
    private final ArgumentHandler<T> argumentHandler;

    protected HologramProperty(String name, ArgumentHandler<T> argumentHandler) {
        this.name = name;
        this.argumentHandler = argumentHandler;
    }

    public String getName() {
        return this.name;
    }

    public ArgumentHandler<?> getArgumentHandler() {
        return this.argumentHandler;
    }

    public Class<T> getType() {
        return this.argumentHandler.getHandledType();
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
                "type=" + this.getType() +
                "]";
    }

}
