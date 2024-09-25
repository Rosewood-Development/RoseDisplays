package dev.rosewood.rosedisplays.datatype;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramGroup;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.UnloadedHologramGroup;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosedisplays.hologram.view.HologramPropertyView;
import dev.rosewood.rosedisplays.model.ChunkLocation;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.util.ItemSerializer;
import dev.rosewood.rosegarden.RosePlugin;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class CustomPersistentDataType {

    public static final NamespacedKey HOLOGRAM_KEY = KeyHelper.get("holograms");

    public static final PersistentDataType<PersistentDataContainer, UnloadedHologramGroup> UNLOADED_HOLOGRAM_GROUP = new PersistentDataType<>() {

        private static final NamespacedKey KEY_NAME = KeyHelper.get("name");
        private static final NamespacedKey KEY_CHUNK_LOCATION = KeyHelper.get("chunk_location");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<UnloadedHologramGroup> getComplexType() { return UnloadedHologramGroup.class; }

        @Override
        public PersistentDataContainer toPrimitive(UnloadedHologramGroup unloadedHologramGroup, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_NAME, PersistentDataType.STRING, unloadedHologramGroup.name());
            container.set(KEY_CHUNK_LOCATION, CHUNK_LOCATION, unloadedHologramGroup.chunkLocation());
            return container;
        }

        @Override
        public UnloadedHologramGroup fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String name = container.get(KEY_NAME, PersistentDataType.STRING);
            ChunkLocation chunkLocation = container.get(KEY_CHUNK_LOCATION, CHUNK_LOCATION);
            if (name == null || chunkLocation == null)
                throw new IllegalArgumentException("Invalid UnloadedHologramGroup");
            return new UnloadedHologramGroup(name, chunkLocation);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, ChunkLocation> CHUNK_LOCATION = new PersistentDataType<>() {

        private static final NamespacedKey KEY_WORLD = KeyHelper.get("world");
        private static final NamespacedKey KEY_X = KeyHelper.get("x");
        private static final NamespacedKey KEY_Z = KeyHelper.get("z");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<ChunkLocation> getComplexType() { return ChunkLocation.class; }

        @Override
        public PersistentDataContainer toPrimitive(ChunkLocation chunkLocation, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_WORLD, PersistentDataType.STRING, chunkLocation.world());
            container.set(KEY_X, PersistentDataType.INTEGER, chunkLocation.x());
            container.set(KEY_Z, PersistentDataType.INTEGER, chunkLocation.z());
            return container;
        }

        @Override
        public ChunkLocation fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String world = container.get(KEY_WORLD, PersistentDataType.STRING);
            Integer x = container.get(KEY_X, PersistentDataType.INTEGER);
            Integer z = container.get(KEY_Z, PersistentDataType.INTEGER);
            if (world == null || x == null || z == null)
                throw new IllegalArgumentException("Invalid ChunkLocation");
            return new ChunkLocation(world, x, z);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, Location> LOCATION = new PersistentDataType<>() {

        private static final NamespacedKey KEY_WORLD = KeyHelper.get("world");
        private static final NamespacedKey KEY_X = KeyHelper.get("x");
        private static final NamespacedKey KEY_Y = KeyHelper.get("y");
        private static final NamespacedKey KEY_Z = KeyHelper.get("z");
        private static final NamespacedKey KEY_YAW = KeyHelper.get("yaw");
        private static final NamespacedKey KEY_PITCH = KeyHelper.get("pitch");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<Location> getComplexType() { return Location.class; }

        @Override
        public PersistentDataContainer toPrimitive(Location location, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_WORLD, PersistentDataType.STRING, location.getWorld().getName());
            container.set(KEY_X, PersistentDataType.DOUBLE, location.getX());
            container.set(KEY_Y, PersistentDataType.DOUBLE, location.getY());
            container.set(KEY_Z, PersistentDataType.DOUBLE, location.getZ());
            container.set(KEY_YAW, PersistentDataType.FLOAT, location.getYaw());
            container.set(KEY_PITCH, PersistentDataType.FLOAT, location.getPitch());
            return container;
        }

        @Override
        public Location fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String worldName = container.get(KEY_WORLD, PersistentDataType.STRING);
            Double x = container.get(KEY_X, PersistentDataType.DOUBLE);
            Double y = container.get(KEY_Y, PersistentDataType.DOUBLE);
            Double z = container.get(KEY_Z, PersistentDataType.DOUBLE);
            Float yaw = container.get(KEY_YAW, PersistentDataType.FLOAT);
            Float pitch = container.get(KEY_PITCH, PersistentDataType.FLOAT);
            if (worldName == null || x == null || y == null || z == null || yaw == null || pitch == null)
                throw new IllegalArgumentException("Invalid Location");
            World world = Bukkit.getWorld(worldName);
            if (world == null)
                throw new IllegalArgumentException("Invalid Location, world is not loaded");
            return new Location(world, x, y, z, yaw, pitch);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, Vector3> VECTOR3 = new PersistentDataType<>() {

        private static final NamespacedKey KEY_X = KeyHelper.get("x");
        private static final NamespacedKey KEY_Y = KeyHelper.get("y");
        private static final NamespacedKey KEY_Z = KeyHelper.get("z");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<Vector3> getComplexType() { return Vector3.class; }

        @Override
        public PersistentDataContainer toPrimitive(Vector3 vector, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_X, PersistentDataType.FLOAT, vector.x());
            container.set(KEY_Y, PersistentDataType.FLOAT, vector.y());
            container.set(KEY_Z, PersistentDataType.FLOAT, vector.z());
            return container;
        }

        @Override
        public Vector3 fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            Float x = container.get(KEY_X, PersistentDataType.FLOAT);
            Float y = container.get(KEY_Y, PersistentDataType.FLOAT);
            Float z = container.get(KEY_Z, PersistentDataType.FLOAT);
            if (x == null || y == null || z == null)
                throw new IllegalArgumentException("Invalid Vector3");
            return new Vector3(x, y, z);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, Quaternion> QUATERNION = new PersistentDataType<>() {

        private static final NamespacedKey KEY_X = KeyHelper.get("x");
        private static final NamespacedKey KEY_Y = KeyHelper.get("y");
        private static final NamespacedKey KEY_Z = KeyHelper.get("z");
        private static final NamespacedKey KEY_W = KeyHelper.get("w");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<Quaternion> getComplexType() { return Quaternion.class; }

        @Override
        public PersistentDataContainer toPrimitive(Quaternion quaternion, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_X, PersistentDataType.FLOAT, quaternion.x());
            container.set(KEY_Y, PersistentDataType.FLOAT, quaternion.y());
            container.set(KEY_Z, PersistentDataType.FLOAT, quaternion.z());
            container.set(KEY_W, PersistentDataType.FLOAT, quaternion.w());
            return container;
        }

        @Override
        public Quaternion fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            Float x = container.get(KEY_X, PersistentDataType.FLOAT);
            Float y = container.get(KEY_Y, PersistentDataType.FLOAT);
            Float z = container.get(KEY_Z, PersistentDataType.FLOAT);
            Float w = container.get(KEY_W, PersistentDataType.FLOAT);
            if (x == null || y == null || z == null || w == null)
                throw new IllegalArgumentException("Invalid Quaternion");
            return new Quaternion(x, y, z, w);
        }

    };

    public static final PersistentDataType<Integer, Color> JAVA_COLOR = new PersistentDataType<>() {

        public Class<Integer> getPrimitiveType() { return Integer.class; }
        public Class<Color> getComplexType() { return Color.class; }

        @Override
        public Integer toPrimitive(Color color, PersistentDataAdapterContext context) {
            return color.getRGB();
        }

        @Override
        public Color fromPrimitive(Integer primitive, PersistentDataAdapterContext context) {
            return new Color(primitive);
        }

    };

    public static final PersistentDataType<String, BlockData> BLOCK_DATA = new PersistentDataType<>() {

        public Class<String> getPrimitiveType() { return String.class; }
        public Class<BlockData> getComplexType() { return BlockData.class; }

        @Override
        public String toPrimitive(BlockData blockData, PersistentDataAdapterContext context) {
            return blockData.getAsString();
        }

        @Override
        public BlockData fromPrimitive(String primitive, PersistentDataAdapterContext context) {
            return Bukkit.getServer().createBlockData(primitive);
        }

    };

    public static final PersistentDataType<byte[], ItemStack> ITEMSTACK = new PersistentDataType<>() {

        public Class<byte[]> getPrimitiveType() { return byte[].class; }
        public Class<ItemStack> getComplexType() { return ItemStack.class; }

        @Override
        public byte[] toPrimitive(ItemStack itemStack, PersistentDataAdapterContext context) {
            return ItemSerializer.toByteArray(itemStack);
        }

        @Override
        public ItemStack fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
            return ItemSerializer.fromByteArray(primitive);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, HologramPropertyView> HOLOGRAM_PROPERTY_CONTAINER = new PersistentDataType<>() {

        private static final NamespacedKey KEY_TAG = KeyHelper.get("tag");
        private static final NamespacedKey KEY_SIZE = KeyHelper.get("size");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<HologramPropertyView> getComplexType() { return HologramPropertyView.class; }

        @Override
        public PersistentDataContainer toPrimitive(HologramPropertyView properties, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_TAG, PersistentDataType.STRING, properties.getTag().getName());
            Set<HologramProperty<?>> propertiesSet = properties.getProperties();
            container.set(KEY_SIZE, PersistentDataType.INTEGER, propertiesSet.size());
            int i = 0;
            for (HologramProperty<?> property : propertiesSet) {
                int index = i++;
                NamespacedKey keyKey = this.getKeyKey(index);
                NamespacedKey valueKey = this.getValueKey(index);
                container.set(keyKey, PersistentDataType.STRING, property.getName());
                this.forceSet(container, valueKey, property, properties.get(property));
            }
            return container;
        }

        @Override
        public HologramPropertyView fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String tagString = container.get(KEY_TAG, PersistentDataType.STRING);
            Integer size = container.get(KEY_SIZE, PersistentDataType.INTEGER);
            if (tagString == null || size == null)
                throw new IllegalArgumentException("Invalid HologramPropertyContainer");
            HologramPropertyTag tag = HologramPropertyTag.getRegistry().get(tagString);
            if (tag == null)
                throw new IllegalArgumentException("Invalid HologramPropertyContainer, unknown HologramPropertyTag");
            HologramPropertyView properties = new DirtyingHologramPropertyView(tag);
            for (int i = 0; i < size; i++) {
                NamespacedKey keyKey = KeyHelper.get("key-" + i);
                NamespacedKey valueKey = KeyHelper.get("value-" + i);
                String hologramPropertyName = container.get(keyKey, PersistentDataType.STRING);
                HologramProperty<?> hologramProperty = HologramProperties.valueOf(hologramPropertyName);
                Object hologramPropertyValue = container.get(valueKey, hologramProperty.getPersistentDataType());
                this.forceSet(properties, hologramProperty, hologramPropertyValue);
            }
            return properties;
        }

        @SuppressWarnings("unchecked")
        private <T> void forceSet(PersistentDataContainer container, NamespacedKey key, HologramProperty<T> property, Object value) {
            container.set(key, property.getPersistentDataType(), (T) value);
        }

        @SuppressWarnings("unchecked")
        private <T> void forceSet(HologramPropertyView properties, HologramProperty<T> property, Object value) {
            properties.set(property, (T) value);
        }

        private NamespacedKey getKeyKey(int index) {
            return KeyHelper.get("key-" + index);
        }

        private NamespacedKey getValueKey(int index) {
            return KeyHelper.get("value-" + index);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, Hologram> HOLOGRAM = new PersistentDataType<>() {

        private static final NamespacedKey KEY_TYPE = KeyHelper.get("type");
        private static final NamespacedKey KEY_PROPERTIES = KeyHelper.get("properties");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<Hologram> getComplexType() { return Hologram.class; }

        @Override
        public PersistentDataContainer toPrimitive(Hologram hologram, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_TYPE, PersistentDataType.STRING, hologram.getType().name());
            container.set(KEY_PROPERTIES, HOLOGRAM_PROPERTY_CONTAINER, hologram.getProperties());
            hologram.writeAdditionalPDCData(container, context);
            return container;
        }

        @Override
        public Hologram fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String type = container.get(KEY_TYPE, PersistentDataType.STRING);
            HologramPropertyView properties = container.get(KEY_PROPERTIES, HOLOGRAM_PROPERTY_CONTAINER);
            if (type == null || !(properties instanceof DirtyingHologramPropertyView dirtyingView))
                throw new IllegalArgumentException("Invalid Hologram");
            HologramType hologramType = HologramType.getRegistry().get(type);
            if (hologramType == null)
                throw new IllegalArgumentException("Invalid Hologram, HologramType " + type + " not found");
            return hologramType.deserialize(hologramType, dirtyingView, container, context);
        }

    };

    public static final PersistentDataType<PersistentDataContainer, HologramGroup> HOLOGRAM_GROUP = new PersistentDataType<>() {

        private static final NamespacedKey KEY_NAME = KeyHelper.get("name");
        private static final NamespacedKey KEY_ORIGIN = KeyHelper.get("origin");
        private static final NamespacedKey KEY_PROPERTIES = KeyHelper.get("properties");
        private static final NamespacedKey KEY_HOLOGRAMS = KeyHelper.get("holograms");

        public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
        public Class<HologramGroup> getComplexType() { return HologramGroup.class; }

        @Override
        public PersistentDataContainer toPrimitive(HologramGroup hologramGroup, PersistentDataAdapterContext context) {
            PersistentDataContainer container = context.newPersistentDataContainer();
            container.set(KEY_NAME, PersistentDataType.STRING, hologramGroup.getName());
            container.set(KEY_ORIGIN, LOCATION, hologramGroup.getOrigin());
            container.set(KEY_PROPERTIES, HOLOGRAM_PROPERTY_CONTAINER, hologramGroup.getGroupProperties());
            container.set(KEY_HOLOGRAMS, forList(HOLOGRAM), hologramGroup.getHolograms());
            return container;
        }

        @Override
        public HologramGroup fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
            String name = container.get(KEY_NAME, PersistentDataType.STRING);
            Location origin = container.get(KEY_ORIGIN, LOCATION);
            HologramPropertyView properties = container.get(KEY_PROPERTIES, HOLOGRAM_PROPERTY_CONTAINER);
            List<Hologram> holograms = container.get(KEY_HOLOGRAMS, forList(HOLOGRAM));
            if (name == null || origin == null || holograms == null)
                throw new IllegalArgumentException("Invalid HologramGroup");
            DirtyingHologramPropertyView dirtyingProperties;
            if (properties instanceof DirtyingHologramPropertyView view) {
                dirtyingProperties = view;
            } else {
                dirtyingProperties = new DirtyingHologramPropertyView(HologramPropertyTag.GROUP);
            }
            return new HologramGroup(name, origin, holograms, dirtyingProperties);
        }

    };

    public static <T extends Enum<T>> PersistentDataType<String, T> forEnum(Class<T> enumClass) {
        return new PersistentDataType<>() {

            public Class<String> getPrimitiveType() { return String.class; }
            public Class<T> getComplexType() { return enumClass; }

            @Override
            public String toPrimitive(T enumValue, PersistentDataAdapterContext context) {
                return enumValue.name();
            }

            @Override
            public T fromPrimitive(String primitive, PersistentDataAdapterContext context) {
                return Enum.valueOf(enumClass, primitive);
            }

        };
    }

    @SuppressWarnings("unchecked")
    public static <T> PersistentDataType<PersistentDataContainer, T[]> forArray(PersistentDataType<?, T> arrayElementDataType) {
        return new PersistentDataType<>() {

            private static final NamespacedKey KEY_SIZE = KeyHelper.get("size");

            public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
            public Class<T[]> getComplexType() { return (Class<T[]>) arrayElementDataType.getComplexType().arrayType(); }

            @Override
            public PersistentDataContainer toPrimitive(T[] array, PersistentDataAdapterContext context) {
                PersistentDataContainer container = context.newPersistentDataContainer();
                container.set(KEY_SIZE, PersistentDataType.INTEGER, array.length);
                for (int i = 0; i < array.length; i++) {
                    T element = array[i];
                    if (element == null)
                        continue;
                    NamespacedKey elementKey = KeyHelper.get(String.valueOf(i));
                    container.set(elementKey, arrayElementDataType, element);
                }
                return container;
            }

            @Override
            public T[] fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
                Integer size = container.get(KEY_SIZE, PersistentDataType.INTEGER);
                if (size == null)
                    throw new IllegalArgumentException("Invalid " + arrayElementDataType.getComplexType().getSimpleName() + "[]");
                T[] array = (T[]) Array.newInstance(arrayElementDataType.getComplexType(), size);
                for (int i = 0; i < size; i++) {
                    NamespacedKey elementKey = KeyHelper.get(String.valueOf(i));
                    array[i] = container.get(elementKey, arrayElementDataType);
                }
                return array;
            }

        };
    }

    @SuppressWarnings("unchecked")
    public static <T> PersistentDataType<PersistentDataContainer, List<T>> forList(PersistentDataType<?, T> listElementDataType) {
        return new PersistentDataType<>() {

            private static final NamespacedKey KEY_SIZE = KeyHelper.get("size");

            public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
            public Class<List<T>> getComplexType() { return (Class<List<T>>) (Class<?>) List.class; } // Lists types aren't real they can't hurt you

            @Override
            public PersistentDataContainer toPrimitive(List<T> list, PersistentDataAdapterContext context) {
                PersistentDataContainer container = context.newPersistentDataContainer();
                container.set(KEY_SIZE, PersistentDataType.INTEGER, list.size());
                for (int i = 0; i < list.size(); i++) {
                    T element = list.get(i);
                    if (element == null)
                        continue;
                    NamespacedKey elementKey = KeyHelper.get(String.valueOf(i));
                    container.set(elementKey, listElementDataType, element);
                }
                return container;
            }

            @Override
            public List<T> fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
                Integer size = container.get(KEY_SIZE, PersistentDataType.INTEGER);
                if (size == null)
                    throw new IllegalArgumentException("Invalid List<" + listElementDataType.getComplexType().getSimpleName() + ">");
                List<T> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    NamespacedKey elementKey = KeyHelper.get(String.valueOf(i));
                    list.add(container.get(elementKey, listElementDataType));
                }
                return list;
            }

        };
    }

    private static class KeyHelper {

        private static final RosePlugin PLUGIN = RoseDisplays.getInstance();
        private static final Map<String, NamespacedKey> CACHE = new HashMap<>();

        public static NamespacedKey get(String key) {
            return CACHE.computeIfAbsent(key, x -> new NamespacedKey(PLUGIN, x));
        }

    }

}
