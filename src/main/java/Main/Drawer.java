package Main;

import Classes.CElement;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Drawer extends JPanel {

    private int smallCircleCount = 15;      // Number of small circles
    private int smallCircleRadius = 10;    // Radius of the small circles
    private int layerCompCap = 15;
    private List<CElement> components = new ArrayList<>();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;



        // Get the center of the panel
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        int i = 0;
        double finalBigRadius = 0;
        components.sort(Comparator.comparingInt(CElement::getRank));
        //Transforms the list of components according to the rules for optimization
        listTransform(components);
        // Calculates the radius for every layer and draws the small circles per layer and the outline of the layer itself
        while(i < components.size()) {

            CElement current = components.get(i);
            //Calculates the radius of the current layer
            double bigCircleRadius = calculateBigCircleRadius(current.radius, current.nr_of_components);
            if(bigCircleRadius > finalBigRadius + current.radius) finalBigRadius = bigCircleRadius;
            else finalBigRadius += 2 * current.radius;
            g2d.setColor(Color.BLACK);
            //Draws the layer circle
            g2d.draw(new Ellipse2D.Double(centerX  - finalBigRadius, centerY  - finalBigRadius,
                    2 * finalBigRadius, 2 * finalBigRadius));
            //Draws the small circles on the layer
            drawSmallCircles(g2d, centerX, centerY, current.radius, current.nr_of_components, finalBigRadius, current.color);
            i++;
            System.out.println(components.size());
        }
    }


    protected List<CElement> listTransform(List<CElement> components){
        int i = 0;
        double finalBigRadius = 0;
        while(i < components.size()) {
            CElement current = components.get(i);
            //Splits the components to fit the number of maximum components per layer
            while (components.get(i).nr_of_components > layerCompCap) {

                int inter = (int) current.nr_of_components - layerCompCap;
                components.set(i, new CElement(current.name, current.nr_of_components - inter, current.radius, current.color));
                CElement interC = new CElement(current.name + "1", inter, current.radius, current.color);
                components.add(interC);
                components.sort(Comparator.comparingInt(CElement::getRank));
            }
            //Splits the components based on the space that is left unused in order to optimize size
//            double bigCircleRadius = calculateBigCircleRadius(current.radius, current.nr_of_components);
//            if(bigCircleRadius > finalBigRadius + current.radius){
//                double difference = Math.floor(Math.abs(bigCircleRadius - (finalBigRadius + current.radius))/ current.radius - 1);
//                int compNr= current.nr_of_components;
//                int splitNr = (int) Math.floor(compNr / difference);
//                for(int j = 0; j < difference; j++){
//                    compNr -= splitNr;
//                    CElement interC = new CElement(current.name + "1", splitNr, current.radius, current.color);
//                    components.add(interC);
//                }
//                components.set(i, new CElement(current.name, compNr, current.radius, current.color));
//                finalBigRadius = bigCircleRadius;
//                components.sort(Comparator.comparingInt(CElement::getRank));
//            }
//            else finalBigRadius += 2 * current.radius;


            i++;

        }
        return components;
    }

    protected void drawSmallCircles(Graphics2D g2d, int centerX, int centerY, int smallCircleRadius, int smallCircleCount, double bigCircleRadius, Color color){
        // Calculate the distance from the center for the small circles (R - r)
        int distanceFromCenter = (int) (bigCircleRadius - smallCircleRadius);

        // Calculate the angle step based on the number of small circles
        double angleStep = 360.0 / smallCircleCount;

        // Loop to position the small circles around the big circle
        for (int i = 0; i < smallCircleCount; i++) {
            // Convert the current angle to radians
            double angle = Math.toRadians(i * angleStep);

            // Calculate the x and y position of the small circle's center
            int smallCircleX = centerX + (int) (distanceFromCenter * Math.cos(angle)) - smallCircleRadius;
            int smallCircleY = centerY + (int) (distanceFromCenter * Math.sin(angle)) - smallCircleRadius;

            // Draw the small circle
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Double(smallCircleX , smallCircleY,
                    2 * smallCircleRadius, 2 * smallCircleRadius));
        }
    }

    // Method to calculate the big circle's radius based on small circle radius and count
    private double calculateBigCircleRadius(int smallCircleRadius, int smallCircleCount) {
        // Use the formula: R = r / sin(π / n) + r
        double r = smallCircleRadius;
        double n = smallCircleCount;
        return (r / Math.sin(Math.PI / n)) + r;
    }

    // Main method to run the program
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dynamic Circle Packing");


        Drawer drawer = new Drawer();
        //Add components to the cable
        CElement elem1 = new CElement("comp1",10, 80, Color.BLUE);
        drawer.components.add(elem1);
        elem1 = new CElement("comp2",30, 40, Color.cyan);
        drawer.components.add(elem1);
        elem1 = new CElement("comp3",6, 10, Color.RED);
        drawer.components.add(elem1);
//        elem1 = new CElement("comp3",10, 52, Color.PINK);
//        drawer.components.add(elem1);
        // Add the panel to the frame
        frame.add(drawer);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
