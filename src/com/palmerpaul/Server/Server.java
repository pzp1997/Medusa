package com.palmerpaul.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;

import javax.swing.Timer;

import com.palmerpaul.Shared.Food;
import com.palmerpaul.Shared.GameConstants;
import com.palmerpaul.Shared.Point;
import com.palmerpaul.Shared.Snake;

final class Server {

    private volatile ServerSocket serverSocket;
    private final Map<String, ClientConnection> openConnections;
    private volatile boolean running;

    Server(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        openConnections = Collections.synchronizedMap(new HashMap<String, ClientConnection>());
        running = true;

        for (int i = 0; i < GameConstants.AMOUNT_OF_FOOD; i++) {
            ServerMain.serverModel.addFood(generateUniqueId(), new Food(Point.getRandom()));
        }

        // Handles new connections to the server.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String userId = generateUniqueId();
                        openConnections.put(userId, new ClientConnection(userId, clientSocket));
                        if (ServerMain.LOGGER != null) {
                            ServerMain.LOGGER.printf("new client connection -- assigned id %s\n", userId);
                        }
                    } catch (IOException e) {
                    }
                }
                killServer();
            }
        }).start();

        // Performs ticks and updates clients every UPDATE_INTERVAL ms
        new Timer(GameConstants.SERVER_UPDATE_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        }).start();
    }

    private void tick() {
        Entry<String, Snake> snakeEntry;
        Snake snake;

        Iterator<Point> otherTail;
        Point otherPoint;

        Iterator<Entry<String, Food>> foodIter;
        Entry<String, Food> foodEntry;

        Iterator<Entry<String, Snake>> snakes = ServerMain.serverModel.allSnakes();
        while (snakes.hasNext()) {
            snakeEntry = snakes.next();
            snake = snakeEntry.getValue();

            Point snakeHead = snake.getHead();
            if (snakeHead == null || snakeHead.isOutOfBounds()) {
                snakes.remove();
                new Response(new DestroyCommand(snakeEntry.getKey(), "SNAKE"), ServerMain.server.allClientIds()).send();
                continue;
            }

            // Collision with food
            foodIter = ServerMain.serverModel.allFoods();
            while (foodIter.hasNext()) {
                foodEntry = foodIter.next();
                if (snakeHead.equals(foodEntry.getValue().getPosition())) {

                    snake.eat();
                    new Response(new GrowCommand(snakeEntry.getKey()), ServerMain.server.allClientIds()).send();

                    foodIter.remove();
                    new Response(new DestroyCommand(foodEntry.getKey(), "FOOD"), ServerMain.server.allClientIds())
                            .send();

                    Food newFood = new Food(Point.getRandom());
                    String newFoodId = ServerMain.server.generateUniqueId();
                    ServerMain.serverModel.addFood(newFoodId, newFood);
                    new Response(new CreateCommand(newFoodId, newFood), ServerMain.server.allClientIds()).send();

                    break;
                }
            }

            // Collision with tail of other snake
            Iterator<Entry<String, Snake>> otherSnakes = ServerMain.serverModel.allSnakes();
            outerloop: while (otherSnakes.hasNext()) {
                otherTail = otherSnakes.next().getValue().getTail();
                while (otherTail.hasNext()) {
                    otherPoint = otherTail.next();
                    if (snakeHead.equals(otherPoint)) {
                        snakes.remove();
                        new Response(new DestroyCommand(snakeEntry.getKey(), "SNAKE"), ServerMain.server.allClientIds())
                                .send();
                        break outerloop;
                    }
                }

            }

            snake.move();
        }

        new Response("TICK", ServerMain.server.allClientIds()).send();
    }

    public void disconnect(String userId) {
        openConnections.remove(userId);
        ServerMain.serverModel.removeSnake(userId);
        if (ServerMain.LOGGER != null) {
            ServerMain.LOGGER.printf("client %s disconnected\n", userId);
        }
    }

    public void sendResponse(Response resp) {
        String msg = resp.getMessage();

        ClientConnection client;
        for (String userId : resp.getRecipients()) {
            client = openConnections.get(userId);
            client.sendMessage(msg);
        }
    }

    public Set<String> allClientIds() {
        return new TreeSet<String>(openConnections.keySet());
    }

    public void killServer() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                running = false;
            } catch (IOException e) {
            }
        }

        synchronized (openConnections) {
            for (ClientConnection cc : openConnections.values()) {
                cc.killConnection();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String generateUniqueId() {
        String id = UUID.randomUUID().toString();
        return id.length() > 8 ? id.substring(0, 8) : id;
    }

}
