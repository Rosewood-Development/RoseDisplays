package dev.rosewood.rosedisplays.property;

import dev.rosewood.rosedisplays.hologram.HologramLineType;
import java.util.Map;

/**
 * Provides a way to get HologramProperties available in the current game version
 */
public interface HologramPropertyProvider {

    /**
     * Gets a map of available properties for a given HologramLineType
     *
     * @param type the HologramLineType to get available properties for
     * @return a map of all properties available for the given HologramLineType
     */
    Map<String, HologramProperty<?>> getProperties(HologramLineType type);

    /**
     * @return a map of all properties available in the current game version
     */
    Map<String, HologramProperty<?>> getAllProperties();

}
