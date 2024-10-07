package Classes;

import java.awt.*;

public class CElement {
    public int nr_of_components;
    public String name;
    public int radius;
    public Color color;

    public CElement(String name, int nr_of_components, int radius, Color color){
        this.name = name;
        this.nr_of_components = nr_of_components;
        this.radius = radius;
        this.color = color;
    }
    public int getRank(){
        return this.nr_of_components * this.radius;
    }
}
