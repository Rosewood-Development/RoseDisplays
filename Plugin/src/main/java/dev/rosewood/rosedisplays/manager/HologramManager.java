package dev.rosewood.rosedisplays.manager;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.datatype.CustomPersistentDataType;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramGroup;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.UnloadedHologramGroup;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosegarden.registry.RoseKey;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import java.util.ArrayList;
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

    private final ListMultimap<ChunkLocation, HologramGroup> loadedHolograms;
    private final ListMultimap<ChunkLocation, UnloadedHologramGroup> unloadedHolograms;
    private final Map<ChunkLocation, Long> chunkTickets;
    private ScheduledTask tickTask;
    private ScheduledTask chunkTicketTask;
    private transient boolean ticking = false;

    public HologramManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.loadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();
        this.unloadedHolograms = MultimapBuilder.hashKeys().arrayListValues().build();
        this.chunkTickets = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        this.tickTask = this.rosePlugin.getScheduler().runTaskTimerAsync(this::tick, 0L, SettingKey.HOLOGRAM_UPDATE_FREQUENCY.get());
        this.chunkTicketTask = this.rosePlugin.getScheduler().runTaskTimer(this::checkChunkTickets, 0L, 20L);

        Bukkit.getWorlds().forEach(this::loadWorldHolograms);
    }

    @Override
    public void disable() {
        if (this.tickTask != null) {
            this.tickTask.cancel();
            this.tickTask = null;
        }

        if (this.chunkTicketTask != null) {
            this.chunkTicketTask.cancel();
            this.chunkTicketTask = null;
        }

        Bukkit.getWorlds().forEach(this::unloadWorldHolograms);

        this.loadedHolograms.clear();
        this.unloadedHolograms.clear();
    }

    private void tick() {
        if (this.ticking)
            return;

        this.ticking = true;
        try {
            Multimap<UUID, Player> playersPerWorld = MultimapBuilder.hashKeys().hashSetValues().build();
            for (Player player : Bukkit.getOnlinePlayers())
                playersPerWorld.put(player.getWorld().getUID(), player);

            for (HologramGroup group : this.loadedHolograms.values()) {
                if (group.shouldKeepWatchersInSync()) {
                    for (Player player : playersPerWorld.get(group.getOrigin().getWorld().getUID())) {
                        boolean watching = group.isWatching(player);
                        boolean inRange = group.isInRange(player);
                        if (inRange && !watching) {
                            group.addWatcher(player);
                        } else if (!inRange && watching) {
                            group.removeWatcher(player);
                        }
                    }
                }
                group.update();
            }
        } finally {
            this.ticking = false;
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

    public Hologram createHologram(String name, HologramType type, Location location) {
        name = name.toLowerCase();
        if (this.getHologramNames().contains(name))
            return null;

        location = location.clone();
        location.setYaw(0);
        location.setPitch(0);

        HologramGroup group = new HologramGroup(RoseKey.of(name), location);
        Hologram hologram = type.create();
        group.addHologram(hologram);
        this.loadedHolograms.put(ChunkLocation.of(location), group);
        return hologram;
    }

    public boolean deleteHologram(RoseKey key) {
        HologramGroup hologram = this.getHologram(key);
        if (hologram == null)
            return false;

        hologram.removeAllWatchers();

        ChunkLocation chunkLocation = hologram.getChunkLocation();
        Collection<HologramGroup> chunkHolograms = this.loadedHolograms.get(chunkLocation);
        chunkHolograms.remove(hologram);

        Chunk chunk = hologram.getOrigin().getChunk();
        if (chunkHolograms.isEmpty() && this.chunkTickets.containsKey(chunkLocation))
            this.removeChunkTicket(chunk);

        this.saveHolograms(chunk, this.loadedHolograms.get(chunkLocation));
        return true;
    }

    /**
     * Gets a hologram by its name, loading the chunk it's in if needed.
     *
     * @param key The key of the hologram to get
     * @return The hologram or null or one with that name did not exist
     */
    public HologramGroup getHologram(RoseKey key) {
        // Search loaded holograms first
        HologramGroup hologram = this.loadedHolograms.values().stream()
                .filter(x -> x.key().equals(key))
                .findFirst()
                .orElse(null);
        if (hologram != null)
            return hologram;

        // Search unloaded holograms
        UnloadedHologramGroup unloadedHologram = this.unloadedHolograms.values().stream()
                .filter(x -> x.key().equals(key))
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
                .filter(x -> x.key().equals(key))
                .findFirst()
                .orElse(null);
        if (hologram == null)
            this.rosePlugin.getLogger().warning("A hologram with the name " + key + " was expected to be loaded but was not");

        return hologram;
    }

    public List<String> getHologramNames() {
        List<String> names = new ArrayList<>();
        this.loadedHolograms.values().forEach(hologram -> names.add(hologram.key().toString()));
        this.unloadedHolograms.values().forEach(hologram -> names.add(hologram.key().toString()));
        return names;
    }

    public void loadWorldHolograms(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        if (!pdc.has(CustomPersistentDataType.HOLOGRAM_KEY))
            return;

        try {
            List<UnloadedHologramGroup> unloadedHolograms = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.forList(CustomPersistentDataType.UNLOADED_HOLOGRAM_GROUP));
            if (unloadedHolograms == null || unloadedHolograms.isEmpty()) {
                pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
                return;
            }

            unloadedHolograms.forEach(x -> this.unloadedHolograms.put(x.chunkLocation(), x));
            unloadedHolograms.stream()
                    .map(UnloadedHologramGroup::chunkLocation)
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

        List<UnloadedHologramGroup> worldHolograms = new ArrayList<>();
        for (UnloadedHologramGroup unloadedHologram : this.unloadedHolograms.values())
            if (unloadedHologram.chunkLocation().world().equals(world.getName()))
                worldHolograms.add(unloadedHologram);
        this.unloadedHolograms.values().removeAll(worldHolograms);

        PersistentDataContainer pdc = world.getPersistentDataContainer();
        if (!worldHolograms.isEmpty()) {
            pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.forList(CustomPersistentDataType.UNLOADED_HOLOGRAM_GROUP), worldHolograms);
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
            List<HologramGroup> holograms = pdc.get(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.forList(CustomPersistentDataType.HOLOGRAM_GROUP));
            if (holograms == null || holograms.isEmpty()) {
                if (unloaded > 0)
                    RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but none were found. Ignoring them.");
                pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
                return;
            }

            if (unloaded != holograms.size())
                RoseDisplays.getInstance().getLogger().warning("Expected " + unloaded + " holograms in chunk " + chunkLocation + " but " + holograms.size() + " were found. Loading them anyway.");

            this.loadedHolograms.putAll(chunkLocation, holograms);
        } catch (Exception e) {
            RoseDisplays.getInstance().getLogger().warning("Failed to load holograms in chunk " + chunkLocation + ": " + e.getMessage());
            pdc.remove(CustomPersistentDataType.HOLOGRAM_KEY);
        }
    }

    public void unloadHolograms(Chunk chunk) {
        ChunkLocation chunkLocation = ChunkLocation.of(chunk);
        List<HologramGroup> holograms = this.loadedHolograms.removeAll(chunkLocation);

        holograms.forEach(hologram -> {
            this.unloadedHolograms.put(chunkLocation, hologram.asUnloaded());
            hologram.removeAllWatchers();
        });

        this.saveHolograms(chunk, holograms);
    }

    private void saveHolograms(Chunk chunk, List<HologramGroup> holograms) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        if (!holograms.isEmpty()) {
            pdc.set(CustomPersistentDataType.HOLOGRAM_KEY, CustomPersistentDataType.forList(CustomPersistentDataType.HOLOGRAM_GROUP), holograms);
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
        this.loadedHolograms.values().forEach(x -> x.removeWatcher(player));
    }

}
