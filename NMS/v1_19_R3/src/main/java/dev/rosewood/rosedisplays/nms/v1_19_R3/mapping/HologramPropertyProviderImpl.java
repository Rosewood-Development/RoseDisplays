package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayProperties;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.property.HologramProperty;
import dev.rosewood.rosedisplays.property.HologramPropertyProvider;
import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HologramPropertyProviderImpl implements HologramPropertyProvider {

    private static final Function<Vector3, Vector3f> VECTOR3_TRANSFORMER = vec -> new Vector3f(vec.x(), vec.y(), vec.z());
    private static final Function<Quaternion, Quaternionf> QUATERNION_TRANSFORMER = quat -> new Quaternionf(quat.x(), quat.y(), quat.z(), quat.w());
    private static final Function<ItemDisplayType, Byte> ITEMDISPLAYTYPE_TRANSFORMER = type -> (byte) type.ordinal();
    private static final Function<BlockData, BlockState> BLOCKDATA_TRANSFORMER = data -> ((CraftBlockData) data).getState();
    private static final Function<TextDisplayProperties, Byte> TEXTDISPLAYPROPERTIES_TRANSFORMER = props -> {
        byte value = 0;
        if (props.hasShadow()) value |= 0x01;
        if (props.isSeeThrough()) value |= 0x02;
        if (props.useDefaultBackgroundColor()) value |= 0x04;
        value |= props.alignment().ordinal() << 3;
        return value;
    };

    private static final Map<HologramLineType, Map<String, HologramPropertyMapping<?, ?>>> properties;
    private static final Map<String, HologramPropertyMapping<?, ?>> propertiesByName;
    static {
        properties = new EnumMap<>(HologramLineType.class);
        Stream.of(HologramLineType.values()).forEach(type -> properties.put(type, new HashMap<>()));

        // All
        define("interpolation_delay", 8, Integer.class, EntityDataSerializers.INT, Function.identity(), HologramLineType.values());
        define("interpolation_duration", 9, Integer.class, EntityDataSerializers.INT, Function.identity(), HologramLineType.values());
        define("translation", 10, Vector3.class, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, HologramLineType.values());
        define("scale", 11, Vector3.class, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, HologramLineType.values());
        define("rotation_left", 12, Quaternion.class, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, HologramLineType.values());
        define("rotation_right", 13, Quaternion.class, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, HologramLineType.values());
        define("billboard_constraint", 14, Byte.class, EntityDataSerializers.BYTE, Function.identity(), HologramLineType.values());
        define("brightness_override", 15, Integer.class, EntityDataSerializers.INT, Function.identity(), HologramLineType.values());
        define("view_range", 16, Float.class, EntityDataSerializers.FLOAT, Function.identity(), HologramLineType.values());
        define("shadow_radius", 17, Float.class, EntityDataSerializers.FLOAT, Function.identity(), HologramLineType.values());
        define("shadow_strength", 18, Float.class, EntityDataSerializers.FLOAT, Function.identity(), HologramLineType.values());
        define("width", 19, Float.class, EntityDataSerializers.FLOAT, Function.identity(), HologramLineType.values());
        define("height", 20, Float.class, EntityDataSerializers.FLOAT, Function.identity(), HologramLineType.values());
        define("glow_color_override", 21, Integer.class, EntityDataSerializers.INT, Function.identity(), HologramLineType.values());

        // Text Display
        define("text", 22, String.class, EntityDataSerializers.COMPONENT, CraftChatMessage::fromStringOrNull, HologramLineType.TEXT);
        define("line_width", 23, Integer.class, EntityDataSerializers.INT, Function.identity(), HologramLineType.TEXT);
        define("background_color", 24, Color.class, EntityDataSerializers.INT, Color::getRGB, HologramLineType.TEXT);
        define("text_opacity", 25, Byte.class, EntityDataSerializers.BYTE, Function.identity(), HologramLineType.TEXT);
        define("bit_mask", 26, TextDisplayProperties.class, EntityDataSerializers.BYTE, TEXTDISPLAYPROPERTIES_TRANSFORMER, HologramLineType.TEXT);

        // Item Display
        define("item", 22, ItemStack.class, EntityDataSerializers.ITEM_STACK, CraftItemStack::asNMSCopy, HologramLineType.ITEM);
        define("display_type", 23, ItemDisplayType.class, EntityDataSerializers.BYTE, ITEMDISPLAYTYPE_TRANSFORMER, HologramLineType.ITEM);

        // Block Display
        define("block_data", 22, BlockData.class, EntityDataSerializers.BLOCK_STATE, BLOCKDATA_TRANSFORMER, HologramLineType.BLOCK);

        propertiesByName = properties.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toMap(HologramPropertyMapping::property, Function.identity()));
    }

    private static HologramPropertyProviderImpl instance;

    private HologramPropertyProviderImpl() {

    }

    public static HologramPropertyProviderImpl getInstance() {
        if (instance == null)
            instance = new HologramPropertyProviderImpl();
        return instance;
    }

    private static <T, R> void define(String name, int accessorId, Class<T> propertyType, EntityDataSerializer<R> entityDataSerializer, Function<T, R> transformer, HologramLineType... validTypes) {
        EntityDataAccessor<R> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<T, R> mapping = new HologramPropertyMapping<>(name, propertyType, entityDataAccessor, transformer);
        Stream.of(validTypes).forEach(type -> properties.get(type).put(name, mapping));
    }

    @Override
    public Map<String, HologramProperty<?>> getProperties(HologramLineType type) {
        return Collections.unmodifiableMap(properties.get(type));
    }

    @Override
    public Map<String, HologramProperty<?>> getAllProperties() {
        return Collections.unmodifiableMap(propertiesByName);
    }

    @SuppressWarnings("unchecked")
    public <T> SynchedEntityData.DataValue<?> createDataValue(String name, T value) {
        HologramPropertyMapping<?, ?> mapping = propertiesByName.get(name);
        if (mapping == null)
            throw new IllegalArgumentException("Unknown property " + name + "!");
        if (mapping.propertyType() != value.getClass())
            throw new IllegalArgumentException("Value type " + value.getClass().getName() + " does not match property type " + mapping.propertyType().getName() + "!");
        return ((HologramPropertyMapping<T, ?>) mapping).createDataValue(value);
    }

}
