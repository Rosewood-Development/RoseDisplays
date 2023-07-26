package dev.rosewood.rosedisplays.manager;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class HologramManager extends Manager implements Listener {

    public final static NamespacedKey HOLOGRAM_KEY = new NamespacedKey(RoseDisplays.getInstance(), "holograms");

    private final Multimap<ChunkLocation, Hologram> loadedHolograms;
    private final Multimap<ChunkLocation, UnloadedHologram> unloadedHolograms;

    public HologramManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.loadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();
        this.unloadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        Bukkit.getScheduler().runTaskTimer(rosePlugin, this::tick, 10L, 10L); // TODO: Change this value and run async
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

    private void tick() {
        this.loadedHolograms.values().forEach(x -> x.getRenderer().render(x));
    }

    public Hologram createHologram(String name, Location location) {
        if (this.getHologram(name) != null)
            throw new IllegalArgumentException("A hologram with the name " + name + " already exists");

        Hologram hologram = new Hologram(name, location);
        this.loadedHolograms.put(ChunkLocation.of(location), hologram);
        return hologram;
    }

    public Hologram getHologram(String name) {
        Hologram loadedHologram = this.loadedHolograms.values().stream()
                .filter(hologram -> hologram.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (loadedHologram != null)
            return loadedHologram;

        UnloadedHologram unloadedHologram = this.unloadedHolograms.values().stream()
                .filter(hologram -> hologram.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (unloadedHologram == null)
            return null;

        // Force load the hologram
        ChunkLocation chunkLocation = unloadedHologram.getChunkLocation();
        World world = Bukkit.getWorld(chunkLocation.world());
        if (world == null)
            return null;

        // Load the chunk and holograms, keep the chunk loaded for minimum 30 seconds
        Chunk chunk = world.getChunkAt(chunkLocation.x(), chunkLocation.z());
        chunk.addPluginChunkTicket(RoseDisplays.getInstance());
        Bukkit.getScheduler().runTaskLater(RoseDisplays.getInstance(), () -> chunk.removePluginChunkTicket(RoseDisplays.getInstance()), 600L);
        this.loadHolograms(chunk);

        return this.loadedHolograms.values().stream()
                .filter(hologram -> hologram.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getHologramNames() {
        List<String> names = new ArrayList<>();
        this.loadedHolograms.values().forEach(hologram -> names.add(hologram.getName()));
        this.unloadedHolograms.values().forEach(hologram -> names.add(hologram.getName()));
        return names;
    }

    public void loadHolograms(World world) {
        List<UnloadedHologram> unloadedHolograms = UnloadedHologram.fromWorld(world);
        unloadedHolograms.forEach(x -> this.unloadedHolograms.put(x.getChunkLocation(), x));
        unloadedHolograms.stream()
                .map(UnloadedHologram::getChunkLocation)
                .distinct()
                .filter(chunkLocation -> world.isChunkLoaded(chunkLocation.x(), chunkLocation.z()))
                .map(chunkLocation -> world.getChunkAt(chunkLocation.x(), chunkLocation.z()))
                .forEach(this::loadHolograms);
    }

    public void unloadHolograms(World world) {
        for (Chunk chunk : world.getLoadedChunks())
            this.unloadHolograms(chunk);

        List<UnloadedHologram> worldHolograms = new ArrayList<>();
        for (UnloadedHologram unloadedHologram : this.unloadedHolograms.values())
            if (unloadedHologram.getChunkLocation().world().equals(world.getName()))
                worldHolograms.add(unloadedHologram);
        this.unloadedHolograms.values().removeAll(worldHolograms);

        UnloadedHologram.saveWorld(world, worldHolograms);
    }

    public void loadHolograms(Chunk chunk) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        Hologram[] holograms = pdc.get(HOLOGRAM_KEY, CustomPersistentDataType.HOLOGRAM_ARRAY);
        if (holograms == null)
            return;

        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        int unloaded = this.unloadedHolograms.removeAll(chunkLocation).size();
        if (unloaded != holograms.length)
            RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but " + holograms.length + " were found. Loading them anyway.");

        this.loadedHolograms.putAll(chunkLocation, Arrays.asList(holograms));
    }

    public void unloadHolograms(Chunk chunk) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        this.loadedHolograms.removeAll(chunkLocation).forEach(hologram -> {
            UnloadedHologram unloadedHologram = UnloadedHologram.fromHologram(hologram);
            this.unloadedHolograms.put(chunkLocation, unloadedHologram);
        });
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
        this.unloadHolograms(event.getChunk());
    }

}
