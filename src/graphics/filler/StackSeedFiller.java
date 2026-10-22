package graphics.filler;

import graphics.Raster;

import java.util.ArrayDeque;
import java.util.Deque;

public class StackSeedFiller implements SeedFiller {
    public final Raster raster;

    public StackSeedFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public boolean fill(int x1, int y1, int targetColor, int fillColor) {
        if(targetColor == fillColor || raster.getPixel(x1,y1) == null || raster.getPixel(x1,y1) != targetColor) {
            return false;
        }

        int width = raster.getWidth();
        Deque<Integer> pixels = new ArrayDeque<>();
        enqueue(x1,y1, width, targetColor, fillColor, pixels);

        while(!pixels.isEmpty()) {
            int index = pixels.removeLast();
            int x = index % width;
            int y = index / width;
            enqueue(x-1, y, width, targetColor, fillColor, pixels);
            enqueue(x+1, y, width, targetColor, fillColor, pixels);
            enqueue(x, y-1, width, targetColor, fillColor, pixels);
            enqueue(x, y+1, width, targetColor, fillColor, pixels);
        }
        return true;
    }
    private void enqueue(int x, int y, int width, int targetColor, int fillColor, Deque<Integer> pixels) {
        Integer color = raster.getPixel(x,y);
        if(color != null && color == targetColor) {
            raster.setPixel(x,y,fillColor);
            pixels.addLast(y * width + x);
        }
    }
}
