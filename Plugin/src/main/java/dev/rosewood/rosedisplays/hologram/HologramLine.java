package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import java.util.Objects;

public class HologramLine {

    private final HologramLineType type;
    private final HologramProperties properties;

    private final int entityId;

    public HologramLine(HologramLineType type) {
        this(type, new HologramProperties());
    }

    public HologramLine(HologramLineType type, HologramProperties properties) {
        this(type, properties, NMSAdapter.getHandler().getNextAvailableEntityId());
    }

    private HologramLine(HologramLineType type, HologramProperties properties, int entityId) {
        this.type = type;
        this.properties = properties;
        this.entityId = entityId;
    }

    public HologramLineType getType() {
        return this.type;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public HologramProperties getProperties() {
        return this.properties;
    }

    public boolean isDirty() {
        return this.properties.isDirty();
    }

    public HologramLine copy() {
        return new HologramLine(this.type, this.properties.copy(), this.entityId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        HologramLine that = (HologramLine) o;
        return this.type == that.type && this.entityId == that.entityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.entityId);
    }

}
