package dev.rosewood.rosedisplays.manager;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.hologram.DisplayEntityType;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.hologram.type.DisplayEntityHologram;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.CustomPersistentDataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        this.chunkTickets = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        this.rosePlugin.getScheduler().runTaskTimerAsync(this::tick, 0L, SettingKey.HOLOGRAM_UPDATE_FREQUENCY.get());
        this.rosePlugin.getScheduler().runTaskTimer(this::checkChunkTickets, 0L, 20L);
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
                    this.removeChunkTicket(chunk);
                }
            }
        }
    }

    private void setChunkTicket(Chunk chunk) {
        if (chunk.addPluginChunkTicket(this.rosePlugin))
            this.chunkTickets.put(ChunkLocation.of(chunk), System.currentTimeMillis() + 30000L);
    }

    private void removeChunkTicket(Chunk chunk) {
        chunk.removePluginChunkTicket(this.rosePlugin);
    }

    public DisplayEntityHologram createDisplayEntityHologram(String name, DisplayEntityType type, Location location) {
        if (this.getHologramNames().contains(name.toLowerCase()))
            throw new IllegalArgumentException("A hologram with the name " + name + " already exists");

        DisplayEntityHologram hologram = new DisplayEntityHologram(name, type, location);
        this.loadedHolograms.put(ChunkLocation.of(location), hologram);
        return hologram;
    }

    public boolean deleteHologram(String name) {
        Hologram hologram = this.getHologram(name);
        if (hologram == null)
            return false;

        hologram.getRenderer().removeAllWatchers();

        ChunkLocation chunkLocation = hologram.getChunkLocation();
        Collection<Hologram> chunkHolograms = this.loadedHolograms.get(chunkLocation);
        chunkHolograms.remove(hologram);

        Chunk chunk = hologram.getLocation().getChunk();
        if (chunkHolograms.isEmpty() && this.chunkTickets.containsKey(chunkLocation)) {
            this.removeChunkTicket(chunk);
        }

        this.saveHolograms(chunk, this.loadedHolograms.get(chunkLocation));
        return true;
    }

    /**
     * Gets a hologram by its name, loading the chunk it's in if needed.
     *
     * @param name The name of the hologram to get
     * @return The hologram or null or one with that name did not exist
     */
    public Hologram getHologram(String name) {
        // Search loaded holograms first
        Hologram hologram = this.loadedHolograms.values().stream()
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (hologram != null)
            return hologram;

        // Search unloaded holograms
        UnloadedHologram unloadedHologram = this.unloadedHolograms.values().stream()
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (unloadedHologram == null)
            return null;

        // Force load the hologram
        ChunkLocation chunkLocation = unloadedHologram.chunkLocation();
        World world = Bukkit.getWorld(chunkLocation.world());
        if (world == null)
            return null;

        // Load the chunk and holograms, keep the chunk loaded for minimum 30 seconds
        Chunk chunk = world.getChunkAt(chunkLocation.x(), chunkLocation.z());
        this.setChunkTicket(chunk);
        this.loadHolograms(chunk);

        hologram = this.loadedHolograms.values().stream()
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (hologram == null)
            this.rosePlugin.getLogger().warning("A hologram with the name " + name + " was expected to be loaded but was not");

        return hologram;
    }

    public List<String> getHologramNames() {
        List<String> names = new ArrayList<>();
        this.loadedHolograms.values().forEach(hologram -> names.add(hologram.getName()));
        this.unloadedHolograms.values().forEach(hologram -> names.add(hologram.name()));
        return names;
    }

    public void loadWorldHolograms(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        if (!pdc.has(CustomPersistentDataType.HOLOGRAM_KEY))
            return;

        try {
            UnloadedHologram[] unloadedHologramsArray = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY);
            if (unloadedHologramsArray == null || unloadedHologramsArray.length == 0) {
                pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
                return;
            }

            List<UnloadedHologram> unloadedHolograms = Arrays.asList(unloadedHologramsArray);
            unloadedHolograms.forEach(x -> this.unloadedHolograms.put(x.chunkLocation(), x));
            unloadedHolograms.stream()
                    .map(UnloadedHologram::chunkLocation)
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
            if (unloadedHologram.chunkLocation().world().equals(world.getName()))
                worldHolograms.add(unloadedHologram);
        this.unloadedHolograms.values().removeAll(worldHolograms);

        PersistentDataContainer pdc = world.getPersistentDataContainer();
        if (!worldHolograms.isEmpty()) {
            pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.UNLOADED_HOLOGRAM_ARRAY, worldHolograms.toArray(UnloadedHologram[]::new));
        } else {
            pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
        }
    }

    public void loadHolograms(Chunk chunk) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        int unloaded = this.unloadedHolograms.removeAll(chunkLocation).size();
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        if (!pdc.has(CustomPersistentDataType.HOLOGRAM_KEY)) {
            if (unloaded > 0)
                RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but none were found. Ignoring them.");
            return;
        }

        try {
            Hologram[] holograms = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.HOLOGRAM_ARRAY);
            if (holograms == null || holograms.length == 0) {
                if (unloaded > 0)
                    RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but none were found. Ignoring them.");
                pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
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
            hologram.getRenderer().removeAllWatchers();
        });

        this.saveHolograms(chunk, holograms);
    }

    private void saveHolograms(Chunk chunk, Collection<Hologram> holograms) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        if (!holograms.isEmpty()) {
            pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.HOLOGRAM_ARRAY, holograms.toArray(Hologram[]::new));
        } else {
            pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
        }
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
