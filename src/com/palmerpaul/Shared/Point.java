package com.palmerpaul.Shared;

/**
 * The abstract (not in the technical sense) concept of a point.
 * @author palmerpa
 */
public class Point {

    private final int x;
    private final int y;
    
    /**
     * Default constructor. Creates a point object located at (0, 0).
     */
    public Point() {
        x = 0;
        y = 0;
    }
    
    /**
     * Creates a point located at a specific location.
     * @param x The x-coordinate of the point to be created
     * @param y The y-coordinate of the point to be created
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor. Creates a copy of an existing point.
     * @param p The point to be copied.
     */
    public Point(Point p) {
        x = p.getX();
        y = p.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    /**
     * Calculates the distance between two points.
     * @param p The other point
     * @return The distance between the points stored as a double (for accuracy).
     */
    public double dist(Point p) {
        int deltaX = x - p.getX();
        int deltaY = y - p.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    /**
     * Determines if the point is off the screen. Used to check if
     * a snake tried to run through a side of screen.
     * @return A boolean that is true if the point is off the screen and false otherwise.
     */
    public boolean isOutOfBounds() {
        return x <= 0 || x >= GameConstants.WID || y <= 0 || y >= GameConstants.HGT;
    }
    
    /**
     * Parses a string representation of a point into a point object.
     * @param s A string representation of a point.
     * @return The point represented by that string as a Point.
     */
    public static Point parse(String s) {

        int openParen = s.lastIndexOf('(');
        int closeParen = s.indexOf(')');

        if (openParen == -1 || closeParen == -1 || openParen > closeParen) {
            return null;
        }

        s = s.substring(openParen + 1, closeParen);

        int comma = s.indexOf(',');

        String fst = s.substring(0, comma).trim();
        String snd = s.substring(comma + 1).trim();

        try {
            int x = Integer.parseInt(fst);
            int y = Integer.parseInt(snd);
            return new Point(x, y);
        } catch (NumberFormatException ex) {
            return null;
        }

    }
    
    /**
     * Gets a random point on the screen. The returned point will be at a
     * valid grid location. Also leaves a border of one grid unit around
     * the edge of the screen so that, when drawn, points won't appear
     * to be half off the screen. Used for spawning snakes and food.
     * @return A random Point on the screen.
     */
    public static Point getRandom() {
        int sz = GameConstants.GRID_UNIT_SIZE;

        // Get random x, y location on Canvas leaving a small border
        int x = (int) (Math.random() * (GameConstants.WID - 2 * sz)) + sz;
        int y = (int) (Math.random() * (GameConstants.HGT - 2 * sz)) + sz;

        // Snap point to grid
        x = (x / sz) * sz;
        y = (y / sz) * sz;

        return new Point(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 37 * (373 * 37 + x) + y;
    }
}