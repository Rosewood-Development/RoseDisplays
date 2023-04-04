package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

public record UnloadedHologram(String id, ChunkLocation chunkLocation) {

    private final static NamespacedKey HOLOGRAM_KEY = new NamespacedKey(RoseDisplays.getInstance(), "holograms");

    public static List<UnloadedHologram> fromWorld(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        UnloadedHologram[] unloadedHolograms = pdc.get(HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY);
        return unloadedHolograms == null ? List.of() : List.of(unloadedHolograms);
    }

    public static void saveWorld(World world, List<UnloadedHologram> unloadedHolograms) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        pdc.set(HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY, unloadedHolograms.toArray(new UnloadedHologram[0]));
    }

}
