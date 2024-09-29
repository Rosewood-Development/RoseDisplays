package dev.rosewood.rosedisplays.hologram.view;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.Map;
import java.util.Set;

/**
 * A HologramPropertyView that can contain null property values to represent dirty values that have been unset
 */
public class DirtyHologramPropertyView implements HologramPropertyView {

    private final HologramPropertyTag tag;
    private final Map<HologramProperty<?>, Object> properties;

    public DirtyHologramPropertyView(HologramPropertyTag tag, Map<HologramProperty<?>, Object> properties) {
        this.tag = tag;
        this.properties = properties;
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
        throw new UnsupportedOperationException("This HologramPropertyView is read-only");
    }

    @Override
    public void unset(HologramProperty<?> property) {
        throw new UnsupportedOperationException("This HologramPropertyView is read-only");
    }

    @Override
    public boolean has(HologramProperty<?> property) {
        return this.properties.containsKey(property);
    }

    @Override
    public Set<HologramProperty<?>> getProperties() {
        return this.properties.keySet();
    }

}
