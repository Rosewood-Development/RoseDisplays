package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public class DisplayEntityHologram extends Hologram {

    protected final DisplayEntityType displayEntityType;
    protected final int entityId;
    protected final NMSHandler nmsHandler;
    protected Location lastLocation;

    public DisplayEntityHologram(HologramType type) {
        super(type);
        this.displayEntityType = DisplayEntityType.getByHologramType(type);
        this.nmsHandler = NMSAdapter.getHandler();
        this.entityId = this.nmsHandler.getNextAvailableEntityId();
    }

    public DisplayEntityHologram(HologramType type, DirtyingHologramPropertyView properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        super(type, properties, container, context);
        this.displayEntityType = DisplayEntityType.getByHologramType(type);
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
                this.nmsHandler.sendEntityMetadataPacket(this.entityId, this.properties.getDirty(), players);
            this.properties.clean();
        }
    }

    @Override
    public void onWatcherAdded(Location location, Player player) {
        this.nmsHandler.sendEntitySpawnPacket(this.entityId, this.properties, this.displayEntityType.getEntityType(), location, List.of(player));
    }

    @Override
    public void onWatcherRemoved(Player player) {
        this.nmsHandler.sendEntityDespawnPacket(this.entityId, List.of(player));
    }

    protected enum DisplayEntityType {
        TEXT(HologramType.TEXT_DISPLAY_ENTITY, EntityType.TEXT_DISPLAY),
        ITEM(HologramType.ITEM_DISPLAY_ENTITY, EntityType.ITEM_DISPLAY),
        BLOCK(HologramType.BLOCK_DISPLAY_ENTITY, EntityType.BLOCK_DISPLAY);

        private static final Map<HologramType, DisplayEntityType> BY_HOLOGRAM_TYPE;
        static {
            BY_HOLOGRAM_TYPE = Arrays.stream(values()).collect(Collectors.toMap(DisplayEntityType::getHologramType, Function.identity()));
        }

        private final HologramType hologramType;
        private final EntityType entityType;

        DisplayEntityType(HologramType hologramType, EntityType entityType) {
            this.hologramType = hologramType;
            this.entityType = entityType;
        }

        public HologramType getHologramType() {
            return this.hologramType;
        }

        public EntityType getEntityType() {
            return this.entityType;
        }

        public static DisplayEntityType getByHologramType(HologramType type) {
            DisplayEntityType value = BY_HOLOGRAM_TYPE.get(type);
            if (value == null)
                throw new IllegalArgumentException("Invalid HologramType, not a DisplayEntityHologram");
            return value;
        }
    }

}
