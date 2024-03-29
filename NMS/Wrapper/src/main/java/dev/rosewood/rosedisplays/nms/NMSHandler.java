package dev.rosewood.rosedisplays.nms;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NMSHandler {

    void sendHologramSpawnPacket(Collection<Player> players, int entityId, Location location);

    void sendHologramMetadataPacket(Collection<Player> players, int entityId, int[] frameData);

    void sendHologramDespawnPacket(Collection<Player> players, int entityId);

    int getNextAvailableEntityId();

}
