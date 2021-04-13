package dev.rosewood.rosedisplays.data;

import dev.rosewood.rosedisplays.util.GifDecoder;
import dev.rosewood.rosedisplays.util.GifDecoder.GifImage;
import dev.rosewood.rosedisplays.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GifDataSource extends FileDataSource {

    private List<ImageFrame> frames;
    private int frameIndex = 0;
    private int width, height;

    public GifDataSource(File file) {
        super(file);
    }

    @Override
    public void loadData(int targetWidth, int targetHeight) throws IOException {
        this.frames = new ArrayList<>();

        try (InputStream data = new FileInputStream(this.file)) {
            GifImage gif = GifDecoder.read(data);
            this.width = gif.getWidth();
            this.height = gif.getHeight();
            int frameCount = gif.getFrameCount();

            for (int i = 0; i < frameCount; i++) {
                BufferedImage image = gif.getFrame(i);
                int delay = gif.getDelay(i);
                this.frames.add(new ImageFrame(image, delay));
            }

            if (this.width > targetWidth || this.height > targetHeight) {
                this.frames.forEach(x -> x.scale(targetWidth, targetHeight));
                ImageFrame frame = this.frames.get(0);
                this.width = frame.getWidth();
                this.height = frame.getHeight();
            }
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int[] nextFrame() {
        this.frameIndex = (this.frameIndex + 1) % this.frames.size();
        return this.frames.get(this.frameIndex).getColorData();
    }

    @Override
    public boolean isStatic() {
        return this.frames.size() <= 1;
    }

    @Override
    public int getFrameDelay() {
        return this.frames.get(this.frameIndex).getDelay();
    }

    private static class ImageFrame {
        private final int delay;
        private BufferedImage image;
        private int[] cachedColorData;

        public ImageFrame(BufferedImage image, int delay) {
            this.image = image;

            // Fix delay quirks (delay is normally measured in 1/100 of a second)
            if (delay == 0) {
                this.delay = 100;
            } else {
                this.delay = delay * 10;
            }
        }

        public void scale(int targetWidth, int targetHeight) {
            try {
                this.image = ImageUtil.resize(this.image, targetWidth, targetHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int[] getColorData() {
            if (this.cachedColorData == null)
                this.cachedColorData = ImageUtil.getBufferedImageAsColorValues(this.image);
            return this.cachedColorData;
        }

        public int getDelay() {
            return this.delay;
        }

        public int getWidth() {
            return this.image.getWidth();
        }

        public int getHeight() {
            return this.image.getHeight();
        }
    }

}
