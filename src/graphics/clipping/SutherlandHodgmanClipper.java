package graphics.clipping;


import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public final class SutherlandHodgmanClipper implements PolygonClipper {

    @Override
    public Polygon clip(Polygon subject, Polygon boundary) {
        List<Line> subjectEdges = subject.getLines();
        List<Point> result = vertices(subject);
        List<Point> boundaryVertices = vertices(boundary);

        if (result.size() < 3) {
            return new Polygon(new ArrayList<>());
        }

        double boundaryArea = area(boundaryVertices);
        if (boundaryVertices.size() < 3 || boundaryArea == 0) {
            throw new IllegalArgumentException("Polygon musí mít alespoň tři vrcholy.");
        }

        boolean ccw = boundaryArea > 0;

        for (int i = 0; i < boundaryVertices.size(); i++) {
            Point a = boundaryVertices.get(i);
            Point b = boundaryVertices.get((i + 1) % boundaryVertices.size());

            result = clipEdge(result, a, b, ccw);
            if (result.size() < 3) {
                return new Polygon(new ArrayList<>());
            }
        }

        return toPolygon(result, subjectEdges.getFirst().getColor());
    }

    public boolean isInside(Point p, Point a, Point b, boolean ccw) {
        double side = cross(a, b, p);
        return ccw ? side >= 0 : side <= 0; //Ukázka ze cvika -> prvni strana pro TRUE, druhá pro FAlSE
    }

    public Point intersection(Point p, Point q, Point a, Point b) {
        double d1 = cross(a, b, p);
        double d2 = cross(a, b, q);

        if (d1 == d2) {
            throw new IllegalArgumentException("Přímky nemají průsečík.");
        }

        double t = d1 / (d1 - d2);
        double x = p.getX() + t * ((double) q.getX() - p.getX());
        double y = p.getY() + t * ((double) q.getY() - p.getY());

        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    private List<Point> clipEdge(List<Point> polygon, Point a, Point b, boolean ccw) {
        List<Point> result = new ArrayList<>();
        Point previous = polygon.getLast();
        boolean previousInside = isInside(previous, a, b, ccw);

        for (Point current : polygon) {
            boolean currentInside = isInside(current, a, b, ccw);

            if (previousInside != currentInside) {
                result.add(intersection(previous, current, a, b));
            }
            if (currentInside) {
                result.add(current);
            }

            previous = current;
            previousInside = currentInside;
        }

        return result;
    }

    private double area(List<Point> polygon) {
        double sum = 0;
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());
            sum += (double) a.getX() * b.getY() - (double) b.getX() * a.getY();
        }
        return sum / 2;
    }

    private double cross(Point a, Point b, Point p) {
        double abX = (double) b.getX() - a.getX();
        double abY = (double) b.getY() - a.getY();
        double apX = (double) p.getX() - a.getX();
        double apY = (double) p.getY() - a.getY();
        return abX * apY - abY * apX;
    }

    private List<Point> vertices(Polygon polygon) {
        List<Line> lines = polygon.getLines();
        List<Point> result = new ArrayList<>();

        for (Line line : lines) {
            result.add(line.getPoint1());
        }

        if (!lines.isEmpty()) {
            Point first = lines.getFirst().getPoint1();
            Point last = lines.getLast().getPoint2();

            if (first.getX() != last.getX() || first.getY() != last.getY()) {
                result.add(last);
            }
        }

        return result;
    }

    private Polygon toPolygon(List<Point> points, int color) {
        List<Line> lines = new ArrayList<>();

        if (points.size() < 3 || area(points) == 0) {
            return new Polygon(lines);
        }

        for (int i = 1; i < points.size(); i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);

            if (a.getX() != b.getX() || a.getY() != b.getY()) {
                lines.add(new Line(color, a, b));
            }
        }

        return new Polygon(lines);
    }
}