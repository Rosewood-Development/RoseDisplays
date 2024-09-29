package dev.rosewood.rosedisplays.hologram.view;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DirtyingHologramPropertyView implements HologramPropertyView {

    private final HologramPropertyTag tag;
    protected final Map<HologramProperty<?>, Object> properties;
    protected final Set<HologramProperty<?>> dirtyProperties;

    public DirtyingHologramPropertyView(HologramPropertyTag tag) {
        this.tag = tag;
        this.properties = new HashMap<>();
        this.dirtyProperties = new HashSet<>();
    }

    public DirtyingHologramPropertyView(HologramPropertyTag tag, Map<HologramProperty<?>, Object> properties) {
        this.tag = tag;
        this.properties = properties.entrySet().stream()
                .filter(x -> tag.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y));
        this.dirtyProperties = new HashSet<>();
    }

    @Override
    public HologramPropertyTag getTag() {
        return this.tag;
    }

    @Override
    public <T> T get(HologramProperty<T> property) {
        return property.getValueType().cast(this.properties.get(property));
    }

    @Override
    public <T> void set(HologramProperty<T> property, T value) {
        if (!this.tag.contains(property))
            throw new IllegalArgumentException("HologramProperty " + property.key() + " is not applicable");
        this.properties.put(property, value);
        this.dirtyProperties.add(property);
    }

    @Override
    public void unset(HologramProperty<?> property) {
        this.properties.remove(property);
        this.dirtyProperties.add(property);
    }

    @Override
    public boolean has(HologramProperty<?> property) {
        return this.properties.containsKey(property);
    }

    @Override
    public Set<HologramProperty<?>> getProperties() {
        return this.properties.keySet();
    }

    /**
     * Removes all dirty HologramProperties.
     */
    public void clean() {
        this.dirtyProperties.clear();
    }

    /**
     * @return a view of this DirtyingHologramPropertyView with dirty properties still present but set to null
     */
    public HologramPropertyView getDirty() {
        Map<HologramProperty<?>, Object> propertiesMap = new HashMap<>(this.properties);
        for (HologramProperty<?> dirtyProperty : this.dirtyProperties)
            if (!propertiesMap.containsKey(dirtyProperty))
                propertiesMap.put(dirtyProperty, null);
        return new DirtyHologramPropertyView(this.tag, propertiesMap);
    }

    /**
     * @return true if any HologramProperties contained within are dirty and still need to be rendered
     */
    public boolean isDirty() {
        return !this.dirtyProperties.isEmpty();
    }

}
