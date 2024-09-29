package dev.rosewood.rosegarden.registry;

import java.util.Objects;
import java.util.regex.Pattern;

final class RoseKeyImpl implements RoseKey {

    private static final Pattern KEY_PATTERN = Pattern.compile(RoseKey.KEY_REGEX);

    private final String key;

    RoseKeyImpl(String key) {
        if (!KEY_PATTERN.matcher(key).matches())
            throw new IllegalArgumentException("Invalid key, must match pattern " + RoseKey.KEY_REGEX);
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoseKeyImpl roseKey)) return false;
        return Objects.equals(this.key, roseKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.key);
    }

}
