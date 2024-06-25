package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.Vector3;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HologramPropertyMappings {

    private static final HologramPropertyMappings INSTANCE = new HologramPropertyMappings();
    public static HologramPropertyMappings getInstance() {
        return INSTANCE;
    }

    private final Map<HologramProperty<?>, HologramPropertyMapping<?, ?>> propertyMappings;
    private final Map<HologramPropertyMapping<?, ?>, List<HologramProperty<?>>> propertyMappingMasks;
    private final Set<HologramProperty<?>> maskedProperties;

    private HologramPropertyMappings() {
        this.propertyMappings = new HashMap<>();
        this.propertyMappingMasks = new HashMap<>();
        this.maskedProperties = new HashSet<>();

        Function<Vector3, Vector3f> VECTOR3_TRANSFORMER = vec -> new Vector3f(vec.x(), vec.y(), vec.z());
        Function<Quaternion, Quaternionf> QUATERNION_TRANSFORMER = quat -> new Quaternionf(quat.x(), quat.y(), quat.z(), quat.w());
        Function<ItemDisplayType, Byte> ITEMDISPLAYTYPE_TRANSFORMER = type -> (byte) type.ordinal();
        Function<BillboardConstraint, Byte> BILLBOARDCONSTRAINT_TRANSFORMER = constraint -> (byte) constraint.ordinal();
        Function<BlockData, BlockState> BLOCKDATA_TRANSFORMER = data -> ((CraftBlockData) data).getState();
        Function<String, Component> TEXT_TRANSFORMER = text -> CraftChatMessage.fromStringOrNull(text, true);
        Function<Color, Integer> COLOR_TRANSFORMER = Color::getRGB;
        Function<Boolean, Byte> GLOWING_TRANSFORMER = glowing -> (byte) (glowing ? 0x40 : 0x00);

        // All
        this.define(HologramProperty.GLOWING, 0, EntityDataSerializers.BYTE, GLOWING_TRANSFORMER, (byte) 0);
        this.define(HologramProperty.INTERPOLATION_DELAY, 8, EntityDataSerializers.INT, Function.identity(), 0);
        this.define(HologramProperty.INTERPOLATION_DURATION, 9, EntityDataSerializers.INT, Function.identity(), 0);
        this.define(HologramProperty.TRANSLATION, 10, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, new Vector3f(0, 0, 0));
        this.define(HologramProperty.SCALE, 11, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, new Vector3f(1, 1, 1));
        this.define(HologramProperty.ROTATION_LEFT, 12, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, new Quaternionf(0, 0, 0, 1));
        this.define(HologramProperty.ROTATION_RIGHT, 13, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, new Quaternionf(0, 0, 0, 1));
        this.define(HologramProperty.BILLBOARD_CONSTRAINT, 14, EntityDataSerializers.BYTE, BILLBOARDCONSTRAINT_TRANSFORMER, (byte) 0);
        this.defineMask(15, EntityDataSerializers.INT, List.of(
                HologramProperty.BLOCK_LIGHT_OVERRIDE,
                HologramProperty.SKY_LIGHT_OVERRIDE
        ), properties -> {
            int mask = 0;
            if (properties.has(HologramProperty.BLOCK_LIGHT_OVERRIDE))
                mask |= properties.get(HologramProperty.BLOCK_LIGHT_OVERRIDE) << 4;
            if (properties.has(HologramProperty.SKY_LIGHT_OVERRIDE))
                mask |= properties.get(HologramProperty.SKY_LIGHT_OVERRIDE) << 20;
            return mask;
        }, -1);
        this.define(HologramProperty.VIEW_RANGE, 16, EntityDataSerializers.FLOAT, Function.identity(), 1.0F);
        this.define(HologramProperty.SHADOW_RADIUS, 17, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperty.SHADOW_STRENGTH, 18, EntityDataSerializers.FLOAT, Function.identity(), 1.0F);
        this.define(HologramProperty.WIDTH, 19, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperty.HEIGHT, 20, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperty.GLOW_COLOR_OVERRIDE, 21, EntityDataSerializers.INT, COLOR_TRANSFORMER, -1);

        // Text Display
        this.define(HologramProperty.TEXT, 22, EntityDataSerializers.COMPONENT, TEXT_TRANSFORMER, CraftChatMessage.fromStringOrNull(""));
        this.define(HologramProperty.LINE_WIDTH, 23, EntityDataSerializers.INT, Function.identity(), 200);
        this.define(HologramProperty.BACKGROUND_COLOR, 24, EntityDataSerializers.INT, COLOR_TRANSFORMER, 0x40000000);
        this.define(HologramProperty.TEXT_OPACITY, 25, EntityDataSerializers.BYTE, Function.identity(), (byte) -1);
        this.defineMask(26, EntityDataSerializers.BYTE, List.of(
                HologramProperty.HAS_SHADOW,
                HologramProperty.SEE_THROUGH,
                HologramProperty.USE_DEFAULT_BACKGROUND_COLOR,
                HologramProperty.ALIGNMENT
        ), properties -> {
            byte mask = 0;
            if (properties.has(HologramProperty.HAS_SHADOW) && properties.get(HologramProperty.HAS_SHADOW)) mask |= 0x01;
            if (properties.has(HologramProperty.SEE_THROUGH) && properties.get(HologramProperty.SEE_THROUGH)) mask |= 0x02;
            if (properties.has(HologramProperty.USE_DEFAULT_BACKGROUND_COLOR) && properties.get(HologramProperty.USE_DEFAULT_BACKGROUND_COLOR)) mask |= 0x04;
            if (properties.has(HologramProperty.ALIGNMENT))
                mask |= properties.get(HologramProperty.ALIGNMENT).ordinal() << 3;
            return mask;
        }, (byte) 0);

        // Item Display
        this.define(HologramProperty.ITEM, 22, EntityDataSerializers.ITEM_STACK, CraftItemStack::asNMSCopy, ItemStack.EMPTY);
        this.define(HologramProperty.DISPLAY_TYPE, 23, EntityDataSerializers.BYTE, ITEMDISPLAYTYPE_TRANSFORMER, (byte) 0);

        // Block Display
        this.define(HologramProperty.BLOCK_DATA, 22, EntityDataSerializers.BLOCK_STATE, BLOCKDATA_TRANSFORMER, ((CraftBlockData) Material.AIR.createBlockData()).getState());

        // Cache masked properties
        this.propertyMappingMasks.forEach((mapping, properties) -> this.maskedProperties.addAll(properties));
    }

    private <T, R> void define(HologramProperty<T> property, int accessorId, EntityDataSerializer<R> entityDataSerializer, Function<T, R> transformer, R defaultValue) {
        EntityDataAccessor<R> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<T, R> mapping = new HologramPropertyMapping<>(property.getType(), entityDataAccessor, transformer, defaultValue);
        this.propertyMappings.put(property, mapping);
    }

    private <T> void defineMask(int accessorId, EntityDataSerializer<T> entityDataSerializer, List<HologramProperty<?>> properties, Function<HologramProperties, T> transformer, T defaultValue) {
        EntityDataAccessor<T> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<HologramProperties, T> mapping = new HologramPropertyMapping<>(HologramProperties.class, entityDataAccessor, transformer, defaultValue);
        this.propertyMappingMasks.put(mapping, properties);
    }

    @SuppressWarnings("unchecked")
    private <T> SynchedEntityData.DataValue<?> createDataValue(HologramProperty<T> property, Object value) {
        HologramPropertyMapping<?, ?> mapping = this.propertyMappings.get(property);
        if (mapping == null)
            throw new IllegalArgumentException("Unknown property " + property.getName() + "!");

        if (value != null && mapping.inputPropertyType() != value.getClass())
            throw new IllegalArgumentException("Value type " + value.getClass().getName() + " does not match property type " + mapping.inputPropertyType() + "!");

        return ((HologramPropertyMapping<T, ?>) mapping).createDataValue((T) value);
    }

    @SuppressWarnings("unchecked")
    private SynchedEntityData.DataValue<?> createMaskDataValue(HologramPropertyMapping<?, ?> mapping, HologramProperties properties) {
        return ((HologramPropertyMapping<HologramProperties, ?>) mapping).createDataValue(properties);
    }

    public List<SynchedEntityData.DataValue<?>> createFreshDataValues(HologramLine hologramLine) {
        return this.createDataValues(hologramLine.getProperties(), true);
    }

    public List<SynchedEntityData.DataValue<?>> createDataValues(HologramLine hologramLine) {
        return this.createDataValues(hologramLine.getProperties(), false);
    }

    private List<SynchedEntityData.DataValue<?>> createDataValues(HologramProperties properties, boolean fresh) {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();

        HologramProperties actingProperties;
        if (fresh) {
            actingProperties = properties;
        } else {
            actingProperties = properties.getDirty();
        }

        // Create data values for normal properties
        actingProperties.forEach((property, value) -> {
            if (!this.maskedProperties.contains(property))
                dataValues.add(this.createDataValue(property, value));
        });

        // Create data values for masked properties
        this.propertyMappingMasks.forEach((mapping, maskProperties) -> {
            // Use all properties to create the mask, unmodified values still need to be present to build the full mask
            if (maskProperties.stream().anyMatch(actingProperties::has))
                dataValues.add(this.createMaskDataValue(mapping, properties));
        });

        return dataValues;
    }

    public boolean isAvailable(HologramProperty<?> property) {
        return this.propertyMappings.containsKey(property);
    }

}
