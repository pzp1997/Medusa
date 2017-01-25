package com.palmerpaul.Shared;

/**
 * Enumeration of the directions that a snake can move in.
 * 
 * @author palmerpa
 */
public enum Direction {
    LEFT, UP, RIGHT, DOWN;

    /**
     * Parses a string into a direction (essentially a safe form of eval)
     * @param s The string representing a direction. Valid values are
     * "LEFT", "UP", "RIGHT", and "DOWN"
     * @return Direction represented by the string
     */
    public static Direction parse(String s) {
        switch (s) {
        case "LEFT":
            return LEFT;
        case "UP":
            return UP;
        case "RIGHT":
            return RIGHT;
        case "DOWN":
            return DOWN;
        default:
            return null;
        }
    }
    
    /**
     * @return A random direction
     */
    public static Direction getRandom() {
        int randomChoice = (int) (Math.random() * 4);
        switch (randomChoice) {
        case 0:
            return LEFT;
        case 1:
            return UP;
        case 2:
            return RIGHT;
        case 3:
            return DOWN;
        default:
            return null;
        }
    }
}