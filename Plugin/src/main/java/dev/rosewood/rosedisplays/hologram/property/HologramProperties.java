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

    public HologramProperties(Map<HologramProperty<?>, Object> properties) {
        this.properties = new HashMap<>(properties);
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
        HologramProperties copy = this.copy();
        copy.dirtyProperties.addAll(this.dirtyProperties);
        this.dirtyProperties.clear();
        return copy;
    }

    public boolean isDirty() {
        return !this.dirtyProperties.isEmpty();
    }

    public int size() {
        return this.properties.size();
    }

    public void forEach(BiConsumer<HologramProperty<?>, Object> consumer) {
        Set<HologramProperty<?>> combinedProperties = new HashSet<>(this.properties.keySet());
        combinedProperties.addAll(this.dirtyProperties);

        for (HologramProperty<?> property : combinedProperties)
            consumer.accept(property, this.properties.get(property));
    }

    public Map<HologramProperty<?>, Object> asMap() {
        return Map.copyOf(this.properties);
    }

    public HologramProperties copy() {
        return new HologramProperties(this.properties);
    }

}
