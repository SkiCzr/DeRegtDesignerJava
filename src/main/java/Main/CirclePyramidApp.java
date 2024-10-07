package Main;

import javax.swing.*;
import java.awt.*;

class CirclePyramidPanel extends JPanel {
    private int circleCount;
    private int circleRadius;

    public CirclePyramidPanel(int circleCount, int circleRadius) {
        this.circleCount = circleCount;
        this.circleRadius = circleRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int yPosition = 10;  // Initial vertical position for the first row
        int level = 1;  // The current level in the pyramid (starting with 1 circle)

        int circlesLeft = circleCount;
        while (circlesLeft > 0) {
            // Calculate how many circles to draw on this level
            int circlesInRow = Math.min(level, circlesLeft);
            circlesLeft -= circlesInRow;

            // Calculate the initial x position for centering the row
            int totalRowWidth = circlesInRow * 2 * circleRadius;  // Total width of the current row
            int xPosition = (getWidth() - totalRowWidth) / 2;  // Center horizontally

            // Draw circles in this row
            for (int i = 0; i < circlesInRow; i++) {
                g.drawOval(xPosition, yPosition, 2 * circleRadius, 2 * circleRadius);
                xPosition += 2 * circleRadius;  // Move to the next circle
            }

            // Move the y position down for the next row
            yPosition += 2 * circleRadius;

            // Move to the next level of the pyramid
            level++;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);  // Set preferred size of the window
    }
}

public class CirclePyramidApp extends JFrame {
    public CirclePyramidApp(int circleCount, int circleRadius) {
        setTitle("Pyramid of Circles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add the panel to draw the circles
        CirclePyramidPanel panel = new CirclePyramidPanel(circleCount, circleRadius);
        add(panel);

        pack();  // Automatically set the window size
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        // Parameters: total number of circles, radius of each circle
        int circleCount = 15;
        int circleRadius = 30;

        // Create the window and display it
        new CirclePyramidApp(circleCount, circleRadius);
    }
}
