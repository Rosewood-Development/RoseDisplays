package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.nms.NMSAdapter;

public class HologramLine {

    private final HologramLineType type;
    private final HologramProperties properties;
    private final int entityId;

    public HologramLine(HologramLineType type) {
        this(type, new HologramProperties());
    }

    public HologramLine(HologramLineType type, HologramProperties properties) {
        this.type = type;
        this.properties = properties;
        this.entityId = NMSAdapter.getHandler().getNextAvailableEntityId();
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

}
