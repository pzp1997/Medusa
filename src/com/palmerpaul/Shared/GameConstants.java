package com.palmerpaul.Shared;

/**
 * All the "magic numbers" used in the game. Allows for easy modification.
 * @author palmerpa
 */
public final class GameConstants {

    public static final int WID = 500; // pixels
    public static final int HGT = 500; // pixels
    
    /* Conceptually, the canvas can be thought of as a grid where each Point is
     * drawn with the diameter of "one grid unit". All points must be located at
     * valid grid locations in order for collision detection to work properly.
     * One grid unit is the metric that is used to determine all of those properties.
     */
    public static final int GRID_UNIT_SIZE = 10; // pixels
    
    public static final int SERVER_UPDATE_INTERVAL = 50; // milliseconds
    // Should be considerably less than SERVER_UPDATE_INTERVAL to prevent lag
    public static final int CLIENT_REDRAW_RATE = 10; // milliseconds

    public static final int AMOUNT_OF_FOOD = 10;

}
