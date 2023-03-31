package dev.rosewood.rosedisplays.display;

public enum DisplayType {

    HOLOGRAM(Integer.MAX_VALUE, Integer.MAX_VALUE),
    DISPLAY_ENTITY(Integer.MAX_VALUE, Integer.MAX_VALUE),
    PARTICLE(Integer.MAX_VALUE, Integer.MAX_VALUE),
    PARTICLE_MARKER(Integer.MAX_VALUE, Integer.MAX_VALUE),
    SCOREBOARD(64, 15),
    DEBUG(Integer.MAX_VALUE, Integer.MAX_VALUE),
    CHAT(35, 20);

    private final int maxWidth, minHeight;

    DisplayType(int maxWidth, int minHeight) {
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.minHeight;
    }

}
