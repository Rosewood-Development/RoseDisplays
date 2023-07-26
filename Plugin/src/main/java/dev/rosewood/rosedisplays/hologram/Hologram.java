package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.renderer.BasicHologramRenderer;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class Hologram extends UnloadedHologram {

    private final String name;
    private final Location location;
    private final HologramRenderer renderer;
    private final List<HologramLine> lines;

    public Hologram(String name, Location location) {
        super(name, ChunkLocation.of(location));
        this.name = name;
        this.location = location;
        this.renderer = new BasicHologramRenderer();
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return this.name;
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

    public void removeLine(int index) {
        this.lines.remove(index);
    }

}
