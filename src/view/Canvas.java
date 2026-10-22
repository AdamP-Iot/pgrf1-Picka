package view;

import graphics.Raster;
import graphics.RasterImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Represents a raster drawing surface based on a Swing {@link JPanel}.
 * The canvas stores its image data in a {@link BufferedImage} and allows
 * individual pixels to be manipulated and rendered on the screen.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Canvas extends JPanel {

    private final RasterImage raster;
    private static final Color BACKGROUND_COLOR = new Color(0x2F2F2F);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new RasterImage(width, height);
        clear();
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Rendering -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void clear() {
        raster.clear(BACKGROUND_COLOR.getRGB());
    }

    @Override
    protected void paintComponent(Graphics graphics) { //nesahat
        super.paintComponent(graphics);
        raster.present(graphics);
    }
    public Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Getters -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Raster<Integer> getRaster() {
        return raster;
    }

}
