package dev.rosewood.rosedisplays.model;

public record TextDisplayProperties(boolean hasShadow,
                                    boolean isSeeThrough,
                                    boolean useDefaultBackgroundColor,
                                    TextDisplayAlignment alignment) {

    public TextDisplayProperties withShadow(boolean hasShadow) {
        return new TextDisplayProperties(hasShadow, this.isSeeThrough, this.useDefaultBackgroundColor, this.alignment);
    }

    public TextDisplayProperties withSeeThrough(boolean isSeeThrough) {
        return new TextDisplayProperties(this.hasShadow, isSeeThrough, this.useDefaultBackgroundColor, this.alignment);
    }

    public TextDisplayProperties withUseDefaultBackgroundColor(boolean useDefaultBackgroundColor) {
        return new TextDisplayProperties(this.hasShadow, this.isSeeThrough, useDefaultBackgroundColor, this.alignment);
    }

    public TextDisplayProperties withAlignment(TextDisplayAlignment alignment) {
        return new TextDisplayProperties(this.hasShadow, this.isSeeThrough, this.useDefaultBackgroundColor, alignment);
    }

}
