package dev.rosewood.rosedisplays.hologram.view;

import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import java.util.Set;
import java.util.function.Supplier;

public interface HologramPropertyView {

    /**
     * @return the tag of allowed properties in this view
     */
    HologramPropertyTag getTag();

    /**
     * Gets a HologramProperty value or null if it isn't set.
     *
     * @param property The HologramProperty to get the value of
     * @return The HologramProperty value or null if it isn't set
     * @param <T> The HologramProperty value type
     */
    <T> T get(HologramProperty<T> property);

    /**
     * Gets a HologramProperty value or null if it isn't set.
     *
     * @param property The HologramProperty to get the value of
     * @return The HologramProperty value
     * @throws IllegalStateException if the HologramProperty does not have a default setting defined
     * @param <T> The HologramProperty value type
     */
    default <T> T getOrDefault(HologramProperty<T> property) {
        T value = this.get(property);
        if (value == null)
            value = SettingKey.getDefault(property);
        if (value == null)
            throw new IllegalStateException("HologramProperty default value is not defined and was not provided");
        return value;
    }

    /**
     * Gets a HologramProperty value or the default value if it isn't set.
     *
     * @param property The HologramProperty to get the value of
     * @param defaultValue The default value to return in the case the property is not set
     * @return The HologramProperty value or null if it isn't set
     * @param <T> The HologramProperty value type
     */
    default <T> T getOrDefault(HologramProperty<T> property, T defaultValue) {
        T value = this.get(property);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a HologramProperty value or calls the default value supplier and returns its value if the property isn't set.
     *
     * @param property The HologramProperty to get the value of
     * @param defaultValueSupplier A supplier to provide the default value to return in the case the property is not set
     * @return The HologramProperty value or if not set, calls the given supplier and returns its value
     * @param <T> The HologramProperty value type
     */
    default <T> T getOrDefault(HologramProperty<T> property, Supplier<T> defaultValueSupplier) {
        T value = this.get(property);
        return value != null ? value : defaultValueSupplier.get();
    }

    /**
     * Sets a HologramProperty value, overwrites the existing value.
     *
     * @param property The HologramProperty to set the value of
     * @param value The value to set
     * @throws IllegalArgumentException if the property is not applicable for this hologram
     */
    <T> void set(HologramProperty<T> property, T value);

    /**
     * Unsets a HologramProperty value.
     *
     * @param property The HologramProperty to unset the value of
     */
    void unset(HologramProperty<?> property);

    /**
     * Checks if a HologramProperty is set.
     *
     * @param property The HologramProperty to check for
     * @return true if the HologramProperty exists, false otherwise
     */
    boolean has(HologramProperty<?> property);

    /**
     * @return an unmodifiable set of the properties contained within
     */
    Set<HologramProperty<?>> getProperties();

}
