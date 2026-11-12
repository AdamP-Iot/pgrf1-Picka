
package graphics.clipping;

import model.Line;
import model.Point;

public class CohenSutherlandClipper {
    private static final int INSIDE = 0;
    private static final int TOP = 1;
    private static final int BOTTOM = 2;
    private static final int RIGHT = 4;
    private static final int LEFT = 8;

    public Line clip(Line line, Point corner1, Point corner2) {

        int xMin = Math.min(corner1.getX(), corner2.getX());
        int xMax = Math.max(corner1.getX(), corner2.getX());
        int yMin = Math.min(corner1.getY(), corner2.getY());
        int yMax = Math.max(corner1.getY(), corner2.getY());

        double x1 = line.getPoint1().getX();
        double y1 = line.getPoint1().getY();
        double x2 = line.getPoint2().getX();
        double y2 = line.getPoint2().getY();

        int code1 = getCode(x1, y1, xMin, xMax, yMin, yMax);
        int code2 = getCode(x2, y2, xMin, xMax, yMin, yMax);

        while (true) {

            if (code1 == 0 && code2 == 0) { //Check jestli je celá úsečka uvnitř
                return new Line(line.getColor(), new Point((int) Math.round(x1), (int) Math.round(y1)), new Point((int) Math.round(x2), (int) Math.round(y2)));
            }

            if ((x1 < xMin && x2 < xMin) || (x1 > xMax && x2 > xMax) || (y1 < yMin && y2 < yMin) || (y1 > yMax && y2 > yMax)) { //Check jestli je úsečka mimo
                return null;
            }

            int outsideCode; // Check jestli úsečka protíná ořezávací okno

            if (code1 != INSIDE) {
                outsideCode = code1;
            } else {
                outsideCode = code2;
            }

            double x;
            double y;

            if (code1 != INSIDE) {
                if (y1 < yMin) {
                    y = yMin;
                    x = x1 + (yMin - y1) * (x2 - x1) / (y2 - y1);

                } else if (y1 > yMax) {
                    y = yMax;
                    x = x1 + (yMax - y1) * (x2 - x1) / (y2 - y1);

                } else if (x1 > xMax) {
                    x = xMax;
                    y = y1 + (xMax - x1) * (y2 - y1) / (x2 - x1);

                } else {
                    x = xMin;
                    y = y1 + (xMin - x1) * (y2 - y1) / (x2 - x1);
                }

            } else {

                if (y2 < yMin) {
                    y = yMin;
                    x = x1 + (yMin - y1) * (x2 - x1) / (y2 - y1);

                } else if (y2 > yMax) {
                    y = yMax;
                    x = x1 + (yMax - y1) * (x2 - x1) / (y2 - y1);

                } else if (x2 > xMax) {
                    x = xMax;
                    y = y1 + (xMax - x1) * (y2 - y1) / (x2 - x1);

                } else {
                    x = xMin;
                    y = y1 + (xMin - x1) * (y2 - y1) / (x2 - x1);
                }
            }

            if (outsideCode == code1) { //Nahrazení vnějšího bodu průsečíkem
                x1 = x;
                y1 = y;
                code1 = getCode(x1, y1, xMin, xMax, yMin, yMax);
            } else {
                x2 = x;
                y2 = y;
                code2 = getCode(x2, y2, xMin, xMax, yMin, yMax);
            }
        }
    }

    private int getCode(double x, double y, int xMin, int xMax, int yMin, int yMax) {
        int code = INSIDE;

        if (x < xMin) {
            code = code + LEFT;
        } else if (x > xMax) {
            code = code + RIGHT;
        }

        if (y < yMin) {
            code = code + TOP;
        } else if (y > yMax) {
            code = code + BOTTOM;
        }

        return code;
    }
}
