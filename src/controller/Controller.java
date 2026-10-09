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
                if(e.getButton() == MouseEvent.BUTTON3){
                    finishPolygon();
                } else if (e.getButton()== MouseEvent.BUTTON1) {
                    startLine(e);
                } else if(e.getButton() == MouseEvent.BUTTON2){

                }
            }
        });
        //Tlačítka
        Action lineAction = new AbstractAction("Line"){
            public void actionPerformed(ActionEvent e) {
                System.out.println("Kliknuto");
            }
        };
        //Odeslání tlačítka do JToolBaru
        window.addAction(lineAction);

        canvas.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e){
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
}