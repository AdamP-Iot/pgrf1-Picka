package graphics.filler;

import graphics.Raster;
import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class ScanLineFiller  implements PolygonFiller {
    private final Raster raster;
    public ScanLineFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(Polygon polygon, int color) {
        List<Line> edges = new ArrayList<>(polygon.getLines());
        if(edges.size() < 2){
            return;
        }
        // Uzavření polygonu spojením posledniho a prvního bodu
        Line first = edges.getFirst();
        Line last = edges.getLast();
        edges.add(new Line(last.getColor(),last.getPoint2(),first.getPoint1()));
        // Úprava orientace hrany
        int maxY = 0;
        int minY = raster.getHeight();
        for (int i = 0; i < edges.size(); i++) {
            Line edge =  edges.get(i);
            Point top = edge.getPoint1();
            Point bottom = edge.getPoint2();
            if(top.getY() > bottom.getY()) {
             Point temp = top;
             top = bottom;
             bottom = temp;
             edges.set(i, new Line(edge.getColor(),top,bottom));
            }
             minY = Math.min(minY, top.getY());
             maxY = Math.max(maxY, bottom.getY());
        }
        //Výpočet průsečíků hran se středem každého řádku
        List<Double> intersections = new ArrayList<>();
        for (int y = Math.max(0,minY); y < Math.min(raster.getHeight(), maxY); y++) {
            intersections.clear();
            double scanY = y + 0.5;
            for(Line edge : edges) {
                Point top = edge.getPoint1();
                Point bottom = edge.getPoint2();
                if(y < top.getY() || y >= bottom.getY()) {
                    continue;
                }
                double x = top.getX() + (scanY - top.getY()) * (bottom.getX() - top.getX()) / (bottom.getY() - top.getY());
                intersections.add(x);
            }
            //Seřazení průsečíků a vyplnění pixelů mezi nimi
            intersections.sort(null);
            for (int i = 0; i+1 < intersections.size(); i+=2) {
                //Výpočet počáteční souřadnice
                int from = (int) Math.max(0, Math.ceil(intersections.get(i)-0.5));
                //Výpočet konečné souřadnice
                int to = (int) Math.min(raster.getWidth(), Math.ceil(intersections.get(i+1)-0.5));
                for (int x = from; x <= to; x++) {
                    raster.setPixel(x, y, color);
                }
            }
        }
    }
}

