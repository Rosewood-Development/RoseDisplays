package dev.rosewood.rosedisplays.hologram.view;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;

public final class DirtyingHologramPropertyView implements HologramPropertyView {

    private final HologramPropertyTag tag;
    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirtyProperties;

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
            throw new IllegalArgumentException("HologramProperty " + property.getName() + " is not applicable");
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
    @ApiStatus.Internal
    public void clean() {
        this.dirtyProperties.clear();
    }

    /**
     * @return a view of this DirtyingHologramPropertyView with dirty properties still present but set to null
     */
    @ApiStatus.Internal
    public HologramPropertyView getDirty() {
        Map<HologramProperty<?>, Object> propertiesMap = new HashMap<>(this.properties);
        for (HologramProperty<?> dirtyProperty : this.dirtyProperties)
            if (!propertiesMap.containsKey(dirtyProperty))
                propertiesMap.put(dirtyProperty, null);
        return new UnmodifiableHologramPropertyView(this.tag, propertiesMap);
    }

    /**
     * @return true if any HologramProperties contained within are dirty and still need to be rendered
     */
    @ApiStatus.Internal
    public boolean isDirty() {
        return !this.dirtyProperties.isEmpty();
    }

    /**
     * @return the amount of HologramProperties stored within
     */
    public int size() {
        return this.properties.size();
    }

    /**
     * Calls the BiConsumer input for each HologramProperty
     *
     * @param consumer the BiConsumer to call for each element with a HologramProperty and its value
     */
    public void forEach(BiConsumer<? super HologramProperty<?>, ? super Object> consumer) {
        this.properties.forEach(consumer);
    }

}
