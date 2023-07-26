package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

public class UnloadedHologram {

    protected final String name;
    protected final ChunkLocation chunkLocation;

    public UnloadedHologram(String name, ChunkLocation chunkLocation) {
        this.name = name;
        this.chunkLocation = chunkLocation;
    }

    public static List<UnloadedHologram> fromWorld(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        UnloadedHologram[] unloadedHolograms = pdc.get(HologramManager.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY);
        return unloadedHolograms == null ? List.of() : Arrays.asList(unloadedHolograms);
    }

    public static void saveWorld(World world, List<UnloadedHologram> unloadedHolograms) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        pdc.set(HologramManager.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY, unloadedHolograms.toArray(UnloadedHologram[]::new));
    }

    public static UnloadedHologram fromHologram(Hologram hologram) {
        return new UnloadedHologram(hologram.getName(), ChunkLocation.of(hologram.getLocation()));
    }

    public String getName() {
        return this.name;
    }

    public ChunkLocation getChunkLocation() {
        return this.chunkLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UnloadedHologram) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.chunkLocation, that.chunkLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.chunkLocation);
    }

    @Override
    public String toString() {
        return "UnloadedHologram[" +
                "name=" + this.name + ", " +
                "chunkLocation=" + this.chunkLocation + ']';
    }


}
