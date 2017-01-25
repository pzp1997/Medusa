package com.palmerpaul.Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.palmerpaul.Shared.Command;
import com.palmerpaul.Shared.Direction;
import com.palmerpaul.Shared.Food;
import com.palmerpaul.Shared.GameConstants;
import com.palmerpaul.Shared.GameModel;
import com.palmerpaul.Shared.Point;
import com.palmerpaul.Shared.Snake;

/**
 * The main display. All graphics are drawn here.
 * 
 * @author palmerpa
 */
@SuppressWarnings("serial")
public class Canvas extends JPanel {
    
    private GameModel model = Game.model;
    private final ServerConnection server;

    Canvas(ServerConnection server) {
        this.server = server;

        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (Game.YOU == null) {
                    Canvas.this.server.sendMessage(new WhoAmICommand());
                    return;
                }
                
                Snake snake = model.getSnake(Game.YOU);
                if (snake == null) {
                    return;
                }
                
                Direction movementDir = snake.getDirection();
                Command cmd = null;
                
                // Only allows key presses that are orthogonal to the snakes motion
                switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (movementDir == Direction.UP || movementDir == Direction.DOWN) {
                        cmd = new KeyCommand(Direction.LEFT);
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (movementDir == Direction.LEFT || movementDir == Direction.RIGHT) {
                        cmd = new KeyCommand(Direction.UP);
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (movementDir == Direction.UP || movementDir == Direction.DOWN) {
                        cmd = new KeyCommand(Direction.RIGHT);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (movementDir == Direction.LEFT || movementDir == Direction.RIGHT) {
                        cmd = new KeyCommand(Direction.DOWN);
                    }
                    break;
                }

                if (cmd != null) {
                    Canvas.this.server.sendMessage(cmd);
                }
            }
        });
        
        // Redraws the canvas every CLIENT_REDRAW_RATE milliseconds.
        new Timer(GameConstants.CLIENT_REDRAW_RATE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        }).start();
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void setup() {
        model.initialize();
        server.sendMessage(new StartCommand());
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }
    
    /**
     * Prompts the user to play a new game when they die.
     */
    public void gameOver() {
        String yourLength = "";
        if (Game.YOU != null) {
            Snake yourSnake = model.getSnake(Game.YOU);
            if (yourSnake != null) {
                yourLength = String.format("Your final length was %d.\n", yourSnake.length());
            }
        }
        
        if (JOptionPane.showConfirmDialog(null, "GAME OVER!\n" + yourLength + "Play again?",
                "Game Over", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            setup();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Draw a point as a circle with diameter the size of one grid unit.
     * @param p Point to draw
     * @param g Graphics context where point is drawn
     */
    public void drawPoint(Point p, Graphics g) {
        int diam = GameConstants.GRID_UNIT_SIZE;
        g.fillOval(p.getX() - diam / 2, p.getY() - diam / 2, diam, diam);
    }
    
    /**
     * Draw a point with a specific color. Restores the graphics context to
     * its original color upon exiting.
     * @param p Point to draw
     * @param g Graphics context where point is drawn
     * @param c Color of point
     */
    public void drawPoint(Point p, Graphics g, Color c) {
        Color oldColor = g.getColor();
        g.setColor(c);
        drawPoint(p, g);
        g.setColor(oldColor);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConstants.WID, GameConstants.HGT);
        g.setColor(Color.WHITE);
        
        // Draw all snakes in white
        Iterator<Entry<String, Snake>> snakes = model.allSnakes();
        Snake snake;
        while (snakes.hasNext()) {
            snake = snakes.next().getValue();
            if (snake != null) {
                Iterator<Point> snakeBody = snake.getBody();
                while (snakeBody.hasNext()) {
                    drawPoint(snakeBody.next(), g);
                }
            }
        }
        
        // Draw all food with different colors
        Iterator<Entry<String, Food>> foods = model.allFoods();
        Food food;
        while (foods.hasNext()) {
            food = foods.next().getValue();
            if (food != null) {
                drawPoint(food.getPosition(), g, food.getColor());
            }
        }
        
        // Make the head of your snake red
        Snake yourSnake = model.getSnake(Game.YOU);
        if (yourSnake != null) {
            drawPoint(yourSnake.getHead(), g, Color.RED);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameConstants.WID, GameConstants.HGT);
    }
}
