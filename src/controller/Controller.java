package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.PolygonOutLineRasterizer;
import graphics.rasterizer.PolygonRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import view.Canvas;
import model.Line;
import model.Point;
import model.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import view.Window;


/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 *1) Uloha - midpoint/trivialni-y=kx+q/DDA/Bresenhamuv
 * model = data only
 * view = canvas -> vykresleni
 * controller = akce, vstupy uživatele
 * graphics = Raster
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller  {
    private final Canvas canvas;
    private final Window window;

    private final LineRasterizer rasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final List<Line> lines;
    private final List<Polygon> polygons;

    private Line previewLine;
    private Line previewLine2;

    private static final int LINE_COLOR = Color.WHITE.getRGB();
    private static final int PREVIEW_COLOR = Color.RED.getRGB();
    private Point firstPoint;
    private Point lastPoint;

    private static final int MODE_POLYGON = 0;
    private static final int MODE_CURSOR = 1;
    private static final int MODE_DELETE = 2;

    private int mode = MODE_POLYGON;


    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window) {
        this.canvas = window.getCanvas();
        this.window = window;
        this.rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        this.polygonRasterizer = new PolygonOutLineRasterizer(rasterizer);
        this.polygons = new ArrayList<>();
        this.lines = new ArrayList<>();

//        lines.add(new Line(Color.WHITE.getRGB(), new Point(100,100),new Point(300, 100)));
//        lines.add(new Line(Color.WHITE.getRGB(), new Point(300,100),new Point(350, 250)));
//        lines.add(new Line(Color.WHITE.getRGB(), new Point(350,250),new Point(200, 350)));
//        lines.add(new Line(Color.WHITE.getRGB(), new Point(200,350),new Point(100, 100)));
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();
        polygonRasterizer.rasterize(new Polygon(lines));
        //rasterizer.rasterize(new Line(Color.WHITE.getRGB(), new Point(100,100),new Point(50, 400)));
        //rasterizer.rasterize(new Line(Color.WHITE.getRGB(), new Point(200,300),new Point(200, 400)));
        //rasterizer.rasterize(new Line(Color.WHITE.getRGB(), new Point(150,200),new Point(100, 200)));

        //Vstup myší
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(mode == MODE_POLYGON){
                    if(e.getButton() == MouseEvent.BUTTON1){
                        startLine(e);
                    } else if (e.getButton()== MouseEvent.BUTTON3) {
                        finishPolygon();
                    } else if(e.getButton() == MouseEvent.BUTTON2){

                    }
                } else if (mode == MODE_CURSOR){
                    if(e.getButton() == MouseEvent.BUTTON1){

                    } else if (e.getButton()== MouseEvent.BUTTON3) {

                    } else if(e.getButton() == MouseEvent.BUTTON2){

                    }
                } else if (mode == MODE_DELETE){
                    if(e.getButton() == MouseEvent.BUTTON1){
                        deleteNearestPoint(getPoint(e));
                    }
                }
            }
        });
        //Tlačítka
        Action polygonAction = new AbstractAction("Polygon"){
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_POLYGON;
            }
        };
        Action cursorAction = new AbstractAction("Cursor"){
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_CURSOR;
                previewLine = null;
                previewLine2 = null;
                render();
            }
        };
        Action deleteAction = new AbstractAction("Delete Polygon"){
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = MODE_DELETE;
                previewLine = null;
                previewLine2 = null;
                render();
            }
        };
        //Odeslání tlačítka do JToolBaru
        window.addAction(cursorAction);
        window.addAction(polygonAction);
        window.addAction(deleteAction);

        canvas.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e){
                if(mode != MODE_POLYGON){
                    return;
                }

                Point mousePoint = getPoint(e);
                if(lastPoint != null){
                    previewLine = new Line(PREVIEW_COLOR,lastPoint,getPoint(e));
                }
                if(firstPoint != null && lastPoint != null){
                    previewLine2 = new Line(PREVIEW_COLOR,mousePoint,firstPoint);
                }
                render();
            }
        });

        window.getRootPane()    //ošetření ztraceného focus, když klikáme do JToolBaru
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "delete");
        window.getRootPane().getActionMap().put("delete", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAll();
            }
        });


        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C){
                    deleteAll();
                    render();
                }
            }
        });
        canvas.repaint();
    }
    public void render(){
        canvas.clear();
        for(Polygon polygon : polygons){    //už nakreslené polygony
            polygonRasterizer.rasterize(polygon);
        }

        polygonRasterizer.rasterize(new Polygon(lines));    //právě kreslený polygon

        if(previewLine != null){
            rasterizer.rasterize(previewLine);
        }
        if(previewLine2 != null){
            rasterizer.rasterize(previewLine2);
        }
        canvas.repaint();
    }
    private void startLine(MouseEvent e){
        Point point = getPoint(e);
        if(firstPoint == null){
            firstPoint = point;
            lastPoint = point;
        } else{
            lines.add(new Line(LINE_COLOR, lastPoint, point));
            lastPoint = point;
        }
        render();
    }
    //Pomocna metoda
    private Point getPoint(MouseEvent e){
        return new Point(e.getX(), e.getY());
    }
    private void finishPolygon(){
        if(firstPoint != null && lastPoint != null && firstPoint != lastPoint){
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
    private void deleteAll(){
        polygons.clear();
        lines.clear();
        previewLine = null;
        previewLine2 = null;
        lastPoint = null;
        firstPoint = null;
        canvas.clear();
        canvas.repaint();
    }
    private void deleteNearestPoint(Point mousePoint){
        double nearestDistance = Double.MAX_VALUE;
        Polygon nearestPolygon = null;
        int nearestIndex = -1;

        for(Polygon polygon : polygons){
            List<Line> polygonLines = polygon.getLines();   //hrany aktuálního polygonu
            for(int i = 0; i < polygonLines.size(); i++){

                Point point = polygonLines.get(i).getPoint1();  //projdeme je a vypočítáme vzdálenost mezi body
                double dx = mousePoint.getX() - point.getX();
                double dy = mousePoint.getY() - point.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if(distance < nearestDistance){
                    nearestDistance = distance;
                    nearestPolygon = polygon;
                    nearestIndex = i;

                }
            }
        }
        if(nearestPolygon == null ){
            return;
        }

        List<Line> polygonLines = nearestPolygon.getLines();    //edit polygonů
        int previousIndex = (nearestIndex - 1 + polygonLines.size()) % polygonLines.size(); //ošetření když nearestIndex==0

        Line previousLine = polygonLines.get(previousIndex);
        Line currentLine = polygonLines.get(nearestIndex);

        Point previousPoint = previousLine.getPoint1();
        Point nextPoint = currentLine.getPoint2();

        polygonLines.set(previousIndex, new Line(LINE_COLOR, previousPoint, nextPoint));
        polygonLines.remove(nearestIndex);
        render();

    }
}