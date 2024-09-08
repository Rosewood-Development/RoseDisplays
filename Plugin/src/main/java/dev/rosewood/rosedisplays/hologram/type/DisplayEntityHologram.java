package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.DisplayEntityType;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.renderer.DisplayEntityHologramRenderer;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import org.bukkit.Location;

public class DisplayEntityHologram extends Hologram {

    private final HologramRenderer renderer;
    private final DisplayEntityType type;
    private final HologramPropertyContainer properties;
    private final int entityId;
    private final Location location;

    public DisplayEntityHologram(String name, DisplayEntityType type, Location location) {
        this(name, type, location, new HologramPropertyContainer(type.getTag()));
    }

    public DisplayEntityHologram(String name, DisplayEntityType type, Location location, HologramPropertyContainer properties) {
        this(name, type, location, properties, NMSAdapter.getHandler().getNextAvailableEntityId());
    }

    private DisplayEntityHologram(String name, DisplayEntityType type, Location location, HologramPropertyContainer properties, int entityId) {
        super(name);
        this.type = type;
        this.location = location;
        this.properties = properties;
        this.entityId = entityId;
        this.renderer = new DisplayEntityHologramRenderer(this);
    }

    public DisplayEntityType getType() {
        return this.type;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public HologramPropertyContainer getProperties() {
        return this.properties;
    }

    @Override
    public HologramRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

}
