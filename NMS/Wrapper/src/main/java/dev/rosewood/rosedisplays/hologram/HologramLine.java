package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;

public class HologramLine {

    private final HologramLineType type;
    private final Location location;
    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirtyProperties;

    public HologramLine(HologramLineType type, Location location) {
        this.type = type;
        this.location = location;
        this.properties = new HashMap<>();
        this.dirtyProperties = new HashSet<>();
    }

    public HologramLineType getType() {
        return this.type;
    }

    public Location getLocation() {
        return this.location;
    }

    public Map<HologramProperty<?>, ?> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public Map<HologramProperty<?>, ?> getDirtyProperties() {
        if (this.dirtyProperties.isEmpty())
            return Map.of();

        Map<HologramProperty<?>, Object> dirtyProperties = new HashMap<>();
        for (HologramProperty<?> property : this.dirtyProperties)
            dirtyProperties.put(property, this.properties.get(property));

        this.dirtyProperties.clear();
        return dirtyProperties;
    }

    public <T> void setProperty(HologramProperty<T> property, T value) {
        this.properties.put(property, value);
        this.dirtyProperties.add(property);
    }

    public void unsetProperty(HologramProperty<?> property) {
        this.properties.remove(property);
        this.dirtyProperties.add(property);
    }

    public <T> T getProperty(HologramProperty<T> property) {
        return property.getType().cast(this.properties.get(property));
    }

    public HologramLine transform(HologramLineType newType) {
        HologramLine newLine = new HologramLine(newType, this.location);
        newLine.properties.putAll(this.properties);
        return newLine;
    }

}
