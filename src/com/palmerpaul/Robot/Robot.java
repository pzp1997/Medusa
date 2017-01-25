package com.palmerpaul.Robot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.palmerpaul.Client.Game;
import com.palmerpaul.Shared.Direction;

public final class Robot {

    private static int move;
    private final static int DELAY = (int) (Math.random() * 303 + 491);
    
    public static void main(String[] args) {

        new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.server != null) {
                    Direction dir = Direction.LEFT;
                    switch (move) {
                    case 0:
                        dir = Direction.LEFT;
                        break;
                    case 1:
                        dir = Direction.UP;
                        break;
                    case 2:
                        dir = Direction.RIGHT;
                        break;
                    case 3:
                        dir = Direction.DOWN;
                    }
                    Game.server.sendMessage("KEY " + dir);
                    move = (move + 1) % 4;
                }
            }
        }).start();
        
        SwingUtilities.invokeLater(new Game());
    }

}
