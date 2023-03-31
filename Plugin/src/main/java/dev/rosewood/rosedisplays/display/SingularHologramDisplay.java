package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SingularHologramDisplay extends Display {

    //private static final double HOLOGRAM_OFFSET = 0.225D;
    private static final double HOLOGRAM_OFFSET = 0.2D;

    private int entityId = -1;

    public SingularHologramDisplay(DataSource dataSource, Location location) {
        super(DisplayType.DISPLAY_ENTITY, false, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {
        if (this.entityId == -1)
            this.entityId = NMSAdapter.getHandler().getNextAvailableEntityId();

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        Location baseLocation = this.location.clone().add(0, HOLOGRAM_OFFSET * this.height, 0);
        nmsHandler.sendHologramSpawnPacket(players, this.entityId, baseLocation.clone());
        nmsHandler.sendHologramMetadataPacket(players, this.entityId, frameData);
    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        if (this.entityId == -1)
            return;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        nmsHandler.sendHologramMetadataPacket(players, this.entityId, frameData);
    }

    @Override
    protected void destroy(Set<Player> players) {
        if (this.entityId == -1)
            return;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        nmsHandler.sendHologramDespawnPacket(players, this.entityId);
    }

}
