package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public class DisplayEntityHologram extends Hologram {

    private final int entityId;
    private final NMSHandler nmsHandler;
    private Location lastLocation;

    public DisplayEntityHologram(HologramType type) {
        super(type);
        this.verifyType(type);
        this.nmsHandler = NMSAdapter.getHandler();
        this.entityId = this.nmsHandler.getNextAvailableEntityId();
    }

    public DisplayEntityHologram(HologramType type, HologramPropertyContainer properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        super(type, properties, container, context);
        this.verifyType(type);
        this.nmsHandler = NMSAdapter.getHandler();
        this.entityId = this.nmsHandler.getNextAvailableEntityId();
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void update(Location location, Set<Player> players) {
        // TODO: Send a teleport packet if the location changes
        this.lastLocation = location;

        // Update dirty line properties and clean
        if (this.properties.isDirty()) {
            if (!players.isEmpty())
                this.nmsHandler.sendEntityMetadataPacket(this, players);
            this.properties.clean();
        }
    }

    @Override
    public void onWatcherAdded(Location location, Player player) {
        this.nmsHandler.sendEntitySpawnPacket(this, location, List.of(player));
    }

    @Override
    public void onWatcherRemoved(Player player) {
        this.nmsHandler.sendEntityDespawnPacket(this, List.of(player));
    }

    private void verifyType(HologramType type) {
        if (type != HologramType.TEXT_DISPLAY_ENTITY
                && type != HologramType.ITEM_DISPLAY_ENTITY
                && type != HologramType.BLOCK_DISPLAY_ENTITY)
            throw new IllegalArgumentException("Invalid HologramType, not a DisplayEntityHologram");
    }

}
