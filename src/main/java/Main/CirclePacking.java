package Main;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.variables.IntVar;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CirclePacking extends JPanel {

    private static final int PADDING = 50;  // Padding around the drawing area
    private int largeRadius;
    private int[] xCoords;
    private int[] yCoords;
    private int[] smallRadii;

    // Constructor to initialize the solution values
    public CirclePacking(int largeRadius, int[] xCoords, int[] yCoords, int[] smallRadii) {
        this.largeRadius = largeRadius;
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.smallRadii = smallRadii;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate center of the large circle in the panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw the large circle
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));  // Thin black line
        g2d.drawOval(centerX - largeRadius, centerY - largeRadius, 2 * largeRadius, 2 * largeRadius);

        // Draw the small circles (red filled disks)
        g2d.setColor(Color.RED);
        for (int i = 0; i < xCoords.length; i++) {
            int smallX = centerX + xCoords[i] - smallRadii[i];
            int smallY = centerY + yCoords[i] - smallRadii[i];
            g2d.fillOval(smallX, smallY, 2 * smallRadii[i], 2 * smallRadii[i]);
        }
    }

    public static void main(String[] args) {
        // Step 1: Create a model
        Model model = new Model("Minimize Radius of Large Circle");

        // Define the sizes and number of circles
        int[] smallRadii = {20, 20, 20, 20, 20, 15, 15, 15, 15, 10, 10, 10, 10};  // Radii of the small circles
        int numCircles = smallRadii.length;    // Number of small circles
        int upperBound = 80;
        smallRadii = Arrays.stream(smallRadii).sorted().toArray();
        // Step 2: Define a variable for the radius of the large circle
        IntVar largeRadius = model.intVar("largeRadius", 0, upperBound);

        // Step 3: Define variables for the center (x, y) of each small circle
        IntVar[] x = new IntVar[numCircles];
        IntVar[] y = new IntVar[numCircles];

        for (int i = 0; i < numCircles; i++) {
            x[i] = model.intVar("x_" + i, -upperBound, upperBound);  // Bounds for x
            y[i] = model.intVar("y_" + i, -upperBound, upperBound);  // Bounds for y
        }

        // Step 4: Constraints
        // 4.1: Each small circle must be fully inside the large circle
        for (int i = 0; i < numCircles; i++) {
            ArExpression squaredDistance = x[i].mul(x[i]).add(y[i].mul(y[i])); // (x[i]^2 + y[i]^2)
            ArExpression quadr = largeRadius.sub(smallRadii[i]).mul(largeRadius.sub(smallRadii[i])); // (largeRadius - smallRadius[i])^2
            model.arithm(squaredDistance.intVar(), "<=", quadr.intVar()).post();
        }

//        for (int i = 0; i < numCircles; i++) {
//            ArExpression distanceFromOrigin = x[i].mul(x[i]).add(y[i].mul(y[i]));
//            ArExpression exactDistance = largeRadius.sub(smallRadii[i]).mul(largeRadius.sub(smallRadii[i]));
//            model.arithm(distanceFromOrigin.intVar(), "=", exactDistance.intVar()).post();
//        }

        // 4.2: No overlap between any two circles
        for (int i = 0; i < numCircles; i++) {
            for (int j = i + 1; j < numCircles; j++) {
                ArExpression squaredDistanceBetweenCenters = x[i].sub(x[j]).mul(x[i].sub(x[j]))
                        .add(y[i].sub(y[j]).mul(y[i].sub(y[j]))); // (x[i] - x[j])^2 + (y[i] - y[j])^2
                int minDistanceSquared = (smallRadii[i] + smallRadii[j]) * (smallRadii[i] + smallRadii[j]); // (smallRadius[i] + smallRadius[j])^2
                model.arithm(squaredDistanceBetweenCenters.intVar(), ">=", minDistanceSquared).post();
            }
        }

        // Step 5: Define an objective to minimize the large circle's radius
        model.setObjective(Model.MINIMIZE, largeRadius);

        // Step 6: Solve the problem and get the solution
        Solution solution = model.getSolver().findSolution();

        if (solution != null) {
            int minimizedLargeRadius = solution.getIntVal(largeRadius);
            int[] xCoords = new int[numCircles];
            int[] yCoords = new int[numCircles];
            for (int i = 0; i < numCircles; i++) {
                xCoords[i] = solution.getIntVal(x[i]);
                yCoords[i] = solution.getIntVal(y[i]);
                System.out.println("Circle " + i + ": (x = " + xCoords[i] + ", y = " + yCoords[i] + ")");
            }

            System.out.println("Minimized large circle radius: " + minimizedLargeRadius);

            // Step 7: Create a JFrame to display the drawing
            JFrame frame = new JFrame("Circle Packing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);

            // Add the CirclePacking JPanel to the frame
            CirclePacking panel = new CirclePacking(minimizedLargeRadius, xCoords, yCoords, smallRadii);
            frame.add(panel);

            frame.setVisible(true);
        } else {
            System.out.println("No solution found.");
        }
    }
}
