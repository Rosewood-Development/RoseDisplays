package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public abstract class Hologram {

    protected final HologramType type;
    protected final HologramPropertyContainer properties;

    public Hologram(HologramType type) {
        this.type = type;
        this.properties = new HologramPropertyContainer(type.tag());
    }

    public Hologram(HologramType type, HologramPropertyContainer properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        if (!properties.getTag().equals(type.tag()))
            throw new IllegalArgumentException("Invalid properties for the given hologram type");

        this.type = type;
        this.properties = properties;
        this.readAdditionalPDCData(container, context);
    }

    public HologramType getType() {
        return this.type;
    }

    public HologramPropertyContainer getProperties() {
        return this.properties;
    }

    public abstract void update(Location location, Set<Player> players);

    public void onWatcherAdded(Location location, Player player) {

    }

    public void onWatcherRemoved(Player player) {

    }

    public void writeAdditionalPDCData(PersistentDataContainer container, PersistentDataAdapterContext context) {

    }

    public void readAdditionalPDCData(PersistentDataContainer container, PersistentDataAdapterContext context) {

    }

}
