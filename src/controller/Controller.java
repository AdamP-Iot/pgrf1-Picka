package controller;

import graphics.filler.RecursiveSeedFiller;
import graphics.filler.SeedFiller;
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
    private final List<Polygon> polygons;

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

    private Polygon selectedPolygon;
    private int selectedPointIndex;

    private final SeedFiller seedFiller;


    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window) {
        this.canvas = window.getCanvas();
        this.window = window;
        this.rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        this.polygonRasterizer = new PolygonOutLineRasterizer(rasterizer);
        this.polygons = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.seedFiller = new RecursiveSeedFiller(canvas.getRaster());
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
                } else if (mode == MODE_FILL) {
                    if(e.getButton() == MouseEvent.BUTTON1) {
                        Point point = getMousePoint(e);
                        Integer targetColor = canvas.getRaster().getPixel(point.getX(), point.getY());

                        if (targetColor != null) {
                            seedFiller.fill(point.getX(), point.getY(), targetColor, Color.RED.getRGB());
                            canvas.repaint();
                        }
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
                render();
            }
        };
        Action deleteAction = new AbstractAction("Delete Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishPolygon();
                mode = MODE_DELETE;
                previewLine = null;
                previewLine2 = null;
                render();
            }
        };
        Action fillAction = new AbstractAction("Fill Polygon") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_FILL;
                previewLine = null;
                previewLine2 = null;
                render();
            }
        };
        //Odeslání tlačítka do JToolBaru
        window.addAction(cursorAction);
        window.addAction(polygonAction);
        window.addAction(deleteAction);
        window.addAction(fillAction);
        /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Mouse Listeners -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (mode != MODE_POLYGON) {
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
    }
    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Helper Methods -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public void render() {   //překreslení canvasu
        canvas.clear();
        for (Polygon polygon : polygons) {    //už nakreslené polygony
            polygonRasterizer.rasterize(polygon);
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
        render();
    }
}