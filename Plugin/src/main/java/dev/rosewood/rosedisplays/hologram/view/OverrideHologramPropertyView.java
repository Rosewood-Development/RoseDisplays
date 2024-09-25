package dev.rosewood.rosedisplays.hologram.view;

import com.google.common.collect.Sets;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OverrideHologramPropertyView implements HologramPropertyView {

    private final HologramPropertyView delegate;
    private final Map<HologramProperty<?>, Object> overrideProperties;

    public OverrideHologramPropertyView(HologramPropertyView delegate) {
        this.delegate = delegate;
        this.overrideProperties = new HashMap<>();
    }

    @Override
    public HologramPropertyTag getTag() {
        return this.delegate.getTag();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(HologramProperty<T> property) {
        Object thisValue = this.overrideProperties.get(property);
        if (thisValue != null)
            return (T) thisValue;
        return this.delegate.get(property);
    }

    @Override
    public <T> void set(HologramProperty<T> property, T value) {
        this.overrideProperties.put(property, value);
    }

    @Override
    public void unset(HologramProperty<?> property) {
        this.overrideProperties.remove(property);
    }

    @Override
    public boolean has(HologramProperty<?> property) {
        return this.overrideProperties.containsKey(property) || this.delegate.has(property);
    }

    @Override
    public Set<HologramProperty<?>> getProperties() {
        return Sets.union(this.overrideProperties.keySet(), this.delegate.getProperties());
    }

}
