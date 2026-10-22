package view;

import javax.swing.*;
import javax.swing.border.Border;
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
    JComboBox<String> rasterizerBox;

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

    private JToolBar ToolBar() {
        tb = new JToolBar();
        tb.setVisible(true);
        tb.setBackground(new Color(0x252526));
        tb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,new Color(0x444444)),
                BorderFactory.createEmptyBorder(8, 10, 10, 10)
        ));
        String[] rasterizers = {"Trivial", "DDA", "Bresenham", "Midpoint"};

        Dimension componentSize = new Dimension(180,35);
        rasterizerBox = new JComboBox<>(rasterizers);
        rasterizerBox.setMaximumSize(new Dimension(120, 25));
        rasterizerBox.setBackground(new Color(0x343434));
        rasterizerBox.setForeground(Color.WHITE);
        rasterizerBox.setPreferredSize(componentSize);
        rasterizerBox.setMaximumSize(componentSize);
        rasterizerBox.setMinimumSize(componentSize);
        rasterizerBox.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        rasterizerBox.setBorder(BorderFactory.createLineBorder(new Color(0x4A4A4A),1));

        tb.add(rasterizerBox);
        tb.add(Box.createHorizontalStrut(12));

        return tb;
    }

    public void addAction(Action action) {
        JButton button = tb.add(action);

        button.setIconTextGap(8);
        button.setBackground(new Color(0x2F2F2F));
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x4A4A4A), 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        tb.add(Box.createHorizontalStrut(8));


    }

    public JComboBox<String> getRasterizerBox() {
        return rasterizerBox;
    }
}