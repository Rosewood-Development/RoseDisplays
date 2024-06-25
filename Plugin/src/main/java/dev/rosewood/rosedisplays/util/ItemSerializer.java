package dev.rosewood.rosedisplays.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public final class ItemSerializer {

    private ItemSerializer() {

    }

    /**
     * Gets one {@link ItemStack} from Base64 string.
     *
     * @param data Base64 string to convert to {@link ItemStack}.
     * @return {@link ItemStack} created from the Base64 string.
     */
    public static ItemStack fromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to decode class type.", e);
        }
    }

    /**
     * A method to serialize one {@link ItemStack} to Base64 String.
     *
     * @param item to turn into a Base64 String.
     * @return Base64 string of the item.
     */
    public static String toBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

}
