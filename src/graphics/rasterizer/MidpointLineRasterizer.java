package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class MidpointLineRasterizer implements LineRasterizer {
    private final Raster<Integer> raster;

    public MidpointLineRasterizer(Raster<Integer> raster) {
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
     * Midpoint algoritmus
     *
     * @param x1    x souřadnice prvního bodu
     * @param x2    x souřadnice druhého bodu
     * @param y1    y souřadnice prvního bodu
     * @param y2    y souřadnice druhého bodu
     * @param color barva vykreslované úsečky
     */
    private void rasterize(int x1, int x2, int y1, int y2, int color) {
        raster.setPixel(x1, y1, color);
        raster.setPixel(x2, y2, color);
        midPoint(x1, x2, y1, y2, color);
    }

    private void midPoint(int x1, int x2, int y1, int y2, int color) {
        int sx = (x1 + x2) / 2;
        int sy = (y1 + y2) / 2;
        raster.setPixel(sx, sy, color);
        if ((Math.abs(x1 - sx) > 1) || (Math.abs(y1 - sy) > 1)) {
            midPoint(x1, sx, y1, sy, color);
        }
        if ((Math.abs(x2 - sx) > 1) || (Math.abs(y2 - sy) > 1)) {
            midPoint(sx, x2, sy, y2, color);
        }
    }
}
