package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.datatype.CustomPersistentDataType;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.HologramGroupSorting;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayAlignment;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosegarden.registry.RoseKey;
import dev.rosewood.rosegarden.registry.RoseRegistry;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class HologramProperties {

    @SuppressWarnings("unchecked")
    public static final RoseRegistry<HologramProperty<?>> REGISTRY = RoseRegistry.create((Class<HologramProperty<?>>) (Class<?>) HologramProperty.class);
    private static final Set<HologramProperty<?>> AVAILABLE_PROPERTIES = new HashSet<>();

    // Hologram Group
    public static final HologramProperty<Long> UPDATE_INTERVAL = create("update_interval", DisplaysArgumentHandlers.DURATION, PersistentDataType.LONG);
    public static final HologramProperty<Integer> RENDER_DISTANCE = create("render_distance", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 1, 1024), PersistentDataType.INTEGER);
    public static final HologramProperty<HologramGroupSorting> HOLOGRAM_SORTING = create("hologram_sorting", ArgumentHandlers.forEnum(HologramGroupSorting.class), CustomPersistentDataType.forEnum(HologramGroupSorting.class));

    // Display Entity
    public static final HologramProperty<Boolean> GLOWING = createMapped("glowing", ArgumentHandlers.BOOLEAN, PersistentDataType.BOOLEAN);
    public static final HologramProperty<Integer> INTERPOLATION_DELAY = createMapped("interpolation_delay", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 60 * 60 * 20), PersistentDataType.INTEGER);
    public static final HologramProperty<Integer> INTERPOLATION_DURATION = createMapped("interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 60 * 60 * 20), PersistentDataType.INTEGER);
    public static final HologramProperty<Integer> TRANSFORMATION_INTERPOLATION_DURATION = createMapped("transformation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE), PersistentDataType.INTEGER);
    public static final HologramProperty<Integer> POSITION_ROTATION_INTERPOLATION_DURATION = createMapped("position_rotation_interpolation_duration", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE), PersistentDataType.INTEGER);
    public static final HologramProperty<Vector3> TRANSLATION = createMapped("translation", DisplaysArgumentHandlers.VECTOR3, CustomPersistentDataType.VECTOR3);
    public static final HologramProperty<Vector3> SCALE = createMapped("scale", DisplaysArgumentHandlers.VECTOR3, CustomPersistentDataType.VECTOR3);
    public static final HologramProperty<Quaternion> ROTATION_LEFT = createMapped("rotation_left", DisplaysArgumentHandlers.QUATERNION, CustomPersistentDataType.QUATERNION);
    public static final HologramProperty<Quaternion> ROTATION_RIGHT = createMapped("rotation_right", DisplaysArgumentHandlers.QUATERNION, CustomPersistentDataType.QUATERNION);
    public static final HologramProperty<BillboardConstraint> BILLBOARD_CONSTRAINT = createMapped("billboard_constraint", ArgumentHandlers.forEnum(BillboardConstraint.class), CustomPersistentDataType.forEnum(BillboardConstraint.class));
    public static final HologramProperty<Integer> BLOCK_LIGHT_OVERRIDE = createMapped("block_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15), PersistentDataType.INTEGER);
    public static final HologramProperty<Integer> SKY_LIGHT_OVERRIDE = createMapped("sky_light_override", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, 15), PersistentDataType.INTEGER);
    public static final HologramProperty<Float> VIEW_RANGE = createMapped("view_range", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE), PersistentDataType.FLOAT);
    public static final HologramProperty<Float> SHADOW_RADIUS = createMapped("shadow_radius", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE), PersistentDataType.FLOAT);
    public static final HologramProperty<Float> SHADOW_STRENGTH = createMapped("shadow_strength", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE), PersistentDataType.FLOAT);
    public static final HologramProperty<Float> WIDTH = createMapped("width", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.MAX_VALUE), PersistentDataType.FLOAT);
    public static final HologramProperty<Float> HEIGHT = createMapped("height", DisplaysArgumentHandlers.forDoubleRange(ArgumentHandlers.FLOAT, 0, Float.POSITIVE_INFINITY), PersistentDataType.FLOAT);
    public static final HologramProperty<Color> GLOW_COLOR_OVERRIDE = createMapped("glow_color_override", ArgumentHandlers.JAVA_COLOR, CustomPersistentDataType.JAVA_COLOR);

    // Text Display
    public static final HologramProperty<String> TEXT = createMapped("text", ArgumentHandlers.GREEDY_STRING, PersistentDataType.STRING);
    public static final HologramProperty<Integer> LINE_WIDTH = createMapped("line_width", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.INTEGER, 0, Integer.MAX_VALUE), PersistentDataType.INTEGER);
    public static final HologramProperty<Color> BACKGROUND_COLOR = createMapped("background_color", ArgumentHandlers.JAVA_COLOR, CustomPersistentDataType.JAVA_COLOR);
    public static final HologramProperty<Byte> TEXT_OPACITY = createMapped("text_opacity", DisplaysArgumentHandlers.forIntRange(ArgumentHandlers.BYTE, -1, Byte.MAX_VALUE), PersistentDataType.BYTE);
    public static final HologramProperty<Boolean> HAS_SHADOW = createMapped("has_shadow", ArgumentHandlers.BOOLEAN, PersistentDataType.BOOLEAN);
    public static final HologramProperty<Boolean> SEE_THROUGH = createMapped("see_through", ArgumentHandlers.BOOLEAN, PersistentDataType.BOOLEAN);
    public static final HologramProperty<Boolean> USE_DEFAULT_BACKGROUND_COLOR = createMapped("use_default_background_color", ArgumentHandlers.BOOLEAN, PersistentDataType.BOOLEAN);
    public static final HologramProperty<TextDisplayAlignment> ALIGNMENT = createMapped("alignment", ArgumentHandlers.forEnum(TextDisplayAlignment.class), CustomPersistentDataType.forEnum(TextDisplayAlignment.class));
    // Custom Text Display
    public static final HologramProperty<Long> PLACEHOLDER_UPDATE_INTERVAL = create("placeholder_update_interval", DisplaysArgumentHandlers.DURATION, PersistentDataType.LONG);

    // Item Display
    public static final HologramProperty<ItemStack> ITEM = createMapped("item", DisplaysArgumentHandlers.ITEMSTACK, CustomPersistentDataType.ITEMSTACK);
    public static final HologramProperty<ItemDisplayType> DISPLAY_TYPE = createMapped("display_type", ArgumentHandlers.forEnum(ItemDisplayType.class), CustomPersistentDataType.forEnum(ItemDisplayType.class));

    // Block Display
    public static final HologramProperty<BlockData> BLOCK_DATA = createMapped("block_data", DisplaysArgumentHandlers.BLOCK_DATA, CustomPersistentDataType.BLOCK_DATA);

    // Initialize the available properties after all other properties have been loaded to avoid circular static loading
    static {
        NMSHandler nmsHandler = NMSAdapter.getHandler();
        REGISTRY.forEach(property -> {
            if (!(property instanceof MappedHologramProperty<?>) || nmsHandler.isPropertyAvailable(property))
                AVAILABLE_PROPERTIES.add(property);
        });
    }

    private HologramProperties() { }

    private static <T> HologramProperty<T> create(String name, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        HologramProperty<T> property = new HologramProperty<>(RoseKey.of(name), argumentHandler, persistentDataType);
        REGISTRY.register(property);
        return property;
    }

    private static <T> MappedHologramProperty<T> createMapped(String name, ArgumentHandler<T> argumentHandler, PersistentDataType<?, T> persistentDataType) {
        MappedHologramProperty<T> property = new MappedHologramProperty<>(RoseKey.of(name), argumentHandler, persistentDataType);
        REGISTRY.register(property);
        return property;
    }

    public static boolean isAvailable(HologramProperty<?> property) {
        return AVAILABLE_PROPERTIES.contains(property);
    }

}
