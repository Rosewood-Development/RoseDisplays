package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.BrightnessOverride;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayProperties;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.property.HologramProperty;
import dev.rosewood.rosedisplays.property.HologramPropertyProvider;
import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HologramPropertyProviderImpl implements HologramPropertyProvider {

    private static final Map<HologramLineType, Map<String, HologramPropertyMapping<?, ?>>> PROPERTY_MAPPINGS;
    private static final Map<String, HologramPropertyMapping<?, ?>> PROPERTY_MAPPINGS_BY_NAME;
    static {
        PROPERTY_MAPPINGS = new EnumMap<>(HologramLineType.class);
        Stream.of(HologramLineType.values()).forEach(type -> PROPERTY_MAPPINGS.put(type, new HashMap<>()));

        Function<Vector3, Vector3f> VECTOR3_TRANSFORMER = vec -> new Vector3f(vec.x(), vec.y(), vec.z());
        Function<Quaternion, Quaternionf> QUATERNION_TRANSFORMER = quat -> new Quaternionf(quat.x(), quat.y(), quat.z(), quat.w());
        Function<ItemDisplayType, Byte> ITEMDISPLAYTYPE_TRANSFORMER = type -> (byte) type.ordinal();
        Function<BillboardConstraint, Byte> BILLBOARDCONSTRAINT_TRANSFORMER = constraint -> (byte) constraint.ordinal();
        Function<BlockData, BlockState> BLOCKDATA_TRANSFORMER = data -> ((CraftBlockData) data).getState();
        Function<String, Component> TEXT_TRANSFORMER = text -> CraftChatMessage.fromStringOrNull(text, true);
        Function<Color, Integer> COLOR_TRANSFORMER = Color::getRGB;
        Function<Boolean, Byte> GLOWING_TRANSFORMER = glowing -> (byte) (glowing ? 0x40 : 0x00);
        Function<BrightnessOverride, Integer> BRIGHTNESSOVERRIDE_TRANSFORMER = override -> (override.blockLight() << 4) | (override.skyLight() << 20);
        Function<TextDisplayProperties, Byte> TEXTDISPLAYPROPERTIES_TRANSFORMER = props -> {
            byte value = 0;
            if (props.hasShadow()) value |= 0x01;
            if (props.isSeeThrough()) value |= 0x02;
            if (props.useDefaultBackgroundColor()) value |= 0x04;
            value |= props.alignment().ordinal() << 3;
            return value;
        };

        // All
        define(HologramProperty.GLOWING, 0, EntityDataSerializers.BYTE, GLOWING_TRANSFORMER);
        define(HologramProperty.INTERPOLATION_DELAY, 8, EntityDataSerializers.INT, Function.identity());
        define(HologramProperty.INTERPOLATION_DURATION, 9, EntityDataSerializers.INT, Function.identity());
        define(HologramProperty.TRANSLATION, 10, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER);
        define(HologramProperty.SCALE, 11, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER);
        define(HologramProperty.ROTATION_LEFT, 12, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER);
        define(HologramProperty.ROTATION_RIGHT, 13, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER);
        define(HologramProperty.BILLBOARD_CONSTRAINT, 14, EntityDataSerializers.BYTE, BILLBOARDCONSTRAINT_TRANSFORMER);
        define(HologramProperty.BRIGHTNESS_OVERRIDE, 15, EntityDataSerializers.INT, BRIGHTNESSOVERRIDE_TRANSFORMER);
        define(HologramProperty.VIEW_RANGE, 16, EntityDataSerializers.FLOAT, Function.identity());
        define(HologramProperty.SHADOW_RADIUS, 17, EntityDataSerializers.FLOAT, Function.identity());
        define(HologramProperty.SHADOW_STRENGTH, 18, EntityDataSerializers.FLOAT, Function.identity());
        define(HologramProperty.WIDTH, 19, EntityDataSerializers.FLOAT, Function.identity());
        define(HologramProperty.HEIGHT, 20, EntityDataSerializers.FLOAT, Function.identity());
        define(HologramProperty.GLOW_COLOR_OVERRIDE, 21, EntityDataSerializers.INT, COLOR_TRANSFORMER);

        // Text Display
        define(HologramProperty.TEXT, 22, EntityDataSerializers.COMPONENT, TEXT_TRANSFORMER, HologramLineType.TEXT);
        define(HologramProperty.LINE_WIDTH, 23, EntityDataSerializers.INT, Function.identity(), HologramLineType.TEXT);
        define(HologramProperty.BACKGROUND_COLOR, 24, EntityDataSerializers.INT, COLOR_TRANSFORMER, HologramLineType.TEXT);
        define(HologramProperty.TEXT_OPACITY, 25, EntityDataSerializers.BYTE, Function.identity(), HologramLineType.TEXT);
        define(HologramProperty.BIT_MASK, 26, EntityDataSerializers.BYTE, TEXTDISPLAYPROPERTIES_TRANSFORMER, HologramLineType.TEXT);

        // Item Display
        define(HologramProperty.ITEM, 22, EntityDataSerializers.ITEM_STACK, CraftItemStack::asNMSCopy, HologramLineType.ITEM);
        define(HologramProperty.DISPLAY_TYPE, 23, EntityDataSerializers.BYTE, ITEMDISPLAYTYPE_TRANSFORMER, HologramLineType.ITEM);

        // Block Display
        define(HologramProperty.BLOCK_DATA, 22, EntityDataSerializers.BLOCK_STATE, BLOCKDATA_TRANSFORMER, HologramLineType.BLOCK);

        PROPERTY_MAPPINGS_BY_NAME = PROPERTY_MAPPINGS.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toMap(x -> x.property().getName(), Function.identity()));
    }

    private static final HologramPropertyProviderImpl INSTANCE = new HologramPropertyProviderImpl();
    public static HologramPropertyProviderImpl getInstance() {
        return INSTANCE;
    }

    private HologramPropertyProviderImpl() {

    }

    private static <T, R> void define(HologramProperty<T> property, int accessorId, EntityDataSerializer<R> entityDataSerializer, Function<T, R> transformer, HologramLineType... lineTypes) {
        EntityDataAccessor<R> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<T, R> mapping = new HologramPropertyMapping<>(property, entityDataAccessor, transformer);
        if (lineTypes.length == 0) { // All
            PROPERTY_MAPPINGS.values().forEach(map -> map.put(property.getName(), mapping));
        } else {
            Stream.of(lineTypes).forEach(type -> PROPERTY_MAPPINGS.get(type).put(property.getName(), mapping));
        }
    }

    @Override
    public Map<String, HologramProperty<?>> getProperties(HologramLineType type) {
        return PROPERTY_MAPPINGS.get(type).entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue().property()));
    }

    @Override
    public Map<String, HologramProperty<?>> getAllProperties() {
        return PROPERTY_MAPPINGS_BY_NAME.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue().property()));
    }

    @SuppressWarnings("unchecked")
    public <T> SynchedEntityData.DataValue<?> createDataValue(String name, T value) {
        HologramPropertyMapping<?, ?> mapping = PROPERTY_MAPPINGS_BY_NAME.get(name);
        if (mapping == null)
            throw new IllegalArgumentException("Unknown property " + name + "!");
        if (mapping.property().getType() != value.getClass())
            throw new IllegalArgumentException("Value type " + value.getClass().getName() + " does not match property type " + mapping.property().getName() + "!");
        return ((HologramPropertyMapping<T, ?>) mapping).createDataValue(value);
    }

}
