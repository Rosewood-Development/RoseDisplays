package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosegarden.registry.RoseKey;

public record UnloadedHologramGroup(RoseKey key,
                                    ChunkLocation chunkLocation) { }
