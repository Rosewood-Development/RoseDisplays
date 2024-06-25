package dev.rosewood.rosedisplays.nms;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Parameter types from the Plugin module are just Object due to cyclic dependencies.
 * The types are cast to their expected types in the NMSHandler implementations.
 * Parameter types are as specified in the comment above the methods.
 * TODO: Find an alternative to this
 */
public interface NMSHandler {

    // HologramLine, Location, Collection<Player>
    void sendHologramSpawnPacket(Object hologramLine, Location location, Collection<Player> players);

    // HologramLine, Collection<Player>
    void sendHologramMetadataPacket(Object hologramLine, Collection<Player> players);

    // HologramLine, Collection<Player>
    void sendHologramDespawnPacket(Object hologramLine, Collection<Player> players);

    int getNextAvailableEntityId();

    // HologramProperty<?>
    boolean isPropertyAvailable(Object property);

}
