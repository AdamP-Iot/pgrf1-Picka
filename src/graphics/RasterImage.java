package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterImage implements Raster<Integer> {
    private final BufferedImage image;

    public RasterImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear(Integer value) {
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(value));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
    }

    @Override
    public void present(Graphics graphics) {
        graphics.drawImage(image, 0, 0, null);
    }

    @Override
    public void setPixel(int x, int y, Integer value) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            image.setRGB(x, y, value);
        }
    }
}
