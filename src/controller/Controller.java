package controller;

import graphics.rasterizer.LineRasterizer;
import graphics.rasterizer.TrivialLineRasterizer;
import view.Canvas;
import model.Line;
import model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final LineRasterizer rasterizer;

    private final Window window;


    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Window window) {
        this.canvas = window.getCanvas();
        this.rasterizer = new TrivialLineRasterizer(canvas.getRaster());
        this.window = window;
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.clear();
        rasterizer.rasterize(new Line(Color.WHITE.getRGB(), new Point(100,100),new Point(50, 400)));
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){

            }
        });
        //ready na tlačítka
        Action lineAction = new AbstractAction("Line"){
            public void actionPerformed(ActionEvent e) {
                System.out.println("Kliknuto");
            }
        };
        window.addAction(lineAction);

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
}