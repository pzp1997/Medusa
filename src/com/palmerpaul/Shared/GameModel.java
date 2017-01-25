package com.palmerpaul.Shared;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The game model used by the client and server. Stores a map of IDs to snakes
 * and another map of IDs to food. The model is 100% thread safe.
 * @author palmerpa
 */
public class GameModel {

    private Map<String, Snake> snakes;
    private Map<String, Food> foods;

    public GameModel() {
        initialize();
    }
    
    /**
     * Initialize and/or clear the model (i.e. when a new game starts).
     */
    public void initialize() {
        snakes = new ConcurrentHashMap<String, Snake>();
        foods = new ConcurrentHashMap<String, Food>();
    }

    public Snake getSnake(String id) {
        return snakes.get(id);
    }

    public Food getFood(String id) {
        return foods.get(id);
    }

    public void addSnake(String id, Snake snake) {
        if (snake != null) {
            snakes.put(id, snake);
        }
    }

    public void addFood(String id, Food food) {
        if (food != null) {
            foods.put(id, food);
        }
    }

    public void removeSnake(String id) {
        snakes.remove(id);

    }

    public void removeFood(String id) {
        foods.remove(id);
    }

    public Iterator<Map.Entry<String, Snake>> allSnakes() {
        return snakes.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Food>> allFoods() {
        return foods.entrySet().iterator();
    }

}
