package dev.rosewood.rosedisplays.nms;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Parameter types from the Plugin module are just Object due to cyclic dependencies.
 * The types are cast to their expected types in the NMSHandler implementations.
 * Parameter types are as specified in the comment above the methods.
 * TODO: Find an alternative to this
 */
public interface NMSHandler {

    // DisplayEntityHologram, Location, Collection<Player>
    // Also sends a metadata packet
    void sendEntitySpawnPacket(Object hologramArg, Location location, Collection<Player> players);

    // DisplayEntityHologram, Collection<Player>
    void sendEntityMetadataPacket(Object hologramArg, Collection<Player> players);

    // DisplayEntityHologram, Collection<Player>
    void sendEntityDespawnPacket(Object hologramArg, Collection<Player> players);

    // DisplayEntityHologram, Entity, Collection<Player>
    default void sendHologramSetVehiclePacket(Object hologramArg, Entity vehicle, Collection<Player> players) {

    }

    int getNextAvailableEntityId();

    // HologramProperty<?>
    boolean isPropertyAvailable(Object propertyArg);

}
