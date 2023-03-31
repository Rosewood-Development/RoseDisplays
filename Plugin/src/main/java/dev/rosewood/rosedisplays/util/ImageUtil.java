package dev.rosewood.rosedisplays.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public final class ImageUtil {

    public static final String BLOCK = "\u2588\uF801";
    public static final String SPACE = "\u3000\uF801";

    private ImageUtil() {

    }

    public static BufferedImage resize(BufferedImage image, int width, int height) throws IOException{
        return Thumbnails.of(image).size(width, height).asBufferedImage();
    }

    public static int[] getBufferedImageAsColorValues(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

}
