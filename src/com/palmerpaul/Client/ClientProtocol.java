package com.palmerpaul.Client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.palmerpaul.Shared.Direction;
import com.palmerpaul.Shared.Food;
import com.palmerpaul.Shared.GameModel;
import com.palmerpaul.Shared.Point;
import com.palmerpaul.Shared.Protocol;
import com.palmerpaul.Shared.Snake;

public class ClientProtocol extends Protocol {

    public ClientProtocol(GameModel model) {
        super(model); // no pun intended
    }

    public void process(String line) {
        String[] tokens = parseTokens(line.toUpperCase());
        String id;

        if (tokens.length < 1) {
            return;
        }

        switch (tokens[0]) {

        case "CREATE":
            if (tokens.length < 3) {
                break;
            }

            id = tokens[2];

            if (tokens[1].equals("FOOD") && tokens.length == 4) {
                Point loc = Point.parse(tokens[3]);
                if (loc == null) {
                    break;
                }

                model.addFood(id, new Food(loc));
            }

            else if (tokens[1].equals("SNAKE") && tokens.length == 5) {
                Direction dir = Direction.parse(tokens[3]);
                if (dir == null) {
                    break;
                }

                String pointsString = tokens[4];

                int semiColon;
                Point p;
                List<Point> points = new ArrayList<Point>();

                while ((semiColon = pointsString.indexOf(';')) != -1) {
                    p = Point.parse(pointsString.substring(0, semiColon));
                    if (p != null) {
                        points.add(p);
                    }
                    pointsString = pointsString.substring(semiColon + 1);
                }
                
                if (!points.isEmpty()) {
                    model.addSnake(id, new Snake(points, dir));
                }
            }

            break;

        case "DESTROY":
            if (tokens.length != 3) {
                break;
            }

            id = tokens[2];

            if (tokens[1].equals("FOOD")) {
                model.removeFood(id);
            }

            else if (tokens[1].equals("SNAKE")) {
                if (id.equals(Game.YOU)) {
                    Game.canvas.gameOver();
                }
                model.removeSnake(id);
            }

            break;

        case "TICK":
            if (tokens.length != 1) {
                break;
            }

            Iterator<Entry<String, Snake>> snakes = model.allSnakes();
            Snake s;
            while (snakes.hasNext()) {
                s = snakes.next().getValue();
                if (s == null) {
                    snakes.remove();
                    continue;
                }
                s.move();
            }

            break;

        case "TURN":
            if (tokens.length != 3) {
                break;
            }

            id = tokens[1];

            Direction dir1 = Direction.parse(tokens[2]);
            if (dir1 == null) {
                break;
            }

            turn(id, dir1);
            break;

        case "GROW":
            if (tokens.length != 2) {
                break;
            }

            id = tokens[1];

            grow(id);
            break;

        case "YOU":
            if (tokens.length != 2) {
                break;
            }

            id = tokens[1];
            Game.YOU = id;
            break;
        }
    }

    private void grow(String id) {
        Snake snake = model.getSnake(id);
        if (snake != null) {
            snake.eat();
        }
    }

    public void turn(String id, Direction dir) {
        Snake snake = model.getSnake(id);
        if (snake != null) {
            snake.setDirection(dir);
        }
    }

}
