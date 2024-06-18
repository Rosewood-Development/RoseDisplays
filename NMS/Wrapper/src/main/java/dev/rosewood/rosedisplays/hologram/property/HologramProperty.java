package dev.rosewood.rosedisplays.hologram.property;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayAlignment;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
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
    public static final HologramProperty<Boolean> GLOWING = new HologramProperty<>("glowing", Boolean.class);
    public static final HologramProperty<Integer> INTERPOLATION_DELAY = new HologramProperty<>("interpolation_delay", Integer.class);
    public static final HologramProperty<Integer> INTERPOLATION_DURATION = new HologramProperty<>("interpolation_duration", Integer.class);
    public static final HologramProperty<Integer> TRANSFORMATION_INTERPOLATION_DURATION = new HologramProperty<>("transformation_interpolation_duration", Integer.class);
    public static final HologramProperty<Integer> POSITION_ROTATION_INTERPOLATION_DURATION = new HologramProperty<>("position_rotation_interpolation_duration", Integer.class);
    public static final HologramProperty<Vector3> TRANSLATION = new HologramProperty<>("translation", Vector3.class);
    public static final HologramProperty<Vector3> SCALE = new HologramProperty<>("scale", Vector3.class);
    public static final HologramProperty<Quaternion> ROTATION_LEFT = new HologramProperty<>("rotation_left", Quaternion.class);
    public static final HologramProperty<Quaternion> ROTATION_RIGHT = new HologramProperty<>("rotation_right", Quaternion.class);
    public static final HologramProperty<BillboardConstraint> BILLBOARD_CONSTRAINT = new HologramProperty<>("billboard_constraint", BillboardConstraint.class);
    public static final HologramProperty<Integer> BLOCK_LIGHT_OVERRIDE = new HologramProperty<>("block_light_override", Integer.class);
    public static final HologramProperty<Integer> SKY_LIGHT_OVERRIDE = new HologramProperty<>("sky_light_override", Integer.class);
    public static final HologramProperty<Float> VIEW_RANGE = new HologramProperty<>("view_range", Float.class);
    public static final HologramProperty<Float> SHADOW_RADIUS = new HologramProperty<>("shadow_radius", Float.class);
    public static final HologramProperty<Float> SHADOW_STRENGTH = new HologramProperty<>("shadow_strength", Float.class);
    public static final HologramProperty<Float> WIDTH = new HologramProperty<>("width", Float.class);
    public static final HologramProperty<Float> HEIGHT = new HologramProperty<>("height", Float.class);
    public static final HologramProperty<Color> GLOW_COLOR_OVERRIDE = new HologramProperty<>("glow_color_override", Color.class);

    // Text Display
    public static final HologramProperty<String> TEXT = new HologramProperty<>("text", String.class, HologramLineType.TEXT);
    public static final HologramProperty<Integer> LINE_WIDTH = new HologramProperty<>("line_width", Integer.class, HologramLineType.TEXT);
    public static final HologramProperty<Color> BACKGROUND_COLOR = new HologramProperty<>("background_color", Color.class, HologramLineType.TEXT);
    public static final HologramProperty<Byte> TEXT_OPACITY = new HologramProperty<>("text_opacity", Byte.class, HologramLineType.TEXT);
    public static final HologramProperty<Boolean> HAS_SHADOW = new HologramProperty<>("has_shadow", Boolean.class, HologramLineType.TEXT);
    public static final HologramProperty<Boolean> SEE_THROUGH = new HologramProperty<>("see_through", Boolean.class, HologramLineType.TEXT);
    public static final HologramProperty<Boolean> USE_DEFAULT_BACKGROUND_COLOR = new HologramProperty<>("use_default_background_color", Boolean.class, HologramLineType.TEXT);
    public static final HologramProperty<TextDisplayAlignment> ALIGNMENT = new HologramProperty<>("alignment", TextDisplayAlignment.class, HologramLineType.TEXT);

    // Item Display
    public static final HologramProperty<ItemStack> ITEM = new HologramProperty<>("item", ItemStack.class, HologramLineType.ITEM);
    public static final HologramProperty<ItemDisplayType> DISPLAY_TYPE = new HologramProperty<>("display_type", ItemDisplayType.class, HologramLineType.ITEM);

    // Block Display
    public static final HologramProperty<BlockData> BLOCK_DATA = new HologramProperty<>("block_data", BlockData.class, HologramLineType.BLOCK);

    static {
        // This has to be down here instead of in the HologramProperty constructor since it can cause a circular dependency
        VersionAvailabilityProvider versionAvailabilityProvider = NMSAdapter.getHandler().getVersionAvailabilityProvider();
        KNOWN_PROPERTIES.forEach(property -> {
            if (versionAvailabilityProvider.isAvailable(property)) {
                AVAILABLE_PROPERTIES.add(property);
                property.getLineTypes().forEach(lineType -> AVAILABLE_PROPERTIES_BY_TYPE.put(lineType, property));
            }
        });
    }

    private final String name;
    private final Class<T> type;
    private final Set<HologramLineType> lineTypes;

    private HologramProperty(String name, Class<T> type, HologramLineType lineType) {
        this.name = name;
        this.type = type;
        this.lineTypes = lineType == null ? EnumSet.allOf(HologramLineType.class) : EnumSet.of(lineType);
        KNOWN_PROPERTIES.add(this);
    }

    private HologramProperty(String name, Class<T> type) {
        this(name, type, null);
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
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
                "type=" + this.type + ']';
    }

    public static Collection<HologramProperty<?>> values() {
        return Collections.unmodifiableCollection(AVAILABLE_PROPERTIES);
    }

    public static Collection<HologramProperty<?>> values(HologramLineType lineType) {
        return Collections.unmodifiableCollection(AVAILABLE_PROPERTIES_BY_TYPE.get(lineType));
    }

}
