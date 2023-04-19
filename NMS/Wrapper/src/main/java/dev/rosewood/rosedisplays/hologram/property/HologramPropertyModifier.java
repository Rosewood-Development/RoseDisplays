package dev.rosewood.rosedisplays.hologram.property;

import java.util.function.BiFunction;

public final class HologramPropertyModifier<T, P> {

    private final String name;
    private final Class<T> type;
    private final BiFunction<T, P, P> modifier;

    public HologramPropertyModifier(String name, Class<T> type, BiFunction<T, P, P> modifier) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public P modify(T object, P value) {
        return this.modifier.apply(object, value);
    }

}
