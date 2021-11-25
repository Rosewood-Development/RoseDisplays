package dev.rosewood.rosedisplays.data;

import dev.rosewood.rosedisplays.util.ImageUtil;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenCaptureDataSource implements DataSource {

    private final static int REFRESH_RATE = 1000 / 20;

    private Robot robot;
    private Rectangle screenBounds;
    private int width, height;

    @Override
    public void loadData(int targetWidth, int targetHeight) throws AWTException, IOException {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = environment.getScreenDevices();
        GraphicsDevice screen = screens[0];

        this.robot = new Robot(screen);
        this.screenBounds = screen.getDefaultConfiguration().getBounds();

        BufferedImage image = this.robot.createScreenCapture(this.screenBounds);
        if (image.getWidth() > targetWidth || image.getHeight() > targetHeight)
            image = ImageUtil.resize(image, targetWidth, targetHeight);

        this.width = image.getWidth();
        this.height = image.getHeight();
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
        BufferedImage image = this.robot.createScreenCapture(this.screenBounds);
        try {
            image = ImageUtil.resize(image, this.width, this.height);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ImageUtil.getBufferedImageAsColorValues(image);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public int getFrameDelay() {
        return REFRESH_RATE;
    }

    @Override
    public String getName() {
        return "Screenshare";
    }
}
