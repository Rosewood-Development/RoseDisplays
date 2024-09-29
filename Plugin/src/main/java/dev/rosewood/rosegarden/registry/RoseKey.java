package dev.rosewood.rosegarden.registry;

public sealed interface RoseKey permits RoseKeyImpl {

    String KEY_REGEX = "[a-z0-9_\\-./:]+";

    /**
     * @return the string representation of this key
     */
    String toString();

    /**
     * Creates a RoseKey from the given string.
     *
     * @param key The string to create the RoseKey from
     * @return the created RoseKey
     * @throws IllegalArgumentException if the RoseKey contains invalid characters
     */
    static RoseKey of(String key) {
        return new RoseKeyImpl(key);
    }

}
