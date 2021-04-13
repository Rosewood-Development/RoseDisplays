package dev.rosewood.rosedisplays.util;

import java.awt.Color;

public class BlockHighlight {

    private final int x, y, z, color, time;
    private final String text;

    public BlockHighlight(int x, int y, int z, int color, String text, int time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.text = text;
        this.time = time;
    }

    public static BlockHighlight getHideBehindBlocks(int time) {
        return new BlockHighlight(0, 0, 0, new Color(0, 0, 0, 0).getRGB(), " ", time);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getColor() {
        return this.color;
    }

    public String getText() {
        return this.text;
    }

    public int getTime() {
        return this.time;
    }

}
