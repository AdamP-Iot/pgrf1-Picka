package graphics;

import java.awt.*;

public interface Raster<P> {
    int getWidth();
    int getHeight();

    void clear(P value);
    void present(Graphics graphics);
    void setPixel(int x, int y, P value);
}
