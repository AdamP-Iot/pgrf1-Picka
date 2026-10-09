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

    private Line previewLine;
    private final Polygon polygon;
    private static final int LINE_COLOR = Color.WHITE.getRGB();
    private static final int PREVIEW_COLOR = Color.RED.getRGB();



    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window) {
        this.canvas = window.getCanvas();
        this.window = window;
        this.rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        this.polygonRasterizer = new PolygonOutLineRasterizer(rasterizer);
        this.polygon = new Polygon(new ArrayList<>());
        this.lines = new ArrayList<>();

        lines.add(new Line(Color.WHITE.getRGB(), new Point(100,100),new Point(300, 100)));
        lines.add(new Line(Color.WHITE.getRGB(), new Point(300,100),new Point(350, 250)));
        lines.add(new Line(Color.WHITE.getRGB(), new Point(350,250),new Point(200, 350)));
        lines.add(new Line(Color.WHITE.getRGB(), new Point(200,350),new Point(100, 100)));
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
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
            //test -> 1) polygon, rasterizer...teorie, 2 - definice pojem spojovani 3) vypocet algoritmu kalkulacka - smernici,stred... 4)vyplnovani kodu ze cvik
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
            public void mouseDragged(MouseEvent e){
                //TODO: Zobrazit preview čáry
            }
        });
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C){
                    canvas.clear();
                    canvas.repaint();
                }
            }
        });
        canvas.repaint();
    }
    public void render(){
        canvas.clear();
        polygonRasterizer.rasterize(polygon);
        canvas.repaint();
    }
    private void startLine(MouseEvent e){
        //Vzít posledni bod polygonu
        //Vzít getPoint(e) - aktualni bod
        //previewLine = new Line(startPoint, lastPoint, PREVIEW_COLOR);
        render();
    }
    //Pomocna metoda
    private Point getPoint(MouseEvent e){
        return new Point(e.getX(), e.getY());
    }
    private void finishPolygon(){
        previewLine = null;
        render();
    }
}