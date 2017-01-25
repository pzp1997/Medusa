package com.palmerpaul.Shared;

import java.awt.Color;

public class Food {

    private final Point loc;
    private final Color color;

    public Food(Point p) {
        loc = p;
        color = randomColor();
    }

    public Point getPosition() {
        return loc;
    }
    
    public Color getColor() {
        return color;
    }
    
    /**
     * Used internally to generate color of food. The color that is displayed
     * is determined individually by each client (i.e. two clients most likely
     * will not see a particular food object with the same color). Uses hue,
     * saturation, and brightness (HSB) to ensure that the colors are bright.
     * @return A random, bright color
     */
    private Color randomColor() {
        return Color.getHSBColor((float) Math.random(), (float) 0.9, 1);
    }

}