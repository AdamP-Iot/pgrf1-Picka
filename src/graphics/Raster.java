package graphics;

import java.awt.*;

public interface Raster<P> {
    int getWidth();

    int getHeight();

    void clear(P value);

    void present(Graphics graphics);

    Integer getPixel(int x, int y);
    void setPixel(int x, int y, Integer value);
}
