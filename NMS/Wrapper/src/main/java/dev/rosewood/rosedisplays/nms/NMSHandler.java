package dev.rosewood.rosedisplays.nms;

import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.VersionAvailabilityProvider;
import java.util.Collection;
import org.bukkit.entity.Player;

public interface NMSHandler {

    void sendHologramSpawnPacket(HologramLine hologramLine, int entityId, Collection<Player> players);

    void sendHologramMetadataPacket(HologramLine hologramLine, int entityId, Collection<Player> players);

    void sendHologramDespawnPacket(Collection<Player> players, int entityId);

    int getNextAvailableEntityId();

    VersionAvailabilityProvider getVersionAvailablilityProvider();

}
