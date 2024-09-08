package dev.rosewood.rosedisplays.hologram.property;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import static dev.rosewood.rosedisplays.hologram.property.HologramProperties.*;

public final class HologramPropertyTag implements Iterable<HologramProperty<?>> {

    public static final HologramPropertyTag DISPLAY_ENTITY = builder()
            .add(GLOWING, INTERPOLATION_DELAY, INTERPOLATION_DURATION, TRANSFORMATION_INTERPOLATION_DURATION,
                    POSITION_ROTATION_INTERPOLATION_DURATION, TRANSLATION, SCALE, ROTATION_LEFT, ROTATION_RIGHT,
                    BILLBOARD_CONSTRAINT, BLOCK_LIGHT_OVERRIDE, SKY_LIGHT_OVERRIDE, VIEW_RANGE, SHADOW_RADIUS,
                    SHADOW_STRENGTH, WIDTH, HEIGHT, GLOW_COLOR_OVERRIDE)
            .build();
    public static final HologramPropertyTag TEXT_DISPLAY_ENTITY = builder()
            .inherit(DISPLAY_ENTITY)
            .add(TEXT, LINE_WIDTH, BACKGROUND_COLOR, TEXT_OPACITY, HAS_SHADOW, SEE_THROUGH,
                    USE_DEFAULT_BACKGROUND_COLOR, ALIGNMENT)
            .add(TEXT_UPDATE_INTERVAL, PLACEHOLDER_UPDATE_INTERVAL)
            .build();
    public static final HologramPropertyTag ITEM_DISPLAY_ENTITY = builder()
            .inherit(DISPLAY_ENTITY)
            .add(ITEM, DISPLAY_TYPE)
            .build();
    public static final HologramPropertyTag BLOCK_DISPLAY_ENTITY = builder()
            .inherit(DISPLAY_ENTITY)
            .add(BLOCK_DATA)
            .build();

    private final Set<HologramProperty<?>> properties;

    private HologramPropertyTag(Set<HologramProperty<?>> properties) {
        this.properties = properties;
    }

    public boolean contains(HologramProperty<?> property) {
        return this.properties.contains(property);
    }

    @Override
    public Iterator<HologramProperty<?>> iterator() {
        return this.properties.stream()
                .filter(HologramProperties::isAvailable)
                .iterator();
    }

    public Stream<HologramProperty<?>> stream() {
        return this.properties.stream();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Set<HologramProperty<?>> properties;

        private Builder() {
            this.properties = new HashSet<>();
        }

        public Builder inherit(HologramPropertyTag tag) {
            this.properties.addAll(tag.properties);
            return this;
        }

        public Builder add(Collection<HologramProperty<?>> properties) {
            this.properties.addAll(properties);
            return this;
        }

        public Builder add(HologramProperty<?>... properties) {
            this.properties.addAll(Arrays.asList(properties));
            return this;
        }

        public HologramPropertyTag build() {
            return new HologramPropertyTag(this.properties);
        }

    }

}
