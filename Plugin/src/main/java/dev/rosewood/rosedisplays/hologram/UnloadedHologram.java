package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.model.ChunkLocation;
import java.util.Objects;

public class UnloadedHologram {

    protected final String name;
    protected final ChunkLocation chunkLocation;

    public UnloadedHologram(String name, ChunkLocation chunkLocation) {
        this.name = name;
        this.chunkLocation = chunkLocation;
    }

    public String getName() {
        return this.name;
    }

    public ChunkLocation getChunkLocation() {
        return this.chunkLocation;
    }

    public boolean isLoaded() {
        return false;
    }

    public void removeAllWatchers() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UnloadedHologram) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "UnloadedHologram[" +
                "name=" + this.name + ", " +
                "chunkLocation=" + this.chunkLocation + ']';
    }

}
