package com.palmerpaul.Client;

import com.palmerpaul.Shared.Command;
import com.palmerpaul.Shared.Direction;

class KeyCommand extends Command {

    public KeyCommand(Direction dir) {
        super("KEY " + dir);
    }

}

class StartCommand extends Command {

    public StartCommand() {
        super("START");
    }

}

class WhoAmICommand extends Command {

    public WhoAmICommand() {
        super("WHOAMI");
    }

}