package controller;

import graphics.clipping.SutherlandHodgmanClipper;
import graphics.filler.*;
import graphics.rasterizer.*;
import view.Canvas;
import model.Line;
import model.Point;
import model.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;

import view.Window;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 * <p>
 * model = data only
 * view = canvas -> vykresleni
 * controller = akce, vstupy uživatele
 * graphics = Raster
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {
    private final Canvas canvas;
    private final Window window;

    private LineRasterizer rasterizer;
    private PolygonRasterizer polygonRasterizer;

    private final List<Line> lines;
    private final List<Line> standaloneLines;
    private final List<Polygon> polygons;
    private Point lineStart;

    private Line previewLine;
    private Line previewLine2;

    private static final int LINE_COLOR = Color.WHITE.getRGB();
    private static final int PREVIEW_COLOR = Color.RED.getRGB();
    private Point firstPoint;
    private Point lastPoint;


    private int mode = MODE_POLYGON;
    private static final int MODE_POLYGON = 0;
    private static final int MODE_CURSOR = 1;
    private static final int MODE_DELETE = 2;
    private static final int MODE_FILL = 3;
    private static final int MODE_CLIP_POLYGON = 4;
    private static final int MODE_LINE = 5;
    private static final int MODE_CLIP_LINE = 6;


    private Polygon selectedPolygon;
    private int selectedPointIndex;

    private String selectedFiller = "ScanLineFiller";
    private final SeedFiller seedFiller;
    private int fillColor = Color.RED.getRGB();
    private final PolygonFiller polygonFiller;
    private final TextureFiller textureFiller;

    private SutherlandHodgmanClipper polygonClipper;
    private final List<Point> clipPoints = new ArrayList<>();



    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window) {
        this.canvas = window.getCanvas();
        this.window = window;
        this.rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        this.polygonRasterizer = new PolygonOutLineRasterizer(rasterizer);
        this.polygons = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.seedFiller = new StackSeedFiller(canvas.getRaster());
        this.polygonFiller = new ScanLineFiller(canvas.getRaster());
        this.polygonClipper = new SutherlandHodgmanClipper();
        this.standaloneLines = new ArrayList<>();
        this.textureFiller = new TextureFiller(canvas.getRaster());
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();
        polygonRasterizer.rasterize(new Polygon(lines));

        //Vstup myší
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mode == MODE_POLYGON) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        startLine(e);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        finishPolygon();
                    }
                } else if (mode == MODE_CURSOR) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        selectNearestPoint(getMousePoint(e));
                    }
                } else if (mode == MODE_DELETE) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        deleteNearestPoint(getMousePoint(e));
                    }
                } else if (mode == MODE_FILL) { //JColorChooser pro výběr barvy na fill
                    if (e.getButton() == MouseEvent.BUTTON1) {

                        Point point = getMousePoint(e);
                        Integer targetColor = canvas.getRaster().getPixel(point.getX(), point.getY());

                        if (targetColor != null) {
                            if ("SeedFiller".equals(selectedFiller)) {
                                Color selectedColor = JColorChooser.showDialog(window, "", new Color(fillColor, true));

                                if (selectedColor != null) {
                                    fillColor = selectedColor.getRGB();
                                }

                                seedFiller.fill(point.getX(), point.getY(), targetColor, fillColor);
                            } else if ("ScanLineFiller".equals(selectedFiller)) {
                                selectNearestPoint(point);
                                Color selectedColor = JColorChooser.showDialog(window, "", new Color(fillColor, true));

                                if (selectedColor != null) {
                                    fillColor = selectedColor.getRGB();
                                }
                                if (selectedPolygon != null) {
                                    selectedPolygon.setFillColor(fillColor);
                                    render();
                                }
                            } else if ("TextureFiller".equals(selectedFiller)) {
                                selectNearestPoint(point);

                                if(selectedPolygon != null){
                                    selectedPolygon.setFillColor(textureFiller.getPattern());
                                    render();
                                }
                            }
                            canvas.repaint();
                        }
                    }
                } else if (mode == MODE_CLIP_POLYGON) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        startLine(e);
                    } else if (e.getButton() == MouseEvent.BUTTON3  && lines.size() >= 2) {
                        finishPolygon();
                        Polygon boundary = polygons.removeLast();
                        List<Polygon> results = new ArrayList<>();

                        for(Polygon subject : polygons){
                            Polygon clipped = polygonClipper.clip(subject, boundary);
                            List<Line> edges = clipped.getLines();

                            if (edges.isEmpty()) {
                                continue;
                            }

                            Point first = edges.getFirst().getPoint1();
                            Point last = edges.getLast().getPoint2();

                            if (first.getX() != last.getX() || first.getY() != last.getY()) {
                                edges.add(new Line(edges.getFirst().getColor(), last, first));
                            }

                            if (subject.getFillColor() != null) {
                                clipped.setFillColor(subject.getFillColor());
                            }
                            results.add(clipped);
                        }
                        if(!results.isEmpty()){
                            polygons.clear();
                            polygons.addAll(results);
                        }
                        selectedPolygon = null;
                        selectedPointIndex = -1;
                        render();
                    }
                } else if (mode == MODE_LINE) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        lineStart = getMousePoint(e);
                    }
                }
            }
        });

        /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= JToolBar Buttons -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        Action polygonAction = new AbstractAction("Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_POLYGON;
            }
        };
        Action cursorAction = new AbstractAction("Cursor") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_CURSOR;
                previewLine = null;
                previewLine2 = null;
            }
        };
        Action deleteAction = new AbstractAction("Delete Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_DELETE;
                previewLine = null;
                previewLine2 = null;
            }
        };
        Action fillAction = new AbstractAction("Fill Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_FILL;
                previewLine = null;
                previewLine2 = null;
            }
        };
        Action clipAction = new AbstractAction("Cut Polygon by Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_CLIP_POLYGON;
                clipPoints.clear();
                previewLine = null;
                previewLine2 = null;
            }
        };
        Action lineAction = new AbstractAction("Line") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_LINE;
                previewLine = null;
                previewLine2 = null;
            }
        };
        Action clipLine = new AbstractAction("Cut Line") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implementovat Cohen-Sutherland
            }
        };
        //Odeslání tlačítka do JToolBaru
        window.addAction(polygonAction);
        window.addAction(deleteAction);
        window.addAction(fillAction);
        window.addAction(clipAction);
        window.addAction(lineAction);
        window.addAction(clipLine);
        window.addAction(cursorAction);
        /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Mouse Listeners -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (mode != MODE_POLYGON && mode != MODE_CLIP_POLYGON) {
                    return;
                }

                Point mousePoint = getMousePoint(e);

                if (lastPoint != null) {
                    previewLine = new Line(PREVIEW_COLOR, lastPoint, getMousePoint(e));
                }

                if (firstPoint != null && lastPoint != null) {
                    previewLine2 = new Line(PREVIEW_COLOR, mousePoint, firstPoint);
                }

                render();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == MODE_CURSOR) {
                    moveNearestPoint(getMousePoint(e));
                } else if (mode == MODE_LINE && lineStart != null) {
                    Point endPoint = getMousePoint(e);

                    if (e.isShiftDown()) {
                        endPoint = angleLock(lineStart, endPoint);
                    }

                    previewLine = new Line(PREVIEW_COLOR, lineStart, endPoint);

                    render();
                }
            }

        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    lineStart = getMousePoint(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (mode == MODE_LINE && e.getButton() == MouseEvent.BUTTON1 && lineStart != null) {
                    drawLine(e);
                }
            }
        });
        /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Keybinds -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        window.getRootPane()    //ošetření ztraceného focus, když klikáme do JToolBaru
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "delete");

        window.getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAll();
            }
        });
        /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Line Rasterizer Change -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        window.getRasterizerBox().addActionListener(e -> {
            String selected = (String) window.getRasterizerBox().getSelectedItem();

            if ("Trivial".equals(selected)) {
                setRasterizer(new TrivialLineRasterizer(canvas.getRaster()));
            } else if ("DDA".equals(selected)) {
                setRasterizer(new DDALineRasterizer(canvas.getRaster()));
            } else if ("Bresenham".equals(selected)) {
                setRasterizer(new BresenhamLineRasterizer(canvas.getRaster()));
            } else if ("Midpoint".equals(selected)) {
                setRasterizer(new MidpointLineRasterizer(canvas.getRaster()));
            }
        });
        window.getFillerBox().addActionListener(e -> {
            String selected = (String) window.getFillerBox().getSelectedItem();
            if ("SeedFiller".equals(selected)) {
                setFiller("SeedFiller");
            } else if ("ScanLineFiller".equals(selected)) {
                setFiller("ScanLineFiller");
            } else if ("TextureFiller".equals(selected)) {
                setFiller("TextureFiller");
            }
        });
    }
    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Helper Methods -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public void render() {   //překreslení canvasu
        canvas.clear();
        for (Polygon polygon : polygons) { //už nakreslené polygony
           //uchování barev
            if(polygon.getFillColor() != null) {
                polygonFiller.fill(polygon,polygon.getFillColor());
            } else if (polygon.getFillPattern() != null) {
                textureFiller.fill(polygon,polygon.getFillPattern());
            }
            polygonRasterizer.rasterize(polygon);
        }
        for (Line line : standaloneLines) {
            rasterizer.rasterize(line);
        }
        polygonRasterizer.rasterize(new Polygon(lines));   //právě kreslený polygon

        if (previewLine != null) {
            rasterizer.rasterize(previewLine);
        }
        if (previewLine2 != null) {
            rasterizer.rasterize(previewLine2);
        }

        canvas.repaint();
    }

    private void startLine(MouseEvent e) {   //začátek vykreslení line
        Point point = getMousePoint(e);
        if (firstPoint == null) {
            firstPoint = point;
            lastPoint = point;
        } else {
            lines.add(new Line(LINE_COLOR, lastPoint, point));
            lastPoint = point;
        }
        render();
    }
    private void drawLine(MouseEvent e) {
        Point endPoint = getMousePoint(e);

        if (e.isShiftDown()) {
            endPoint = angleLock(lineStart, endPoint);
        }

        standaloneLines.add(new Line(LINE_COLOR, lineStart, endPoint));
        lineStart = null;
        previewLine = null;

        render();
    }
    private Point angleLock(Point start, Point end) {

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();

        double length = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx);

        double angleStep = Math.PI / 4; // 45° v radiánech
        double lockedAngle = Math.round(angle / angleStep) * angleStep; // zaokrouhlení na nejbližších 45°

        int x = start.getX() + (int) Math.round(Math.cos(lockedAngle) * length);
        int y = start.getY() + (int) Math.round(Math.sin(lockedAngle) * length);

        return new Point(x, y);
    }

    private Point getMousePoint(MouseEvent e) {  //Pomocna metoda pro souřadnici myši
        return new Point(e.getX(), e.getY());
    }

    private void finishPolygon() {   //konec vykreslování
        if (firstPoint != null && lastPoint != null && firstPoint != lastPoint) {
            lines.add(new Line(LINE_COLOR, lastPoint, firstPoint));
        }
        polygons.add(new Polygon(new ArrayList<>(lines)));

        lines.clear();
        previewLine = null;
        previewLine2 = null;
        lastPoint = null;
        firstPoint = null;

        render();
    }

    private void deleteAll() {   //vymazání všech prvků na canvasu
        polygons.clear();
        lines.clear();
        previewLine = null;
        previewLine2 = null;
        lastPoint = null;
        firstPoint = null;
        canvas.clear();
        canvas.repaint();
    }

    private void selectNearestPoint(Point mousePoint) {  //metoda pro získání nejlbižšího bodu
        double nearestDistance = Double.MAX_VALUE;
        selectedPolygon = null;
        selectedPointIndex = -1;

        for (Polygon polygon : polygons) {

            List<Line> polygonLines = polygon.getLines();

            for (int i = 0; i < polygonLines.size(); i++) {

                Point point = polygonLines.get(i).getPoint1();
                double dx = mousePoint.getX() - point.getX();
                double dy = mousePoint.getY() - point.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    selectedPolygon = polygon;
                    selectedPointIndex = i;
                }
            }
        }
    }

    private void deleteNearestPoint(Point mousePoint) {  //metoda pro vymazání nejbližšího bodu
        selectNearestPoint(mousePoint);
        if (selectedPolygon == null || selectedPointIndex == -1) {
            return;
        }

        List<Line> polygonLines = selectedPolygon.getLines();    //edit polygonů
        int previousIndex = (selectedPointIndex - 1 + polygonLines.size()) % polygonLines.size(); //ošetření když nearestIndex == 0

        Line previousLine = polygonLines.get(previousIndex);
        Line currentLine = polygonLines.get(selectedPointIndex);

        Point previousPoint = previousLine.getPoint1();
        Point nextPoint = currentLine.getPoint2();

        polygonLines.set(previousIndex, new Line(LINE_COLOR, previousPoint, nextPoint));
        polygonLines.remove(selectedPointIndex);
        render();
    }

    private void moveNearestPoint(Point point) { //metoda pro pohnutí nejbližšího bodu
        if (selectedPolygon != null) {

            List<Line> polygonLines = selectedPolygon.getLines();
            int previousIndex = (selectedPointIndex - 1 + polygonLines.size()) % polygonLines.size();

            Line previousLine = polygonLines.get(previousIndex);
            Line currentLine = polygonLines.get(selectedPointIndex);

            polygonLines.set(previousIndex, new Line(LINE_COLOR, previousLine.getPoint1(), point)); //první select
            polygonLines.set(selectedPointIndex, new Line(LINE_COLOR, point, currentLine.getPoint2())); //druhý select
            render();
        }
    }

    private void setRasterizer(LineRasterizer rasterizer) {  //setter pro vybraný vykreslovací algoritmus
        this.rasterizer = rasterizer;
        this.polygonRasterizer = new PolygonOutLineRasterizer(rasterizer);
    }
    private void setFiller(String selectedFiller) {
        this.selectedFiller = selectedFiller;
    }
}
