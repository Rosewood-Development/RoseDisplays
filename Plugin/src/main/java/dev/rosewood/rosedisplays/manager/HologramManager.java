package dev.rosewood.rosedisplays.manager;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class HologramManager extends Manager implements Listener {

    private final Multimap<ChunkLocation, Hologram> loadedHolograms;
    private final Multimap<ChunkLocation, UnloadedHologram> unloadedHolograms;

    public HologramManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.loadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();
        this.unloadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        Bukkit.getWorlds().forEach(this::loadHolograms);
    }

    @Override
    public void disable() {
        Bukkit.getWorlds().forEach(this::unloadHolograms);

        this.loadedHolograms.clear();
        this.unloadedHolograms.clear();
    }

    public void loadHolograms(World world) {
        List<UnloadedHologram> unloadedHolograms = UnloadedHologram.fromWorld(world);
        unloadedHolograms.forEach(x -> this.unloadedHolograms.put(x.chunkLocation(), x));
        unloadedHolograms.stream()
                .map(UnloadedHologram::chunkLocation)
                .filter(chunkLocation -> world.isChunkLoaded(chunkLocation.x(), chunkLocation.z()))
                .map(chunkLocation -> world.getChunkAt(chunkLocation.x(), chunkLocation.z()))
                .distinct()
                .forEach(this::loadHolograms);
    }

    public void unloadHolograms(World world) {
        for (Chunk chunk : world.getLoadedChunks())
            this.unloadHolograms(chunk, true);
    }

    public void loadHolograms(Chunk chunk) {

    }

    public void unloadHolograms(Chunk chunk, boolean unloadingWorld) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        this.loadedHolograms.removeAll(chunkLocation);

        if (unloadingWorld) {

        } else {

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        this.loadHolograms(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.unloadHolograms(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        this.loadHolograms(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        this.unloadHolograms(event.getChunk(), false);
    }

}
