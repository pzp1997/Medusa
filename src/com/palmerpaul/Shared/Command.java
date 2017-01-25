package com.palmerpaul.Shared;

/**
 * Parent class of all commands sent by the server and client.
 * Each command has its own associated format / grammar.
 * 
 * @author palmerpa
 */
public abstract class Command {

    private String cmd;

    public Command(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

}
