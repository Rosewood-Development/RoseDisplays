package dev.rosewood.rosedisplays.hologram.renderer;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class SingleHologramRenderer implements HologramRenderer {

    private final Hologram hologram;
    private final Set<Player> watchers;
    private final NMSHandler nmsHandler;
    private List<HologramLine> lastLineStates;

    public SingleHologramRenderer(Hologram hologram) {
        this.hologram = hologram;
        this.watchers = ConcurrentHashMap.newKeySet();
        this.nmsHandler = NMSAdapter.getHandler();
        this.lastLineStates = List.of();
    }

    @Override
    public void update() {
        List<HologramLine> newLineStates = this.hologram.getLines();

        // If any lines have changed types or been removed we need to respawn all hologram lines
        if (!this.lastLineStates.isEmpty() && !this.lastLineStates.equals(newLineStates)) {
            this.lastLineStates.forEach(line -> this.nmsHandler.sendHologramDespawnPacket(line, this.watchers));
            newLineStates.forEach(line -> this.nmsHandler.sendHologramSpawnPacket(line, this.hologram.getLocation(), this.watchers));
            this.lastLineStates = newLineStates.stream().map(HologramLine::copy).toList();
            return; // Spawning also updates the metadata
        }

        // Update last line states for packets that are needed between updates
        this.lastLineStates = newLineStates.stream().map(HologramLine::copy).toList();

        // Update dirty line properties
        newLineStates.stream()
                .filter(HologramLine::isDirty)
                .forEach(line -> this.nmsHandler.sendHologramMetadataPacket(line, this.watchers));
    }

    @Override
    public void addWatcher(Player player) {
        this.watchers.add(player);
        this.lastLineStates.forEach(line -> this.nmsHandler.sendHologramSpawnPacket(line, this.hologram.getLocation(), List.of(player)));
    }

    @Override
    public void removeWatcher(Player player) {
        this.watchers.remove(player);
        this.lastLineStates.forEach(line -> this.nmsHandler.sendHologramDespawnPacket(line, List.of(player)));
    }

    @Override
    public void removeAllWatchers() {
        this.lastLineStates.forEach(line -> this.nmsHandler.sendHologramDespawnPacket(line, this.watchers));
        this.watchers.clear();
    }

    @Override
    public boolean isWatching(Player player) {
        return this.watchers.contains(player);
    }

}
