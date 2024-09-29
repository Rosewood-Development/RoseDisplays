package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosegarden.registry.RoseKey;
import dev.rosewood.rosegarden.registry.RoseKeyed;
import dev.rosewood.rosegarden.registry.RoseRegistry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import static dev.rosewood.rosedisplays.hologram.property.HologramProperties.*;

public final class HologramPropertyTag implements RoseKeyed, Iterable<HologramProperty<?>> {

    public static final RoseRegistry<HologramPropertyTag> REGISTRY = RoseRegistry.create(HologramPropertyTag.class);

    public static final HologramPropertyTag GROUP = builder("group")
            .add(UPDATE_INTERVAL, RENDER_DISTANCE, HOLOGRAM_SORTING)
            .build();
    public static final HologramPropertyTag DISPLAY_ENTITY = builder("display_entity")
            .add(GLOWING, INTERPOLATION_DELAY, INTERPOLATION_DURATION, TRANSFORMATION_INTERPOLATION_DURATION,
                    POSITION_ROTATION_INTERPOLATION_DURATION, TRANSLATION, SCALE, ROTATION_LEFT, ROTATION_RIGHT,
                    BILLBOARD_CONSTRAINT, BLOCK_LIGHT_OVERRIDE, SKY_LIGHT_OVERRIDE, VIEW_RANGE, SHADOW_RADIUS,
                    SHADOW_STRENGTH, WIDTH, HEIGHT, GLOW_COLOR_OVERRIDE)
            .build();
    public static final HologramPropertyTag TEXT_DISPLAY_ENTITY = builder("text_display_entity")
            .inherit(DISPLAY_ENTITY)
            .add(TEXT, LINE_WIDTH, BACKGROUND_COLOR, TEXT_OPACITY, HAS_SHADOW, SEE_THROUGH,
                    USE_DEFAULT_BACKGROUND_COLOR, ALIGNMENT)
            .add(PLACEHOLDER_UPDATE_INTERVAL)
            .build();
    public static final HologramPropertyTag ITEM_DISPLAY_ENTITY = builder("item_display_entity")
            .inherit(DISPLAY_ENTITY)
            .add(ITEM, DISPLAY_TYPE)
            .build();
    public static final HologramPropertyTag BLOCK_DISPLAY_ENTITY = builder("block_display_entity")
            .inherit(DISPLAY_ENTITY)
            .add(BLOCK_DATA)
            .build();

    private final RoseKey key;
    private final Set<HologramProperty<?>> properties;

    private HologramPropertyTag(RoseKey key, Set<HologramProperty<?>> properties) {
        this.key = key;
        this.properties = properties;
    }

    @Override
    public RoseKey key() {
        return this.key;
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

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private final Set<HologramProperty<?>> properties;

        private Builder(String name) {
            this.name = name;
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
            HologramPropertyTag tag = new HologramPropertyTag(RoseKey.of(this.name), this.properties);
            REGISTRY.register(tag);
            return tag;
        }

    }

}
