package dev.rosewood.rosedisplays.property;

import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.BrightnessOverride;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayProperties;
import dev.rosewood.rosedisplays.model.Vector3;
import java.awt.Color;
import java.util.Objects;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public final class HologramProperty<T> {

    // All
    public static final HologramProperty<Boolean> GLOWING = new HologramProperty<>("glowing", Boolean.class);
    public static final HologramProperty<Integer> INTERPOLATION_DELAY = new HologramProperty<>("interpolation_delay", Integer.class);
    public static final HologramProperty<Integer> INTERPOLATION_DURATION = new HologramProperty<>("interpolation_duration", Integer.class);
    public static final HologramProperty<Vector3> TRANSLATION = new HologramProperty<>("translation", Vector3.class);
    public static final HologramProperty<Vector3> SCALE = new HologramProperty<>("scale", Vector3.class);
    public static final HologramProperty<Quaternion> ROTATION_LEFT = new HologramProperty<>("rotation_left", Quaternion.class);
    public static final HologramProperty<Quaternion> ROTATION_RIGHT = new HologramProperty<>("rotation_right", Quaternion.class);
    public static final HologramProperty<BillboardConstraint> BILLBOARD_CONSTRAINT = new HologramProperty<>("billboard_constraint", BillboardConstraint.class);
    public static final HologramProperty<BrightnessOverride> BRIGHTNESS_OVERRIDE = new HologramProperty<>("brightness_override", BrightnessOverride.class);
    public static final HologramProperty<Float> VIEW_RANGE = new HologramProperty<>("view_range", Float.class);
    public static final HologramProperty<Float> SHADOW_RADIUS = new HologramProperty<>("shadow_radius", Float.class);
    public static final HologramProperty<Float> SHADOW_STRENGTH = new HologramProperty<>("shadow_strength", Float.class);
    public static final HologramProperty<Float> WIDTH = new HologramProperty<>("width", Float.class);
    public static final HologramProperty<Float> HEIGHT = new HologramProperty<>("height", Float.class);
    public static final HologramProperty<Color> GLOW_COLOR_OVERRIDE = new HologramProperty<>("glow_color_override", Color.class);

    // Text Display
    public static final HologramProperty<String> TEXT = new HologramProperty<>("text", String.class);
    public static final HologramProperty<Integer> LINE_WIDTH = new HologramProperty<>("line_width", Integer.class);
    public static final HologramProperty<Color> BACKGROUND_COLOR = new HologramProperty<>("background_color", Color.class);
    public static final HologramProperty<Byte> TEXT_OPACITY = new HologramProperty<>("text_opacity", Byte.class);
    public static final HologramProperty<TextDisplayProperties> BIT_MASK = new HologramProperty<>("bit_mask", TextDisplayProperties.class);

    // Item Display
    public static final HologramProperty<ItemStack> ITEM = new HologramProperty<>("item", ItemStack.class);
    public static final HologramProperty<ItemDisplayType> DISPLAY_TYPE = new HologramProperty<>("display_type", ItemDisplayType.class);

    // Block Display
    public static final HologramProperty<BlockData> BLOCK_DATA = new HologramProperty<>("block_data", BlockData.class);

    private final String name;
    private final Class<T> type;

    private HologramProperty(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
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

}
