package dev.rosewood.rosedisplays.hologram.property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class HologramProperties {

    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirtyProperties;

    public HologramProperties() {
        this.properties = new HashMap<>();
        this.dirtyProperties = new HashSet<>();
    }

    public <T> T get(HologramProperty<T> property) {
        return property.getType().cast(this.properties.get(property));
    }

    public <T> void set(HologramProperty<T> property, T value) {
        this.properties.put(property, value);
        this.dirtyProperties.add(property);
    }

    public void unset(HologramProperty<?> property) {
        this.properties.remove(property);
        this.dirtyProperties.add(property);
    }

    public boolean has(HologramProperty<?> property) {
        return this.properties.containsKey(property);
    }

    public HologramProperties getDirty() {
        HologramProperties copy = new HologramProperties();
        for (HologramProperty<?> property : this.dirtyProperties) {
            copy.properties.put(property, this.properties.get(property));
            copy.dirtyProperties.add(property);
        }
        this.dirtyProperties.clear();
        return copy;
    }

    public boolean isDirty() {
        return !this.dirtyProperties.isEmpty();
    }

    public void forEach(BiConsumer<HologramProperty<?>, Object> consumer) {
        for (HologramProperty<?> property : this.properties.keySet())
            consumer.accept(property, this.properties.get(property));
    }

}
