package dev.rosewood.rosedisplays.hologram.property;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayAlignment;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import java.awt.Color;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public final class HologramProperty<T> {

    private static final Set<HologramProperty<?>> KNOWN_PROPERTIES = new HashSet<>();
    private static final Set<HologramProperty<?>> AVAILABLE_PROPERTIES = new HashSet<>();
    private static final Multimap<HologramLineType, HologramProperty<?>> AVAILABLE_PROPERTIES_BY_TYPE = MultimapBuilder.enumKeys(HologramLineType.class).arrayListValues().build();

    // All
    public static final HologramProperty<Boolean> GLOWING = create("glowing", ArgumentHandlers.BOOLEAN).mappedProperty().build();
    public static final HologramProperty<Integer> INTERPOLATION_DELAY = create("interpolation_delay", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Integer> INTERPOLATION_DURATION = create("interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Integer> TRANSFORMATION_INTERPOLATION_DURATION = create("transformation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Integer> POSITION_ROTATION_INTERPOLATION_DURATION = create("position_rotation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Vector3> TRANSLATION = create("translation", DisplaysArgumentHandlers.VECTOR3).mappedProperty().build();
    public static final HologramProperty<Vector3> SCALE = create("scale", DisplaysArgumentHandlers.VECTOR3).mappedProperty().build();
    public static final HologramProperty<Quaternion> ROTATION_LEFT = create("rotation_left", DisplaysArgumentHandlers.QUATERNION).mappedProperty().build();
    public static final HologramProperty<Quaternion> ROTATION_RIGHT = create("rotation_right", DisplaysArgumentHandlers.QUATERNION).mappedProperty().build();
    public static final HologramProperty<BillboardConstraint> BILLBOARD_CONSTRAINT = create("billboard_constraint", ArgumentHandlers.forEnum(BillboardConstraint.class)).mappedProperty().build();
    public static final HologramProperty<Integer> BLOCK_LIGHT_OVERRIDE = create("block_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15)).mappedProperty().build();
    public static final HologramProperty<Integer> SKY_LIGHT_OVERRIDE = create("sky_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15)).mappedProperty().build();
    public static final HologramProperty<Float> VIEW_RANGE = create("view_range", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Float> SHADOW_RADIUS = create("shadow_radius", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Float> SHADOW_STRENGTH = create("shadow_strength", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Float> WIDTH = create("width", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE)).mappedProperty().build();
    public static final HologramProperty<Float> HEIGHT = create("height", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.POSITIVE_INFINITY)).mappedProperty().build();
    public static final HologramProperty<Color> GLOW_COLOR_OVERRIDE = create("glow_color_override", ArgumentHandlers.JAVA_COLOR).mappedProperty().build();

    // Text Display
    public static final HologramProperty<String> TEXT = create("text", ArgumentHandlers.GREEDY_STRING).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Integer> LINE_WIDTH = create("line_width", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE)).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Color> BACKGROUND_COLOR = create("background_color", ArgumentHandlers.JAVA_COLOR).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Byte> TEXT_OPACITY = create("text_opacity", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.BYTE, -1, Byte.MAX_VALUE)).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Boolean> HAS_SHADOW = create("has_shadow", ArgumentHandlers.BOOLEAN).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Boolean> SEE_THROUGH = create("see_through", ArgumentHandlers.BOOLEAN).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<Boolean> USE_DEFAULT_BACKGROUND_COLOR = create("use_default_background_color", ArgumentHandlers.BOOLEAN).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    public static final HologramProperty<TextDisplayAlignment> ALIGNMENT = create("alignment", ArgumentHandlers.forEnum(TextDisplayAlignment.class)).lineTypes(HologramLineType.TEXT).mappedProperty().build();
    // Custom Text Display
    public static final HologramProperty<Duration> TEXT_UPDATE_INTERVAL = create("text_update_interval", DisplaysArgumentHandlers.DURATION).lineTypes(HologramLineType.TEXT).build();
    public static final HologramProperty<Duration> PLACEHOLDER_UPDATE_INTERVAL = create("placeholder_update_interval", DisplaysArgumentHandlers.DURATION).lineTypes(HologramLineType.TEXT).build();

    // Item Display
    public static final HologramProperty<ItemStack> ITEM = create("item", DisplaysArgumentHandlers.ITEMSTACK).lineTypes(HologramLineType.ITEM).mappedProperty().build();
    public static final HologramProperty<ItemDisplayType> DISPLAY_TYPE = create("display_type", ArgumentHandlers.forEnum(ItemDisplayType.class)).lineTypes(HologramLineType.ITEM).mappedProperty().build();

    // Block Display
    public static final HologramProperty<BlockData> BLOCK_DATA = create("block_data", DisplaysArgumentHandlers.BLOCK_DATA).lineTypes(HologramLineType.BLOCK).mappedProperty().build();

    static {
        // This has to be down here instead of in the HologramProperty constructor since it can cause a circular dependency
        NMSHandler nmsHandler = NMSAdapter.getHandler();
        KNOWN_PROPERTIES.forEach(property -> {
            if (!property.isMapped() || nmsHandler.isPropertyAvailable(property)) {
                AVAILABLE_PROPERTIES.add(property);
                property.getLineTypes().forEach(lineType -> AVAILABLE_PROPERTIES_BY_TYPE.put(lineType, property));
            }
        });
    }

    private final String name;
    private final ArgumentHandler<T> argumentHandler;
    private final Set<HologramLineType> lineTypes;
    private final boolean mappedProperty;

    private HologramProperty(String name, ArgumentHandler<T> argumentHandler, Set<HologramLineType> lineTypes, boolean mappedProperty) {
        this.name = name;
        this.argumentHandler = argumentHandler;
        this.lineTypes = lineTypes;
        this.mappedProperty = mappedProperty;
        if (!KNOWN_PROPERTIES.add(this))
            throw new IllegalArgumentException("Duplicate HologramProperty: " + this.name);
    }

    public String getName() {
        return this.name;
    }

    public ArgumentHandler<?> getArgumentHandler() {
        return this.argumentHandler;
    }

    public Class<T> getType() {
        return this.argumentHandler.getHandledType();
    }

    public Set<HologramLineType> getLineTypes() {
        return this.lineTypes;
    }

    public boolean isApplicable(HologramLineType lineType) {
        return this.lineTypes.contains(lineType);
    }

    public boolean isAvailable() {
        return AVAILABLE_PROPERTIES.contains(this);
    }

    /**
     * @return true if this property is mapped to an NMS property, false otherwise
     */
    public boolean isMapped() {
        return this.mappedProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        HologramProperty<?> that = (HologramProperty<?>) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "HologramProperty[" +
                "name=" + this.name + ", " +
                "type=" + this.argumentHandler + ", " +
                "lineTypes=" + this.lineTypes + ", " +
                "mappedProperty=" + this.mappedProperty +
                "]";
    }

    public static Collection<HologramProperty<?>> values() {
        return Set.copyOf(AVAILABLE_PROPERTIES);
    }

    public static Collection<HologramProperty<?>> values(HologramLineType lineType) {
        return Set.copyOf(AVAILABLE_PROPERTIES_BY_TYPE.get(lineType));
    }

    public static HologramProperty<?> valueOf(String name) {
        return AVAILABLE_PROPERTIES.stream()
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private static <T> Builder<T> create(String name, ArgumentHandler<T> argumentHandler) {
        return new Builder<>(name, argumentHandler);
    }

    private static class Builder<T> {

        private final String name;
        private final ArgumentHandler<T> argumentHandler;
        private Set<HologramLineType> lineTypes;
        private boolean mappedProperty;

        public Builder(String name, ArgumentHandler<T> argumentHandler) {
            this.name = name;
            this.argumentHandler = argumentHandler;
            this.lineTypes = EnumSet.allOf(HologramLineType.class);
            this.mappedProperty = false;
        }

        public Builder<T> lineTypes(HologramLineType... lineTypes) {
            if (lineTypes == null || lineTypes.length == 0) {
                this.lineTypes = EnumSet.allOf(HologramLineType.class);
            } else {
                this.lineTypes = EnumSet.copyOf(Arrays.asList(lineTypes));
            }
            return this;
        }

        public Builder<T> mappedProperty() {
            this.mappedProperty = true;
            return this;
        }

        public HologramProperty<T> build() {
            return new HologramProperty<>(
                    this.name,
                    this.argumentHandler,
                    this.lineTypes,
                    this.mappedProperty
            );
        }

    }

}
