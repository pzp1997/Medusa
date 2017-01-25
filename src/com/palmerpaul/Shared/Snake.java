package com.palmerpaul.Shared;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Snakes are stored as instances of this class.
 * 
 * @author palmerpa
 */
public class Snake {

    private Deque<Point> body;
    private Direction moveDir;
    
    /**
     * Creates a new Snake object
     * @param points The list of points that initially make up the snake's body
     * @param dir The initial direction that the snake is moving in
     */
    public Snake(List<Point> points, Direction dir) {
        body = new ConcurrentLinkedDeque<Point>(points);
        moveDir = dir;
    }
    
    /**
     * @return Length of the snake
     */
    public int length() {
        return body.size();
    }
    
    /**
     * Moves the snake one grid unit in the direction of its movement.
     * It does this by adjusting the old head based on the movement
     * direction, adding that new point to the end of the body and
     * removing the first element of the body. This has the effect of
     * very efficiently moving the snake, as it only requires accessing,
     * adding, and removing one Point object at an end of the Deque
     * and all of those operations are O(1) for a Deque.
     */
    public void move() {
        Point head;
        int x, y;
        
        head = getHead();
        x = head.getX();
        y = head.getY();

        switch (getDirection()) {
        case LEFT:
            x -= GameConstants.GRID_UNIT_SIZE;
            break;
        case UP:
            y -= GameConstants.GRID_UNIT_SIZE;
            break;
        case RIGHT:
            x += GameConstants.GRID_UNIT_SIZE;
            break;
        case DOWN:
            y += GameConstants.GRID_UNIT_SIZE;
            break;
        }
        
        Point p = new Point(x, y);
        
        body.addLast(p);
        body.removeFirst();
    }

    public Direction getDirection() {
        return moveDir;
    }

    public void setDirection(Direction dir) {
        moveDir = dir;
    }
    
    /**
     * @return The head of the snake
     */
    public Point getHead() {
        return body.peekLast();
    }
    
    /**
     * @return The "last" of the snake
     */
    public Point getLast() {
        return body.peekFirst();
    }
    
    /**
     * Provides a convenient (and safe) way of accessing the points that make
     * up the snake's body. The order that the points are iterated over is from
     * the head to the last of the snake.
     * @return An iterator over the body of the snake
     */
    public Iterator<Point> getBody() {
        return body.descendingIterator();
    }
    
    /**
     * Similar to {@code getBody()}, but does not include the head of the snake.
     * @return An iterator over the tail of the snake
     */
    public Iterator<Point> getTail() {
        Iterator<Point> tail = getBody();
        if (tail.hasNext()) {
            tail.next();
        }
        return tail;
    }
    
    /**
     * Causes the snake to grow by one Point. Used when the snake eats food.
     * Most of the time, it just makes the new last a copy of the old last.
     * The special case of when the length of the snake is 1 is needed so
     * that the game does not end as soon as a snake eats its first food.
     * (This is because when the length is 1, the head and tail are actually
     * the same point.)
     */
    public void eat() {
        int x = getLast().getX();
        int y = getLast().getY();
        
        if (length() == 1) {
            switch (moveDir) {
            case LEFT:
                x += GameConstants.GRID_UNIT_SIZE;
                break;
            case UP:
                y += GameConstants.GRID_UNIT_SIZE;
                break;
            case RIGHT:
                x -= GameConstants.GRID_UNIT_SIZE;
                break;
            case DOWN:
                y -= GameConstants.GRID_UNIT_SIZE;
                break;
            }
        }
        
        body.addFirst(new Point(x, y));
    }

    @Override
    public String toString() {
        String pointsString = "{";
        Iterator<Point> snakeBody = getBody();
        while (snakeBody.hasNext()) {
            Point p = snakeBody.next();
            pointsString += String.format("(%d,%d);", p.getX(), p.getY());
        }
        pointsString += "}";

        return getDirection() + " " + pointsString;
    }

}