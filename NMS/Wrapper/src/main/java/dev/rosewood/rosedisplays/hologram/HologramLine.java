package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import org.bukkit.Location;

public class HologramLine {

    private final HologramLineType type;
    private final Location location;
    private final HologramProperties properties;
    private final int entityId;

    public HologramLine(HologramLineType type, Location location) {
        this.type = type;
        this.location = location;
        this.properties = new HologramProperties();
        this.entityId = NMSAdapter.getHandler().getNextAvailableEntityId();
    }

    public HologramLineType getType() {
        return this.type;
    }

    public Location getLocation() {
        return this.location;
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
