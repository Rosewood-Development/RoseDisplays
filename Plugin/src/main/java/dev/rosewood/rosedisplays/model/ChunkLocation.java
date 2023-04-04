package dev.rosewood.rosedisplays.model;

import org.bukkit.Chunk;
import org.bukkit.Location;

public record ChunkLocation(String world, int x, int z) {

    public static ChunkLocation of(Chunk chunk) {
        return new ChunkLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkLocation of(Location location) {
        if (location.getWorld() == null)
            throw new IllegalArgumentException("Location must have a world");
        return new ChunkLocation(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

}
