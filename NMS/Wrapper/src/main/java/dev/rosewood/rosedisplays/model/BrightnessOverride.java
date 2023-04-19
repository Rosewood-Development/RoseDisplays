package dev.rosewood.rosedisplays.model;

public record BrightnessOverride(int blockLight,
                                 int skyLight) {

    public BrightnessOverride withBlockLight(int blockLight) {
        return new BrightnessOverride(blockLight, this.skyLight);
    }

    public BrightnessOverride withSkyLight(int skyLight) {
        return new BrightnessOverride(this.blockLight, skyLight);
    }

}
