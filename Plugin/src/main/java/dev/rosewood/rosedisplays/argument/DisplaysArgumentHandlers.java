package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public final class DisplaysArgumentHandlers {

    private static final RosePlugin ROSE_PLUGIN = RoseDisplays.getInstance();
    public static final ArgumentHandler<Hologram> HOLOGRAM = new HologramArgumentHandler(ROSE_PLUGIN);
    public static final ArgumentHandler<HologramLine> HOLOGRAM_LINE = new HologramLineArgumentHandler();
    public static final ArgumentHandler<HologramProperty<?>> HOLOGRAM_PROPERTY = new HologramPropertyArgumentHandler();
    public static final ArgumentHandler<Quaternion> QUATERNION = new QuaternionArgumentHandler();
    public static final ArgumentHandler<Vector3> VECTOR3 = new Vector3ArgumentHandler();
    public static final ArgumentHandler<ItemStack> ITEMSTACK = new ItemStackArgumentHandler();
    public static final ArgumentHandler<BlockData> BLOCK_DATA = new BlockDataArgumentHandler();

    public static final ArgumentHandler<Object> HOLOGRAM_PROPERTY_VALUE = new HologramPropertyValueArgumentHandler();

    private DisplaysArgumentHandlers() { }

    public static <T extends Number> ArgumentHandler<T> forIntRange(ArgumentHandler<T> delegate, int min, int max) {
        return new RangedArgumentHandler<>(delegate, min, max);
    }

    public static <T extends Number> ArgumentHandler<T> forDoubleRange(ArgumentHandler<T> delegate, double min, double max) {
        return new RangedArgumentHandler<>(delegate, min, max);
    }

}
