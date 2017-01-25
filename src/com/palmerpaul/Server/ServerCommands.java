package com.palmerpaul.Server;

import com.palmerpaul.Shared.Command;
import com.palmerpaul.Shared.Direction;
import com.palmerpaul.Shared.Food;
import com.palmerpaul.Shared.Snake;

class CreateCommand extends Command {

    public CreateCommand(String id, Snake snake) {
        super(String.format("CREATE SNAKE %s %s", id, snake));
    }

    public CreateCommand(String id, Food food) {
        super(String.format("CREATE FOOD %s %s", id, food.getPosition()));
    }

}

class DestroyCommand extends Command {

    public DestroyCommand(String id, String type) {
        super(String.format("DESTROY %s %s", type, id));
    }

}

class GrowCommand extends Command {

    public GrowCommand(String id) {
        super("GROW " + id);
    }

}

class TurnCommand extends Command {

    public TurnCommand(String id, Direction dir) {
        super(String.format("TURN %s %s", id, dir));
    }

    public TurnCommand(Direction dir) {
        super(String.format("TURN %s", dir));
    }

}
