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
    public static final HologramProperty<Boolean> GLOWING = new HologramProperty<>("glowing", ArgumentHandlers.BOOLEAN);
    public static final HologramProperty<Integer> INTERPOLATION_DELAY = new HologramProperty<>("interpolation_delay", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE));
    public static final HologramProperty<Integer> INTERPOLATION_DURATION = new HologramProperty<>("interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE));
    public static final HologramProperty<Integer> TRANSFORMATION_INTERPOLATION_DURATION = new HologramProperty<>("transformation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE));
    public static final HologramProperty<Integer> POSITION_ROTATION_INTERPOLATION_DURATION = new HologramProperty<>("position_rotation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE));
    public static final HologramProperty<Vector3> TRANSLATION = new HologramProperty<>("translation", DisplaysArgumentHandlers.VECTOR3);
    public static final HologramProperty<Vector3> SCALE = new HologramProperty<>("scale", DisplaysArgumentHandlers.VECTOR3);
    public static final HologramProperty<Quaternion> ROTATION_LEFT = new HologramProperty<>("rotation_left", DisplaysArgumentHandlers.QUATERNION);
    public static final HologramProperty<Quaternion> ROTATION_RIGHT = new HologramProperty<>("rotation_right", DisplaysArgumentHandlers.QUATERNION);
    public static final HologramProperty<BillboardConstraint> BILLBOARD_CONSTRAINT = new HologramProperty<>("billboard_constraint", ArgumentHandlers.forEnum(BillboardConstraint.class));
    public static final HologramProperty<Integer> BLOCK_LIGHT_OVERRIDE = new HologramProperty<>("block_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15));
    public static final HologramProperty<Integer> SKY_LIGHT_OVERRIDE = new HologramProperty<>("sky_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15));
    public static final HologramProperty<Float> VIEW_RANGE = new HologramProperty<>("view_range", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE));
    public static final HologramProperty<Float> SHADOW_RADIUS = new HologramProperty<>("shadow_radius", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE));
    public static final HologramProperty<Float> SHADOW_STRENGTH = new HologramProperty<>("shadow_strength", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE));
    public static final HologramProperty<Float> WIDTH = new HologramProperty<>("width", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE));
    public static final HologramProperty<Float> HEIGHT = new HologramProperty<>("height", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE));
    public static final HologramProperty<Color> GLOW_COLOR_OVERRIDE = new HologramProperty<>("glow_color_override", ArgumentHandlers.JAVA_COLOR);

    // Text Display
    public static final HologramProperty<String> TEXT = new HologramProperty<>("text", ArgumentHandlers.GREEDY_STRING, HologramLineType.TEXT);
    public static final HologramProperty<Integer> LINE_WIDTH = new HologramProperty<>("line_width", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE), HologramLineType.TEXT);
    public static final HologramProperty<Color> BACKGROUND_COLOR = new HologramProperty<>("background_color", ArgumentHandlers.JAVA_COLOR, HologramLineType.TEXT);
    public static final HologramProperty<Byte> TEXT_OPACITY = new HologramProperty<>("text_opacity", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.BYTE, -1, Byte.MAX_VALUE), HologramLineType.TEXT);
    public static final HologramProperty<Boolean> HAS_SHADOW = new HologramProperty<>("has_shadow", ArgumentHandlers.BOOLEAN, HologramLineType.TEXT);
    public static final HologramProperty<Boolean> SEE_THROUGH = new HologramProperty<>("see_through", ArgumentHandlers.BOOLEAN, HologramLineType.TEXT);
    public static final HologramProperty<Boolean> USE_DEFAULT_BACKGROUND_COLOR = new HologramProperty<>("use_default_background_color", ArgumentHandlers.BOOLEAN, HologramLineType.TEXT);
    public static final HologramProperty<TextDisplayAlignment> ALIGNMENT = new HologramProperty<>("alignment", ArgumentHandlers.forEnum(TextDisplayAlignment.class), HologramLineType.TEXT);

    // Item Display
    public static final HologramProperty<ItemStack> ITEM = new HologramProperty<>("item", DisplaysArgumentHandlers.ITEMSTACK, HologramLineType.ITEM);
    public static final HologramProperty<ItemDisplayType> DISPLAY_TYPE = new HologramProperty<>("display_type", ArgumentHandlers.forEnum(ItemDisplayType.class), HologramLineType.ITEM);

    // Block Display
    public static final HologramProperty<BlockData> BLOCK_DATA = new HologramProperty<>("block_data", DisplaysArgumentHandlers.BLOCK_DATA, HologramLineType.BLOCK);

    static {
        // This has to be down here instead of in the HologramProperty constructor since it can cause a circular dependency
        NMSHandler nmsHandler = NMSAdapter.getHandler();
        KNOWN_PROPERTIES.forEach(property -> {
            if (nmsHandler.isPropertyAvailable(property)) {
                AVAILABLE_PROPERTIES.add(property);
                property.getLineTypes().forEach(lineType -> AVAILABLE_PROPERTIES_BY_TYPE.put(lineType, property));
            }
        });
    }

    private final String name;
    private final ArgumentHandler<T> argumentHandler;
    private final Set<HologramLineType> lineTypes;

    private HologramProperty(String name, ArgumentHandler<T> argumentHandler, HologramLineType lineType) {
        this.name = name;
        this.argumentHandler = argumentHandler;
        this.lineTypes = lineType == null ? EnumSet.allOf(HologramLineType.class) : EnumSet.of(lineType);
        KNOWN_PROPERTIES.add(this);
    }

    private HologramProperty(String name, ArgumentHandler<T> argumentHandler) {
        this(name, argumentHandler, null);
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
                "type=" + this.argumentHandler + ']';
    }

    public static Collection<HologramProperty<?>> values() {
        return Set.copyOf(AVAILABLE_PROPERTIES);
    }

    public static Collection<HologramProperty<?>> values(HologramLineType lineType) {
        return Set.copyOf(AVAILABLE_PROPERTIES_BY_TYPE.get(lineType));
    }

    public static HologramProperty<?> valueOf(String name) {
        return Set.copyOf(AVAILABLE_PROPERTIES).stream()
                .filter(property -> property.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
