package dev.rosewood.rosedisplays.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;
import net.minecraft.server.v1_16_R3.ChatBaseComponent;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatHexColor;
import net.minecraft.server.v1_16_R3.ChatModifier;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;

public final class ImageUtil {

    public static final String BLOCK = "\u2588";
    public static final String SPACE = "\u3000";

    private ImageUtil() {

    }

    public static BufferedImage resize(BufferedImage image, int width, int height) throws IOException{
        return Thumbnails.of(image).size(width, height).asBufferedImage();
    }

    public static int[] getBufferedImageAsColorValues(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    public static IChatBaseComponent getDataAsChatComponent(int[] data) {
        ChatBaseComponent component = new ChatComponentText("");
        ChatComponentText lastComponent = null;
        int lastColor = 0xFFFFFF;
        for (int color : data) {
            lastComponent = appendComponent(component, color, lastColor, lastComponent);
            lastColor = color;
        }

        return component;
    }

    private static ChatComponentText appendComponent(ChatBaseComponent parent, int color, int lastColor, ChatComponentText lastComponent) {
        final ChatComponentText component;

        // Handle transparency
        int alpha = color >>> 24;
        if (alpha <= 127) {
            component = new ChatComponentText(SPACE);
            component.setChatModifier(ChatModifier.a.setColor(EnumChatFormat.BLACK));
            parent.addSibling(component);
            return component;
        } else {
            color &= 0x00FFFFFF;
        }

        if (lastComponent != null && color == lastColor) {
            // Add the character to the last component (with the same color)
            component = new ChatComponentText(lastComponent.g() + BLOCK);
            component.setChatModifier(lastComponent.getChatModifier());

            // Replace old component
            parent.getSiblings().set(parent.getSiblings().size() - 1, component);
        } else {
            // Add a new component
            component = new ChatComponentText(BLOCK);
            component.setChatModifier(ChatModifier.a.setColor(ChatHexColor.a(color)));
            parent.addSibling(component);
        }
        return component;
    }

}
