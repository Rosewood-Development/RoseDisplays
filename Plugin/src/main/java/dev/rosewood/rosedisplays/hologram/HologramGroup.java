package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramGroup {

    private final String name;
    private final Location origin;
    private final List<Hologram> holograms;
    private final Set<Player> watchers;

    public HologramGroup(String name, Location origin) {
        this(name, origin, new ArrayList<>());
    }

    public HologramGroup(String name, Location origin, List<Hologram> holograms) {
        this.name = name;
        this.origin = origin;
        this.holograms = holograms;
        this.watchers = ConcurrentHashMap.newKeySet();
    }

    public String getName() {
        return this.name;
    }

    public Location getOrigin() {
        return this.origin;
    }

    public void addHologram(Hologram hologram) {
        this.holograms.add(hologram);
    }

    public void removeHologram(Hologram hologram) {
        this.holograms.remove(hologram);
    }

    public List<Hologram> getHolograms() {
        return this.holograms;
    }

    public void forEach(BiConsumer<Hologram, Location> consumer) {
        for (Hologram hologram : this.holograms) {
            Location location = this.origin.clone();
            consumer.accept(hologram, location);
        }
    }

    public boolean isInRange(Player player) {
        Location location = this.getOrigin();
        int maxDistance = SettingKey.HOLOGRAM_RENDER_DISTANCE.get();
        return player.getWorld().equals(location.getWorld()) && location.distanceSquared(player.getLocation()) <= maxDistance * maxDistance;
    }

    public ChunkLocation getChunkLocation() {
        return ChunkLocation.of(this.getOrigin());
    }

    public UnloadedHologramGroup asUnloaded() {
        return new UnloadedHologramGroup(this.name, this.getChunkLocation());
    }

    /**
     * Adds a watcher to the renderer.
     *
     * @param player The player to add.
     */
    public void addWatcher(Player player) {
        if (this.watchers.add(player)) {
            for (Hologram hologram : this.holograms) {
                Location location = this.origin.clone();
                hologram.onWatcherAdded(location, player);
            }
        }
    }

    /**
     * Removes a watcher from the renderer.
     *
     * @param player The player to remove.
     */
    public void removeWatcher(Player player) {
        if (this.watchers.remove(player))
            for (Hologram hologram : this.holograms)
                hologram.onWatcherRemoved(player);
    }

    /**
     * Destroys the renderer, removing any watchers and rendered holograms along with it.
     */
    public void removeAllWatchers() {
        for (Player watcher : this.watchers)
            for (Hologram hologram : this.holograms)
                hologram.onWatcherRemoved(watcher);
        this.watchers.clear();
    }

    /**
     * Returns true if the player is watching the hologram.
     *
     * @param player The player to check.
     * @return true if the player is watching the hologram, false otherwise.
     */
    public boolean isWatching(Player player) {
        return this.watchers.contains(player);
    }

    /**
     * Updates the group and all holograms within it.
     */
    public void update() {
        Set<Player> unmodifiableWatchers = Collections.unmodifiableSet(this.watchers);
        for (Hologram hologram : this.holograms) {
            Location location = this.origin.clone();
            hologram.update(location, unmodifiableWatchers);
        }
    }

    /**
     * @return true if this HologramGroup's watchers should be kept in sync with the {@link #isInRange(Player)} check
     */
    public boolean shouldKeepWatchersInSync() {
        return true;
    }

}
