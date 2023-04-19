package dev.rosewood.rosedisplays.hologram.property;

/**
 * Provides a way to get HologramProperties available in the current game version
 */
public interface VersionAvailabilityProvider {

    /**
     * Gets whether or not the provided property is available in the current game version
     *
     * @param property the property
     * @return true if the property is available, false otherwise
     */
    boolean isAvailable(HologramProperty<?> property);

}
