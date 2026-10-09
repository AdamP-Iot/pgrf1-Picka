package view;

import javax.swing.*;
import java.awt.*;
import controller.Controller;

/**
 * Represents the main application window that displays the {@link Canvas}.
 * The window initializes the canvas, configures the Swing frame, and provides access to the canvas.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Window extends JFrame {

    private final Canvas canvas;
    private JToolBar tb;


    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Window(int width, int height) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("FIM UHK | PGRF1 | 2026 | [Adam Picka]");
        setLayout(new BorderLayout());
        setResizable(false); //extBody kdyz true

        canvas = new Canvas(width, height);
        add(canvas, BorderLayout.CENTER);
        add(ToolBar(), BorderLayout.NORTH);

        pack();

        setLocationRelativeTo(null);
        canvas.setFocusable(true);
        canvas.grabFocus();
        setVisible(true);
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Getters -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Canvas getCanvas() {
        return canvas;
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= JToolBar -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    private JToolBar ToolBar(){
        tb = new JToolBar();
        tb.setVisible(true);
        return tb;
    };
    public void addAction(Action action){
        tb.add(action);
    }
}