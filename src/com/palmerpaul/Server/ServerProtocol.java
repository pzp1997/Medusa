package com.palmerpaul.Server;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.palmerpaul.Shared.Direction;
import com.palmerpaul.Shared.Food;
import com.palmerpaul.Shared.GameConstants;
import com.palmerpaul.Shared.GameModel;
import com.palmerpaul.Shared.Point;
import com.palmerpaul.Shared.Protocol;
import com.palmerpaul.Shared.Snake;

class ServerProtocol extends Protocol {

    private final String userId;

    ServerProtocol(String userId, GameModel model) {
        super(model); // no pun intended
        this.userId = userId;
    }

    @Override
    protected void process(String line) {
        String[] tokens = parseTokens(line.toUpperCase());

        if (tokens.length < 1) {
            new Response("INVALID COMMAND", userId).send();
            return;
        }

        switch (tokens[0]) {
        case "START":
            if (tokens.length != 1) {
                new Response("INVALID FORMAT", userId).send();
                break;
            }
            start(userId);
            break;
        case "KEY":
            Direction dir;
            if (tokens.length != 2 || (dir = Direction.parse(tokens[1])) == null) {
                new Response("INVALID FORMAT", userId).send();
                break;
            }
            key(userId, dir);
            break;
        case "WHOAMI":
            if (tokens.length != 1) {
                new Response("INVALID FORMAT", userId).send();
                break;
            }
            new Response("YOU " + userId, userId).send();
            break;
        case "PING":
            new Response("PONG", userId).send();
        default:
            new Response("INVALID COMMAND", userId).send();
        }
    }

    private void start(String senderId) {
        Iterator<Map.Entry<String, Snake>> snakes = model.allSnakes();
        while (snakes.hasNext()) {
            Map.Entry<String, Snake> entry = snakes.next();
            new Response(new CreateCommand(entry.getKey(), entry.getValue()), senderId).send();
        }

        Iterator<Map.Entry<String, Food>> foods = model.allFoods();
        while (foods.hasNext()) {
            Map.Entry<String, Food> entry = foods.next();
            new Response(new CreateCommand(entry.getKey(), entry.getValue()), senderId).send();
        }

        // TODO make sure that snake can't spawn on top of other snakes?
        Snake snake = new Snake(Collections.singletonList(Point.getRandom()), Direction.getRandom());
        model.addSnake(senderId, snake);

        new Response(new CreateCommand(senderId, snake), ServerMain.server.allClientIds()).send();
    }

    private void key(String id, Direction dir) {
        Snake snake = model.getSnake(id);
        if (snake == null) {
            return;
        }
        
        Direction moveDir = snake.getDirection();
        
        if ((moveDir == Direction.UP || moveDir == Direction.DOWN) &&
                (dir == Direction.UP || dir == Direction.DOWN)) {
            return;
        }
        
        if ((moveDir == Direction.LEFT || moveDir == Direction.RIGHT) &&
                (dir == Direction.LEFT || dir == Direction.RIGHT)) {
            return;
        }

        snake.setDirection(dir);

        new Response(new TurnCommand(id, dir), ServerMain.server.allClientIds()).send();
    }

}
