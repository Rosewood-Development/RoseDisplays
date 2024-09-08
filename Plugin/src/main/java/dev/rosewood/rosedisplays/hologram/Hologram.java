package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.renderer.HologramRenderer;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Hologram {

    private final String name;

    public Hologram(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract HologramPropertyContainer getProperties();

    public abstract HologramRenderer getRenderer();

    public abstract Location getLocation();

    public boolean isInRange(Player player) {
        Location location = this.getLocation();
        int maxDistance = SettingKey.HOLOGRAM_RENDER_DISTANCE.get();
        return player.getWorld().equals(location.getWorld()) && location.distanceSquared(player.getLocation()) <= maxDistance * maxDistance;
    }

    public ChunkLocation getChunkLocation() {
        return ChunkLocation.of(this.getLocation());
    }

    @NotNull
    public UnloadedHologram asUnloaded() {
        return new UnloadedHologram(this.name, this.getChunkLocation());
    }

}
