package dev.rosewood.rosedisplays.hologram.property;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;

public final class HologramPropertyContainer {

    private final HologramPropertyTag tag;
    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirtyProperties;

    public HologramPropertyContainer(HologramPropertyTag tag) {
        this.tag = tag;
        this.properties = new HashMap<>();
        this.dirtyProperties = new HashSet<>();
    }

    public HologramPropertyContainer(HologramPropertyTag tag, Map<HologramProperty<?>, Object> properties) {
        this.tag = tag;
        this.properties = properties.entrySet().stream()
                .filter(x -> tag.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
        this.dirtyProperties = new HashSet<>();
    }

    /**
     * @return the HologramPropertyTag of HologramProperties that this container is capable of holding
     */
    public HologramPropertyTag getTag() {
        return this.tag;
    }

    /**
     * Gets a HologramProperty value or null if it isn't set.
     *
     * @param property The HologramProperty to get the value of
     * @return The HologramProperty value or null if it isn't set
     * @param <T> The HologramProperty value type
     */
    public <T> T get(HologramProperty<T> property) {
        return property.getType().cast(this.properties.get(property));
    }

    /**
     * Sets a HologramProperty value, overwrites the existing value.
     *
     * @param property The HologramProperty to set the value of
     * @param value The value to set
     * @throws IllegalArgumentException if the property is not applicable for this hologram
     */
    public <T> void set(HologramProperty<T> property, T value) {
        if (!this.tag.contains(property))
            throw new IllegalArgumentException("HologramProperty " + property.getName() + " is not applicable");
        this.properties.put(property, value);
        this.dirtyProperties.add(property);
    }

    /**
     * Unsets a HologramProperty value and returns it to its default value.
     *
     * @param property The HologramProperty to unset the value of
     */
    public void unset(HologramProperty<?> property) {
        this.properties.remove(property);
        this.dirtyProperties.add(property);
    }

    /**
     * Checks if a HologramProperty is set.
     *
     * @param property The HologramProperty to check for
     * @return true if the HologramProperty exists, false otherwise
     */
    public boolean has(HologramProperty<?> property) {
        return this.properties.containsKey(property);
    }

    /**
     * Removes all dirty HologramProperties.
     */
    @ApiStatus.Internal
    public void clean() {
        this.dirtyProperties.clear();
    }

    /**
     * Gets a copy of this HologramPropertyContainer with dirty properties still present.
     *
     * @return a copy of this HologramPropertyContainer with dirty properties still present
     */
    @ApiStatus.Internal
    public HologramPropertyContainer getDirty() {
        HologramPropertyContainer copy = this.copy();
        copy.dirtyProperties.addAll(this.dirtyProperties);
        return copy;
    }

    /**
     * @return true if any HologramProperties contained within are dirty and still need to be rendered
     */
    @ApiStatus.Internal
    public boolean isDirty() {
        return !this.dirtyProperties.isEmpty();
    }

    /**
     * @return a copy of this HologramPropertyContainer
     */
    public HologramPropertyContainer copy() {
        return new HologramPropertyContainer(this.tag, this.properties);
    }

    /**
     * @return the amount of HologramProperties stored within
     */
    public int size() {
        return this.properties.size();
    }

    /**
     * @return a map entry set of HologramProperties and their values
     */
    public Set<Map.Entry<HologramProperty<?>, Object>> entrySet() {
        return Collections.unmodifiableSet(this.properties.entrySet());
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
