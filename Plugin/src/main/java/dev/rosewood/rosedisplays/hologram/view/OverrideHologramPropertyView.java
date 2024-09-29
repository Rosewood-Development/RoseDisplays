package dev.rosewood.rosedisplays.hologram.view;

import com.google.common.collect.Sets;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.Set;

public class OverrideHologramPropertyView extends DirtyingHologramPropertyView {

    private final DirtyingHologramPropertyView delegate;

    public OverrideHologramPropertyView(DirtyingHologramPropertyView delegate) {
        super(delegate.getTag());
        this.delegate = delegate;
    }

    @Override
    public HologramPropertyTag getTag() {
        return this.delegate.getTag();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(HologramProperty<T> property) {
        Object thisValue = this.properties.get(property);
        if (thisValue != null)
            return (T) thisValue;
        return this.delegate.get(property);
    }

    @Override
    public <T> void set(HologramProperty<T> property, T value) {
        if (!this.delegate.getTag().contains(property))
            throw new IllegalArgumentException("HologramProperty " + property.key() + " is not applicable");
        this.properties.put(property, value);
        this.dirtyProperties.add(property);
    }

    @Override
    public void unset(HologramProperty<?> property) {
        this.properties.remove(property);
    }

    @Override
    public boolean has(HologramProperty<?> property) {
        return this.properties.containsKey(property) || this.delegate.has(property);
    }

    @Override
    public Set<HologramProperty<?>> getProperties() {
        return Sets.union(this.properties.keySet(), this.delegate.getProperties());
    }

    @Override
    public HologramPropertyView getDirty() {
        return new CompositeHologramPropertyView(this, this.delegate.getDirty());
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || this.delegate.isDirty();
    }

}
