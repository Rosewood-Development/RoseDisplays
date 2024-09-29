package dev.rosewood.rosegarden.registry;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public sealed interface RoseRegistry<T extends RoseKeyed> extends Iterable<T> permits RoseRegistryImpl {

    T get(RoseKey key);

    Set<RoseKey> keys();

    default Set<String> stringKeys() {
        return this.keys().stream().map(RoseKey::toString).collect(Collectors.toSet());
    }

    boolean containsKey(RoseKey key);

    boolean containsValue(T value);

    void register(T value);

    Stream<T> stream();

    Class<T> type();

    static <T extends RoseKeyed> RoseRegistry<T> create(Class<T> type) {
        return new RoseRegistryImpl<>(type);
    }

}
