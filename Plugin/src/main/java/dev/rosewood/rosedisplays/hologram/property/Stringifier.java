package dev.rosewood.rosedisplays.hologram.property;

import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosedisplays.model.ItemDisplayType;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.TextDisplayAlignment;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosedisplays.util.ItemSerializer;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public abstract class Stringifier<T> {

    private static final Map<Class<?>, Stringifier<?>> REGISTRY = new HashMap<>();

    public static final Stringifier<String> STRING = create(String.class, Function.identity(), Function.identity());
    public static final Stringifier<Integer> INTEGER = create(Integer.class, x -> Integer.toString(x), Integer::parseInt);
    public static final Stringifier<Float> FLOAT = create(Float.class, x -> Float.toString(x), Float::parseFloat);
    public static final Stringifier<Boolean> BOOLEAN = create(Boolean.class, x -> Boolean.toString(x), Boolean::parseBoolean);
    public static final Stringifier<Vector3> VECTOR3 = create(Vector3.class, x -> Vector3.toString(x), Vector3::parseVector3);
    public static final Stringifier<Quaternion> QUATERNION = create(Quaternion.class, x -> Quaternion.toString(x), Quaternion::parseQuaternion);
    public static final Stringifier<Color> COLOR = create(Color.class, x -> String.format("#%02x%02x%02x", x.getRed(), x.getGreen(), x.getBlue()), Color::decode);
    public static final Stringifier<ItemStack> ITEM_STACK = create(ItemStack.class, ItemSerializer::toBase64, ItemSerializer::fromBase64);
    public static final Stringifier<BlockData> BLOCK_DATA = create(BlockData.class, BlockData::getAsString, x -> Bukkit.getServer().createBlockData(x));
    public static final Stringifier<BillboardConstraint> BILLBOARD_CONSTRAINT = createEnum(BillboardConstraint.class);
    public static final Stringifier<ItemDisplayType> ITEM_DISPLAY_TYPE = createEnum(ItemDisplayType.class);
    public static final Stringifier<TextDisplayAlignment> TEXT_DISPLAY_ALIGNMENT = createEnum(TextDisplayAlignment.class);

    @SuppressWarnings("unchecked")
    public static <T> Stringifier<T> get(Class<T> type) {
        // Try to find an exact match first
        Stringifier<T> stringifier = (Stringifier<T>) REGISTRY.get(type);
        if (stringifier == null) {
            // No match? Try to find a stringifier the type is an instance of
            for (Class<?> stringifierType : REGISTRY.keySet()) {
                if (stringifierType.isAssignableFrom(type)) {
                    stringifier = (Stringifier<T>) REGISTRY.get(stringifierType);
                    REGISTRY.put(type, stringifier); // Cache for faster lookup
                    break;
                }
            }
        }

        if (stringifier == null)
            throw new IllegalArgumentException("No Stringifier found for type " + type.getName());

        return stringifier;
    }

    @SuppressWarnings("unchecked")
    public static <T> String stringify(T object) {
        Stringifier<T> stringifier = (Stringifier<T>) get(object.getClass());
        return stringifier.toString(object);
    }

    public static <T> T unstringify(Class<T> type, String string) {
        Stringifier<T> stringifier = get(type);
        return stringifier.fromString(string);
    }

    private static <T extends Enum<T>> Stringifier<T> createEnum(Class<T> enumClass) {
        return create(enumClass, Enum::name, name -> Enum.valueOf(enumClass, name));
    }

    private static <T> Stringifier<T> create(Class<T> type, Function<T, String> stringify, Function<String, T> unstringify) {
        Stringifier<T> stringifier = new Stringifier<>() {
            public String toString(T object) { return stringify.apply(object); }
            public T fromString(String string) { return unstringify.apply(string); }
        };
        REGISTRY.put(type, stringifier);
        return stringifier;
    }

    private Stringifier() {

    }

    /**
     * Turns an object into a string.
     *
     * @param object The object to turn into a string.
     * @return The string representation of the object.
     */
    public abstract String toString(T object);

    /**
     * Turns a string into an object.
     *
     * @param string The string to turn into an object.
     * @return The object representation of the string.
     */
    public abstract T fromString(String string);

}
