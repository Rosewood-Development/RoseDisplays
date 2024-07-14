package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.renderer.SingleHologramRenderer;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Hologram extends UnloadedHologram {

    private final Location location;
    private final HologramRenderer renderer;
    private final List<HologramLine> lines;

    public Hologram(String name, Location location) {
        super(name, ChunkLocation.of(location));
        this.location = location;
        this.renderer = new SingleHologramRenderer(this);
        this.lines = new ArrayList<>();
    }

    public Location getLocation() {
        return this.location;
    }

    public HologramRenderer getRenderer() {
        return this.renderer;
    }

    public List<HologramLine> getLines() {
        return this.lines;
    }

    public int addLine(HologramLine line) {
        this.lines.add(line);
        return this.lines.size();
    }

    public int addLines(List<HologramLine> lines) {
        this.lines.addAll(lines);
        return this.lines.size();
    }

    public void removeLine(int index) {
        this.lines.remove(index);
    }

    public boolean isInRange(Player player) {
        return player.getWorld().equals(this.location.getWorld()) && this.location.distanceSquared(player.getLocation()) <= 64 * 64;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void removeAllWatchers() {
        this.renderer.removeAllWatchers();
    }

    public UnloadedHologram asUnloaded() {
        return new UnloadedHologram(this.name, this.chunkLocation);
    }

    @Override
    public String toString() {
        return "Hologram[" +
                "name=" + this.name + ", " +
                "location=" + this.location + ", " +
                "chunkLocation=" + this.chunkLocation + ']';
    }

}
