package dev.rosewood.rosedisplays.manager;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class HologramManager extends Manager implements Listener {

    private final Multimap<ChunkLocation, Hologram> loadedHolograms;
    private final Multimap<ChunkLocation, UnloadedHologram> unloadedHolograms;
    private final Map<ChunkLocation, Long> chunkTickets;

    public HologramManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.loadedHolograms = MultimapBuilder.hashKeys().hashSetValues().build();
        this.unloadedHolograms = MultimapBuilder.hashKeys().hashSetValues().build();
        this.chunkTickets = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(rosePlugin, this::tick, 0L, ConfigurationManager.Setting.HOLOGRAM_UPDATE_FREQUENCY.getLong());
        Bukkit.getScheduler().runTaskTimer(rosePlugin, this::checkChunkTickets, 0L, 20L);
    }

    @Override
    public void reload() {
        Bukkit.getWorlds().forEach(this::loadWorldHolograms);
    }

    @Override
    public void disable() {
        Bukkit.getWorlds().forEach(this::unloadWorldHolograms);

        this.loadedHolograms.clear();
        this.unloadedHolograms.clear();
    }

    private void tick() {
        Multimap<UUID, Player> playersPerWorld = MultimapBuilder.hashKeys().hashSetValues().build();
        for (Player player : Bukkit.getOnlinePlayers())
            playersPerWorld.put(player.getWorld().getUID(), player);

        for (Hologram hologram : this.loadedHolograms.values()) {
            HologramRenderer renderer = hologram.getRenderer();
            for (Player player : playersPerWorld.get(hologram.getLocation().getWorld().getUID())) {
                boolean watching = renderer.isWatching(player);
                boolean inRange = hologram.isInRange(player);
                if (inRange && !watching) {
                    renderer.addWatcher(player);
                } else if (!inRange && watching) {
                    renderer.removeWatcher(player);
                }
            }

            renderer.update();
        }
    }

    private void checkChunkTickets() {
        for (Map.Entry<ChunkLocation, Long> entry : this.chunkTickets.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() <= 0) {
                this.chunkTickets.remove(entry.getKey());
                World world = Bukkit.getWorld(entry.getKey().world());
                if (world != null) {
                    Chunk chunk = world.getChunkAt(entry.getKey().x(), entry.getKey().z());
                    chunk.removePluginChunkTicket(RoseDisplays.getInstance());
                }
            }
        }
    }

    public Hologram createHologram(String name, Location location) {
        if (this.getHologram(name) != null)
            throw new IllegalArgumentException("A hologram with the name " + name + " already exists");

        Hologram hologram = new Hologram(name, location);
        this.loadedHolograms.put(ChunkLocation.of(location), hologram);
        return hologram;
    }

    public void deleteHologram(UnloadedHologram hologram) {
        if (hologram.isLoaded())
            this.loadedHolograms.get(hologram.getChunkLocation()).removeIf(x -> x.getName().equalsIgnoreCase(hologram.getName()));
        this.unloadedHolograms.remove(hologram.getChunkLocation(), hologram);
        hologram.removeAllWatchers();
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
        this.chunkTickets.put(chunkLocation, System.currentTimeMillis() + 30000L);
        this.loadHolograms(chunk);

        return this.loadedHolograms.values().stream()
                .filter(hologram -> hologram.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("A hologram with the name " + name + " was expected to be loaded but was not"));
    }

    public List<String> getHologramNames() {
        List<String> names = new ArrayList<>();
        this.loadedHolograms.values().forEach(hologram -> names.add(hologram.getName()));
        this.unloadedHolograms.values().forEach(hologram -> names.add(hologram.getName()));
        return names;
    }

    public void loadWorldHolograms(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        try {
            UnloadedHologram[] unloadedHologramsArray = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY);
            if (unloadedHologramsArray == null)
                return;

            List<UnloadedHologram> unloadedHolograms = Arrays.asList(unloadedHologramsArray);
            unloadedHolograms.forEach(x -> this.unloadedHolograms.put(x.getChunkLocation(), x));
            unloadedHolograms.stream()
                    .map(UnloadedHologram::getChunkLocation)
                    .distinct()
                    .filter(chunkLocation -> world.isChunkLoaded(chunkLocation.x(), chunkLocation.z()))
                    .map(chunkLocation -> world.getChunkAt(chunkLocation.x(), chunkLocation.z()))
                    .forEach(this::loadHolograms);
        } catch (Exception e) {
            RoseDisplays.getInstance().getLogger().warning("Failed to load holograms in world " + world.getName() + ": " + e.getMessage());
            pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
        }
    }

    public void unloadWorldHolograms(World world) {
        for (Chunk chunk : world.getLoadedChunks())
            this.unloadHolograms(chunk);

        List<UnloadedHologram> worldHolograms = new ArrayList<>();
        for (UnloadedHologram unloadedHologram : this.unloadedHolograms.values())
            if (unloadedHologram.getChunkLocation().world().equals(world.getName()))
                worldHolograms.add(unloadedHologram);
        this.unloadedHolograms.values().removeAll(worldHolograms);

        worldHolograms.forEach(UnloadedHologram::removeAllWatchers);

        PersistentDataContainer pdc = world.getPersistentDataContainer();
        pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY, worldHolograms.toArray(UnloadedHologram[]::new));
    }

    public void loadHolograms(Chunk chunk) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        try {
            int unloaded = this.unloadedHolograms.removeAll(chunkLocation).size();
            Hologram[] holograms = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.HOLOGRAM_ARRAY);
            if (holograms == null) {
                if (unloaded > 0)
                    RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but none were found. Ignoring them.");
                return;
            }

            if (unloaded != holograms.length)
                RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but " + holograms.length + " were found. Loading them anyway.");

            this.loadedHolograms.putAll(chunkLocation, Arrays.asList(holograms));
        } catch (Exception e) {
            RoseDisplays.getInstance().getLogger().warning("Failed to load holograms in chunk " + chunkLocation + ": " + e.getMessage());
            pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
        }
    }

    public void unloadHolograms(Chunk chunk) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        Collection<Hologram> holograms = this.loadedHolograms.removeAll(chunkLocation);
        holograms.forEach(hologram -> {
            this.unloadedHolograms.put(chunkLocation, hologram.asUnloaded());
            hologram.removeAllWatchers();
        });

        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.HOLOGRAM_ARRAY, holograms.toArray(Hologram[]::new));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        this.loadWorldHolograms(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.unloadWorldHolograms(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        this.loadHolograms(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        this.unloadHolograms(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.loadedHolograms.values().forEach(x -> x.getRenderer().removeWatcher(player));
    }

}
