package dev.rosewood.rosedisplays.data;

import dev.rosewood.rosedisplays.manager.ConfigurationManager.Setting;
import dev.rosewood.rosedisplays.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageDataSource extends FileDataSource {

    private BufferedImage bufferedImage;
    private int[] cachedData;

    public ImageDataSource(File file) {
        super(file);
    }

    @Override
    public void loadData(int targetWidth, int targetHeight) throws IOException {
        this.bufferedImage = ImageIO.read(this.file);
        if (this.bufferedImage.getWidth() > targetWidth || this.bufferedImage.getHeight() > targetHeight)
            this.bufferedImage = ImageUtil.resize(this.bufferedImage, targetWidth, targetHeight);

        this.cachedData = ImageUtil.getBufferedImageAsColorValues(this.bufferedImage);
    }

    @Override
    public int getWidth() {
        return this.bufferedImage.getWidth();
    }

    @Override
    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    @Override
    public int[] nextFrame() {
        return this.cachedData;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public int getFrameDelay() {
        return Setting.TIME_BETWEEN_UPDATES.getInt();
    }

}
