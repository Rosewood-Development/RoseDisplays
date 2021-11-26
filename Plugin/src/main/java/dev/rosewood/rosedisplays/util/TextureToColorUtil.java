package dev.rosewood.rosedisplays.util;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosegarden.RosePlugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.bukkit.Material;

public final class TextureToColorUtil {

    public final static Int2ObjectOpenHashMap<Material> COLOR_TO_TEXTURE_MAP = new Int2ObjectOpenHashMap<>();
    // TODO: Make this an expiring cache somehow, this is going to cause some big memory issues down the line
    private final static Int2ObjectOpenHashMap<Material> COLOR_TO_TEXTURE_CACHE = new Int2ObjectOpenHashMap<>();

    private TextureToColorUtil() {

    }

    public static Material getClosestMaterial(int color) {
        if (COLOR_TO_TEXTURE_MAP.isEmpty())
            populateTextureToColorMap();

        if (COLOR_TO_TEXTURE_CACHE.containsKey(color))
            return COLOR_TO_TEXTURE_CACHE.get(color);

        int targetR = color >> 16 & 0xFF;
        int targetG = color >> 8 & 0xFF;
        int targetB = color & 0xFF;

        int minDistance = Integer.MAX_VALUE;
        Material material = null;
        for (int key : COLOR_TO_TEXTURE_MAP.keySet()) {
            int keyR = key >> 16 & 0xFF;
            int keyG = key >> 8 & 0xFF;
            int keyB = key & 0xFF;

            int r = keyR - targetR;
            int g = keyG - targetG;
            int b = keyB - targetB;
            int distance = r * r + g * g + b * b;
            if (distance < minDistance) {
                minDistance = distance;
                material = COLOR_TO_TEXTURE_MAP.get(key);
            }
        }

        COLOR_TO_TEXTURE_CACHE.put(color, material);
        return material;
    }

    public static void clearCache() {
        COLOR_TO_TEXTURE_MAP.clear();
        COLOR_TO_TEXTURE_CACHE.clear();
    }

    private static void populateTextureToColorMap() {
        RosePlugin rosePlugin = RoseDisplays.getInstance();
        File texturesDirectory = new File(rosePlugin.getDataFolder(), "block_textures");
        if (!texturesDirectory.exists())
            texturesDirectory.mkdirs();

        File[] files = texturesDirectory.listFiles();
        if (files == null)
            return;

        List<File> textureFiles = new ArrayList<>();
        for (File file : files)
            if (file.isFile() && file.getName().endsWith("png"))
                textureFiles.add(file);

        for (Material material : Material.values()) {
            List<File> textures = new ArrayList<>();
            String prefix = material.name().toLowerCase();
            for (File file : textureFiles) {
                String textureName = getFileName(file);
                if (textureName.startsWith(prefix))
                    textures.add(file);
            }

            if (!textures.isEmpty())
                processTextures(material, textures);
        }
    }

    private static void processTextures(Material material, List<File> textures) {
        if (material.isTransparent() || !material.isBlock())
            return;

        if (material.name().toLowerCase().contains("coral") && !material.name().toLowerCase().contains("block"))
            return;

        if (material.name().toLowerCase().contains("glass"))
            return;

        switch (material) {
            case COBWEB:
            case LIGHT:
            case TURTLE_EGG:
            case LIGHTNING_ROD:
            case SCAFFOLDING:
            case SPAWNER:
            case END_PORTAL_FRAME:
            case BAMBOO:
            case CAMPFIRE:
            case SOUL_CAMPFIRE:
            case BEACON:
            case BELL:
            case BREWING_STAND:
                return;
        }

        int averageR = 0;
        int averageG = 0;
        int averageB = 0;
        int totalColors = 0;

        outer:
        for (File file : textures) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                int[] data = ImageUtil.getBufferedImageAsColorValues(bufferedImage);
                bufferedImage.flush();

                for (int color : data) {
                    int alpha = color >>> 24;
                    if (alpha != 255) // No transparency is allowed, skip this texture entirely
                        continue outer;

                    int r = color >> 16 & 0xFF;
                    int g = color >> 8 & 0xFF;
                    int b = color & 0xFF;

                    averageR += r * r;
                    averageG += g * g;
                    averageB += b * b;
                    totalColors++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (totalColors == 0)
            return;

        int r = (int) Math.sqrt((double) averageR / totalColors);
        int g = (int) Math.sqrt((double) averageG / totalColors);
        int b = (int) Math.sqrt((double) averageB / totalColors);

        int color = (r << 16) | (g << 8) | b;
        COLOR_TO_TEXTURE_MAP.put(color, material);
    }

    private static String getFileName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

}
