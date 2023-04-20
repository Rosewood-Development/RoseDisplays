package dev.rosewood.rosedisplays.nms;

import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.VersionAvailabilityProvider;
import java.util.Collection;
import org.bukkit.entity.Player;

public interface NMSHandler {

    void sendHologramSpawnPacket(HologramLine hologramLine, Collection<Player> players);

    void sendHologramMetadataPacket(HologramLine hologramLine, Collection<Player> players);

    void sendHologramDespawnPacket(HologramLine hologramLine, Collection<Player> players);

    int getNextAvailableEntityId();

    VersionAvailabilityProvider getVersionAvailabilityProvider();

}
