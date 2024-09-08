package dev.rosewood.rosedisplays.hologram.renderer;

import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.type.DisplayEntityHologram;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class DisplayEntityHologramRenderer implements HologramRenderer {

    private final DisplayEntityHologram hologram;
    private final Set<Player> watchers;
    private final NMSHandler nmsHandler;

    public DisplayEntityHologramRenderer(DisplayEntityHologram hologram) {
        this.hologram = hologram;
        this.watchers = ConcurrentHashMap.newKeySet();
        this.nmsHandler = NMSAdapter.getHandler();
    }

    @Override
    public void update() {
        HologramPropertyContainer properties = this.hologram.getProperties();

        // Update dirty line properties and clean
        if (properties.isDirty()) {
            this.nmsHandler.sendEntityMetadataPacket(this.hologram, this.watchers);
            properties.clean();
        }
    }

    @Override
    public void addWatcher(Player player) {
        this.watchers.add(player);
        this.nmsHandler.sendEntitySpawnPacket(this.hologram, List.of(player));
    }

    @Override
    public void removeWatcher(Player player) {
        this.nmsHandler.sendEntityDespawnPacket(this.hologram, List.of(player));
        this.watchers.remove(player);
    }

    @Override
    public void removeAllWatchers() {
        if (this.watchers.isEmpty())
            return;

        this.nmsHandler.sendEntityDespawnPacket(this.hologram, this.watchers);
        this.watchers.clear();
    }

    @Override
    public boolean isWatching(Player player) {
        return this.watchers.contains(player);
    }

}
