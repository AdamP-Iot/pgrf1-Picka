package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class TrivialLineRasterizer implements LineRasterizer {
    private final Raster<Integer> raster;


    public TrivialLineRasterizer(Raster<Integer> raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {
        rasterize(line.getPoint1().getX(),
                line.getPoint2().getX(),
                line.getPoint1().getY(),
                line.getPoint2().getY(),
                line.getColor());

    }

    /**
     * Trivální algoritmus
     *
     * @param x1    x souřadnice prvního bodu
     * @param x2    x souřadnice druhého bodu
     * @param y1    y souřadnice prvního bodu
     * @param y2    y souřadnice druhého bodu
     * @param color barva vykreslované úsečky
     */
    private void rasterize(int x1, int x2, int y1, int y2, int color) {
        int dominantX = Math.abs(x2 - x1);
        int dominantY = Math.abs(y2 - y1);
        if (x1 == x2) {
            if (y2 < y1) {
                int pomY = y1;
                y1 = y2;
                y2 = pomY;
            }
            for (int y = y1; y <= y2; y++) {
                raster.setPixel(x1, y, color);
            }
            return;
        }
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;
        if (dominantX >= dominantY) {
            if (x2 < x1) {
                int pomX = x1;
                x1 = x2;
                x2 = pomX;
            }
            for (int x = x1; x <= x2; x++) {
                float y = k * x + q;
                raster.setPixel(x, Math.round(y), color);
            }
        } else {
            if (y2 < y1) {
                int pomY = y1;
                y1 = y2;
                y2 = pomY;
            }
            for (int y = y1; y <= y2; y++) {
                float x = (y - q) / k;
                raster.setPixel(Math.round(x), y, color);
            }
        }
    }
}
