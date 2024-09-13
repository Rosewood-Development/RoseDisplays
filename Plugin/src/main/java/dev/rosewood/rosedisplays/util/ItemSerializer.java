package dev.rosewood.rosedisplays.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class ItemSerializer {

    private ItemSerializer() {

    }

    /**
     * Gets one {@link ItemStack} from a byte array.
     *
     * @param data Byte array to convert to {@link ItemStack}.
     * @return {@link ItemStack} created from the byte array.
     */
    public static ItemStack fromByteArray(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load item", e);
        }
    }

    /**
     * A method to serialize one {@link ItemStack} to a byte array.
     *
     * @param item to turn into a byte array.
     * @return byte array of the item data.
     */
    public static byte[] toByteArray(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save item", e);
        }
    }

}
