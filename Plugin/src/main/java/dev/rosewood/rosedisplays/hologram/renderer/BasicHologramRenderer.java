package dev.rosewood.rosedisplays.hologram.renderer;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class BasicHologramRenderer implements HologramRenderer {

    private final Hologram hologram;
    private final Set<Player> watchers;
    private final NMSHandler nmsHandler;

    public BasicHologramRenderer(Hologram hologram) {
        this.hologram = hologram;
        this.watchers = ConcurrentHashMap.newKeySet();
        this.nmsHandler = NMSAdapter.getHandler();
    }

    @Override
    public void update() {
        this.hologram.getLines().stream()
                .filter(HologramLine::isDirty)
                .forEach(line -> this.nmsHandler.sendHologramMetadataPacket(line, this.watchers));
    }

    @Override
    public void addWatcher(Player player) {
        RoseDisplays.getInstance().getLogger().warning("Adding watcher " + player.getName() + " to hologram " + this.hologram.getName());
        this.watchers.add(player);
        // TODO: Offset line location by index
        this.hologram.getLines().forEach(line -> this.nmsHandler.sendHologramSpawnPacket(line, this.hologram.getLocation(), List.of(player)));
    }

    @Override
    public void removeWatcher(Player player) {
        RoseDisplays.getInstance().getLogger().warning("Removing watcher " + player.getName() + " from hologram " + this.hologram.getName());
        this.watchers.remove(player);
        this.hologram.getLines().forEach(line -> this.nmsHandler.sendHologramDespawnPacket(line, List.of(player)));
    }

    @Override
    public void removeAllWatchers() {
        RoseDisplays.getInstance().getLogger().warning("Removing all watchers from hologram " + this.hologram.getName());
        this.hologram.getLines().forEach(line -> this.nmsHandler.sendHologramDespawnPacket(line, this.watchers));
        this.watchers.clear();
    }

    @Override
    public boolean isWatching(Player player) {
        return this.watchers.contains(player);
    }

}
