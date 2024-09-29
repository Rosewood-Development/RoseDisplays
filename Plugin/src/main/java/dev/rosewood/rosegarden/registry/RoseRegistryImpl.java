package dev.rosewood.rosegarden.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class RoseRegistryImpl<T extends RoseKeyed> implements RoseRegistry<T> {

    private final Class<T> type;
    private final Map<RoseKey, T> registry;

    RoseRegistryImpl(Class<T> type) {
        this.type = type;
        this.registry = new HashMap<>();
    }

    @Override
    public T get(RoseKey key) {
        return this.registry.get(key);
    }

    @Override
    public Set<RoseKey> keys() {
        return this.registry.keySet();
    }

    @Override
    public boolean containsKey(RoseKey key) {
        return this.registry.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        return this.registry.containsValue(value);
    }

    @Override
    public void register(T value) {
        RoseKey key = value.key();
        if (this.containsKey(key))
            throw new IllegalArgumentException("A value is already registered with this key: " + key);
        this.registry.put(value.key(), value);
    }

    @Override
    public Stream<T> stream() {
        return this.registry.values().stream();
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public Iterator<T> iterator() {
        return this.registry.values().iterator();
    }

}
