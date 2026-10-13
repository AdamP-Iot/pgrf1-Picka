package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class BresenhamLineRasterizer implements LineRasterizer {
    private final Raster<Integer> raster;

    public BresenhamLineRasterizer(Raster<Integer> raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {
        rasterize(
                line.getPoint1().getX(),
                line.getPoint2().getX(),
                line.getPoint1().getY(),
                line.getPoint2().getY(),
                line.getColor()
        );
    }
    /**
     * Bresenhemův algoritmus
     *
     * @param x1 x souřadnice prvního bodu
     * @param x2 x souřadnice druhého bodu
     * @param y1 y souřadnice prvního bodu
     * @param y2 y souřadnice druhého bodu
     * @param color barva vykreslované úsečky
     */
    private void rasterize(int x1, int x2, int y1, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int stepX = x1 < x2 ? 1 : -1;
        int stepY = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) {

            int p = 2 * dy - dx;

            for (int i = 0; i <= dx; i++) {

                raster.setPixel(x, y, color);

                x += stepX;

                if (p >= 0) {
                    y += stepY;
                    p += 2 * (dy - dx);
                } else {
                    p += 2 * dy;
                }
            }

        } else {

            int p = 2 * dx - dy;

            for (int i = 0; i <= dy; i++) {

                raster.setPixel(x, y, color);

                y += stepY;

                if (p >= 0) {
                    x += stepX;
                    p += 2 * (dx - dy);
                } else {
                    p += 2 * dx;
                }
            }
        }
    }
}
