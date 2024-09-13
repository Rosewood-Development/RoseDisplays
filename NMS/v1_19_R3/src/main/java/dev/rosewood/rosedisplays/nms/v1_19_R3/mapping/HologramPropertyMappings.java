package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.property.MappedHologramProperty;
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
        this.define(HologramProperties.GLOWING, 0, EntityDataSerializers.BYTE, GLOWING_TRANSFORMER, (byte) 0);
        this.define(HologramProperties.INTERPOLATION_DELAY, 8, EntityDataSerializers.INT, Function.identity(), 0);
        this.define(HologramProperties.INTERPOLATION_DURATION, 9, EntityDataSerializers.INT, Function.identity(), 0);
        this.define(HologramProperties.TRANSLATION, 10, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, new Vector3f(0, 0, 0));
        this.define(HologramProperties.SCALE, 11, EntityDataSerializers.VECTOR3, VECTOR3_TRANSFORMER, new Vector3f(1, 1, 1));
        this.define(HologramProperties.ROTATION_LEFT, 12, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, new Quaternionf(0, 0, 0, 1));
        this.define(HologramProperties.ROTATION_RIGHT, 13, EntityDataSerializers.QUATERNION, QUATERNION_TRANSFORMER, new Quaternionf(0, 0, 0, 1));
        this.define(HologramProperties.BILLBOARD_CONSTRAINT, 14, EntityDataSerializers.BYTE, BILLBOARDCONSTRAINT_TRANSFORMER, (byte) 0);
        this.defineMask(15, EntityDataSerializers.INT, List.of(
                HologramProperties.BLOCK_LIGHT_OVERRIDE,
                HologramProperties.SKY_LIGHT_OVERRIDE
        ), properties -> {
            int mask = 0;
            if (properties.has(HologramProperties.BLOCK_LIGHT_OVERRIDE))
                mask |= properties.get(HologramProperties.BLOCK_LIGHT_OVERRIDE) << 4;
            if (properties.has(HologramProperties.SKY_LIGHT_OVERRIDE))
                mask |= properties.get(HologramProperties.SKY_LIGHT_OVERRIDE) << 20;
            return mask;
        }, -1);
        this.define(HologramProperties.VIEW_RANGE, 16, EntityDataSerializers.FLOAT, Function.identity(), 1.0F);
        this.define(HologramProperties.SHADOW_RADIUS, 17, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperties.SHADOW_STRENGTH, 18, EntityDataSerializers.FLOAT, Function.identity(), 1.0F);
        this.define(HologramProperties.WIDTH, 19, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperties.HEIGHT, 20, EntityDataSerializers.FLOAT, Function.identity(), 0.0F);
        this.define(HologramProperties.GLOW_COLOR_OVERRIDE, 21, EntityDataSerializers.INT, COLOR_TRANSFORMER, -1);

        // Text Display
        this.define(HologramProperties.TEXT, 22, EntityDataSerializers.COMPONENT, TEXT_TRANSFORMER, CraftChatMessage.fromStringOrNull(" "));
        this.define(HologramProperties.LINE_WIDTH, 23, EntityDataSerializers.INT, Function.identity(), 200);
        this.define(HologramProperties.BACKGROUND_COLOR, 24, EntityDataSerializers.INT, COLOR_TRANSFORMER, 0x40000000);
        this.define(HologramProperties.TEXT_OPACITY, 25, EntityDataSerializers.BYTE, Function.identity(), (byte) -1);
        this.defineMask(26, EntityDataSerializers.BYTE, List.of(
                HologramProperties.HAS_SHADOW,
                HologramProperties.SEE_THROUGH,
                HologramProperties.USE_DEFAULT_BACKGROUND_COLOR,
                HologramProperties.ALIGNMENT
        ), properties -> {
            byte mask = 0;
            if (properties.has(HologramProperties.HAS_SHADOW) && properties.get(HologramProperties.HAS_SHADOW)) mask |= 0x01;
            if (properties.has(HologramProperties.SEE_THROUGH) && properties.get(HologramProperties.SEE_THROUGH)) mask |= 0x02;
            if (properties.has(HologramProperties.USE_DEFAULT_BACKGROUND_COLOR) && properties.get(HologramProperties.USE_DEFAULT_BACKGROUND_COLOR)) mask |= 0x04;
            if (properties.has(HologramProperties.ALIGNMENT))
                mask |= properties.get(HologramProperties.ALIGNMENT).ordinal() << 3;
            return mask;
        }, (byte) 0);

        // Item Display
        this.define(HologramProperties.ITEM, 22, EntityDataSerializers.ITEM_STACK, CraftItemStack::asNMSCopy, ItemStack.EMPTY);
        this.define(HologramProperties.DISPLAY_TYPE, 23, EntityDataSerializers.BYTE, ITEMDISPLAYTYPE_TRANSFORMER, (byte) 0);

        // Block Display
        this.define(HologramProperties.BLOCK_DATA, 22, EntityDataSerializers.BLOCK_STATE, BLOCKDATA_TRANSFORMER, ((CraftBlockData) Material.AIR.createBlockData()).getState());

        // Cache masked properties
        this.propertyMappingMasks.forEach((mapping, properties) -> this.maskedProperties.addAll(properties));
    }

    private <T, R> void define(HologramProperty<T> property, int accessorId, EntityDataSerializer<R> entityDataSerializer, Function<T, R> transformer, R defaultValue) {
        EntityDataAccessor<R> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<T, R> mapping = new HologramPropertyMapping<>(property.getValueType(), entityDataAccessor, transformer, defaultValue);
        this.propertyMappings.put(property, mapping);
    }

    private <T> void defineMask(int accessorId, EntityDataSerializer<T> entityDataSerializer, List<HologramProperty<?>> properties, Function<HologramPropertyContainer, T> transformer, T defaultValue) {
        if (properties.stream().anyMatch(x -> !(x instanceof MappedHologramProperty<?>)))
            throw new IllegalArgumentException("Cannot define a mask for unmapped properties");

        EntityDataAccessor<T> entityDataAccessor = entityDataSerializer.createAccessor(accessorId);
        HologramPropertyMapping<HologramPropertyContainer, T> mapping = new HologramPropertyMapping<>(HologramPropertyContainer.class, entityDataAccessor, transformer, defaultValue);
        this.propertyMappingMasks.put(mapping, properties);
    }

    @SuppressWarnings("unchecked")
    private <T> SynchedEntityData.DataValue<?> createDataValue(HologramProperty<T> property, Object value) {
        HologramPropertyMapping<?, ?> mapping = this.propertyMappings.get(property);
        if (mapping == null)
            throw new IllegalArgumentException("Unknown property " + property.getName() + "!");

        if (value != null && !mapping.inputPropertyType().isAssignableFrom(value.getClass()))
            throw new IllegalArgumentException("Value type " + value.getClass().getName() + " does not match property type " + mapping.inputPropertyType() + "!");

        return ((HologramPropertyMapping<T, ?>) mapping).createDataValue((T) value);
    }

    @SuppressWarnings("unchecked")
    private SynchedEntityData.DataValue<?> createMaskDataValue(HologramPropertyMapping<?, ?> mapping, HologramPropertyContainer properties) {
        return ((HologramPropertyMapping<HologramPropertyContainer, ?>) mapping).createDataValue(properties);
    }

    public List<SynchedEntityData.DataValue<?>> createFreshDataValues(HologramPropertyContainer properties) {
        return this.createDataValues(properties, true);
    }

    public List<SynchedEntityData.DataValue<?>> createDataValues(HologramPropertyContainer properties) {
        return this.createDataValues(properties, false);
    }

    private List<SynchedEntityData.DataValue<?>> createDataValues(HologramPropertyContainer properties, boolean fresh) {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();

        HologramPropertyContainer actingProperties;
        if (fresh) {
            actingProperties = properties;
        } else {
            actingProperties = properties.getDirty();
        }

        // Create data values for normal properties
        for (var entry : actingProperties.entrySet()) {
            HologramProperty<?> property = entry.getKey();
            if (property instanceof MappedHologramProperty<?> && !this.maskedProperties.contains(property))
                dataValues.add(this.createDataValue(property, entry.getValue()));
        }

        // Create data values for masked properties
        this.propertyMappingMasks.forEach((mapping, maskProperties) -> {
            // Use all properties to create the mask, unmodified values still need to be present to build the full mask
            if (maskProperties.stream().anyMatch(actingProperties::has))
                dataValues.add(this.createMaskDataValue(mapping, properties));
        });

        return dataValues;
    }

    public boolean isAvailable(HologramProperty<?> property) {
        return this.propertyMappings.containsKey(property) || this.maskedProperties.contains(property);
    }

}
