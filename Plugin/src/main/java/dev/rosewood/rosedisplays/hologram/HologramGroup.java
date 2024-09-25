package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramGroup {

    private final String name;
    private final Location origin;
    private final DirtyingHologramPropertyView properties;
    private final List<Hologram> holograms;
    private final Set<Player> watchers;
    private long nextUpdateTime;

    public HologramGroup(String name, Location origin) {
        this(name, origin, new ArrayList<>(), new DirtyingHologramPropertyView(HologramPropertyTag.GROUP));
    }

    public HologramGroup(String name, Location origin, List<Hologram> holograms, DirtyingHologramPropertyView properties) {
        if (properties.getTag() != HologramPropertyTag.GROUP)
            throw new IllegalArgumentException("Invalid properties for hologram group");

        this.name = name;
        this.origin = origin;
        this.holograms = holograms;
        this.watchers = new HashSet<>();
        this.properties = properties;
    }

    public String getName() {
        return this.name;
    }

    public Location getOrigin() {
        return this.origin;
    }

    public DirtyingHologramPropertyView getGroupProperties() {
        return this.properties;
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
        int maxDistance = this.properties.getOrDefault(HologramProperties.RENDER_DISTANCE);
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
        if (System.currentTimeMillis() >= this.nextUpdateTime) {
            Set<Player> unmodifiableWatchers = Collections.unmodifiableSet(this.watchers);
            for (Hologram hologram : this.holograms) {
                Location location = this.origin.clone();
                hologram.update(location, unmodifiableWatchers);
            }
            this.nextUpdateTime = System.currentTimeMillis() + this.properties.getOrDefault(HologramProperties.UPDATE_INTERVAL);
        }
    }

    /**
     * @return true if this HologramGroup's watchers should be kept in sync with the {@link #isInRange(Player)} check
     */
    public boolean shouldKeepWatchersInSync() {
        return true;
    }

}
