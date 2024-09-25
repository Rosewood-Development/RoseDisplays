package dev.rosewood.rosedisplays.nms;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Parameter types from the Plugin module are just Object due to cyclic dependencies.
 * The types are cast to their expected types in the NMSHandler implementations.
 * Parameter types are as specified in the comment above the methods.
 * TODO: Find an alternative to this
 */
public interface NMSHandler {

    // int entityId, HologramPropertyView, EntityType, Location, Collection<Player>
    // Also sends a metadata packet
    void sendEntitySpawnPacket(int entityId, Object hologramArg, EntityType entityType, Location location, Collection<Player> players);

    // int entityId, HologramPropertyView, Collection<Player>
    void sendEntityMetadataPacket(int entityId, Object hologramArg, Collection<Player> players);

    // int entityId, Collection<Player>
    void sendEntityDespawnPacket(int entityId, Collection<Player> players);

    // int entityId, Entity, Collection<Player>
    default void sendHologramSetVehiclePacket(int entityId, Entity vehicle, Collection<Player> players) {

    }

    int getNextAvailableEntityId();

    // HologramProperty<?>
    boolean isPropertyAvailable(Object propertyArg);

}
