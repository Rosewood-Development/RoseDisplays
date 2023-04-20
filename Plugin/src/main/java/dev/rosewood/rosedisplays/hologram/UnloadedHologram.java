package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import java.util.Arrays;
import java.util.List;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

public record UnloadedHologram(String name, ChunkLocation chunkLocation) {

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

}
