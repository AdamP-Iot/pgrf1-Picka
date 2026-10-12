package graphics.rasterizer;

import graphics.Raster;
import model.Line;

public class MidpointLineRasterizer implements LineRasterizer{
    private final Raster<Integer> raster;

    public MidpointLineRasterizer(Raster<Integer> raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(Line line) {

    }
}
