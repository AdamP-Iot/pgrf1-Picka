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

    }
}
