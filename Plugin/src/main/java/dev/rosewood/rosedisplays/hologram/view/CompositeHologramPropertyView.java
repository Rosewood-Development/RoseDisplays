package dev.rosewood.rosedisplays.hologram.view;

import com.google.common.collect.Sets;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.Set;

public class CompositeHologramPropertyView implements HologramPropertyView {

    private final HologramPropertyView primary;
    private final HologramPropertyView secondary;

    public CompositeHologramPropertyView(HologramPropertyView primary, HologramPropertyView secondary) {
        if (!primary.getTag().equals(secondary.getTag()))
            throw new IllegalArgumentException("Tags do not match");

        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public HologramPropertyTag getTag() {
        return this.primary.getTag();
    }

    @Override
    public <T> T get(HologramProperty<T> property) {
        T value = this.primary.get(property);
        if (value == null)
            value = this.secondary.get(property);
        return value;
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
        return this.primary.has(property) || this.secondary.has(property);
    }

    @Override
    public Set<HologramProperty<?>> getProperties() {
        return Sets.union(this.primary.getProperties(), this.secondary.getProperties());
    }

}
