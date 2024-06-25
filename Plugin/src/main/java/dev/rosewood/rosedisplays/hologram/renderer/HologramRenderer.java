package dev.rosewood.rosedisplays.hologram.renderer;

import org.bukkit.entity.Player;

public interface HologramRenderer {

    /**
     * Updates the renderer, performing any actions needed like spawning, updating metadata, or despawning the hologram.
     */
    void update();

    /**
     * Adds a watcher to the renderer.
     *
     * @param player The player to add.
     */
    void addWatcher(Player player);

    /**
     * Removes a watcher from the renderer.
     *
     * @param player The player to remove.
     */
    void removeWatcher(Player player);

    /**
     * Destroys the renderer, removing any watchers and rendered holograms along with it.
     */
    void removeAllWatchers();

    /**
     * Returns true if the player is watching the hologram.
     *
     * @param player The player to check.
     * @return true if the player is watching the hologram, false otherwise.
     */
    boolean isWatching(Player player);

}
