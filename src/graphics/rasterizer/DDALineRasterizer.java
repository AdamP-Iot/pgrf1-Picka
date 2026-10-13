package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class DDALineRasterizer implements LineRasterizer {
    private final Raster<Integer> raster;

    public DDALineRasterizer(Raster<Integer> raster) {
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
     * DDA algoritmus
     *
     * @param x1 x souřadnice prvního bodu
     * @param x2 x souřadnice druhého bodu
     * @param y1 y souřadnice prvního bodu
     * @param y2 y souřadnice druhého bodu
     * @param color barva vykreslované úsečky
     */
    private void rasterize (int x1, int x2, int y1, int y2, int color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if(steps == 0){
            raster.setPixel(x1,y1,color);
        }

        float x_increment = dx / (float) steps;
        float y_increment = dy / (float) steps;

        float x = x1;
        float y = y1;

        for(int i = 0; i <= steps; i++){
            raster.setPixel(Math.round(x),Math.round(y),color);
            x += x_increment;
            y += y_increment;
        }
    }
}
