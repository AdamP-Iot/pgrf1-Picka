package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class BresenhamLineRasterizer implements LineRasterizer{
    private final Raster<Integer> raster;

    public BresenhamLineRasterizer(Raster<Integer> raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {

    }
}
