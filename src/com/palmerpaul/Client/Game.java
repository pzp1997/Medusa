package com.palmerpaul.Client;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.palmerpaul.Shared.GameModel;

public class Game implements Runnable {
    
    public static ServerConnection server;
    public static final GameModel model = new GameModel();
    public static Canvas canvas;
    public static String YOU;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        ServerConnection server;
        
        while (true) {
            String serverLocation = JOptionPane.showInputDialog("Medusa (online multiplayer snake)\n"
                    + "Enter the hostname and port of the server (e.g. localhost:21212): ");

            int colonIdx = serverLocation.indexOf(':');
            if (colonIdx == -1) {
                // no colon in server location
                JOptionPane.showMessageDialog(null, "There must be a colon dividing the hostname and port.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String hostName = serverLocation.substring(0, colonIdx).trim();
            
            int portNumber;
            try {
                portNumber = Integer.parseInt(serverLocation.substring(colonIdx + 1).trim());
            } catch (NumberFormatException ex) {
                // portNumber is not a valid number
                JOptionPane.showMessageDialog(null, "Port must a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            
            try {
                server = new ServerConnection(hostName, portNumber, new ClientProtocol(model));
            } catch (UnknownHostException ex) {
                // hostname and portnumber combination doesn't map to a valid server
                JOptionPane.showMessageDialog(null, "Could not locate server with specified hostname and port.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            } catch (IOException ex) {
                // something went wrong with connecting to the server
                JOptionPane.showMessageDialog(null, "Could not connect to specified server.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            
            break;
        }
        
        Game.server = server;
        server.sendMessage(new WhoAmICommand());
        
        // Show the instructions before the game starts
        final String instructionsMessage = "Arrow keys to move.\n"
                + "Your goal: to be the biggest baddest serpent out there.\n"
                + "Eating food will help you in your mission.\n"
                + "If you collide with the tail of any snake (including yourself), game over.\n"
                + "If you run into the side of screen, game over.\n"
                + "Good luck.";
        
        JOptionPane.showMessageDialog(
                null, instructionsMessage, "Instructions", JOptionPane.INFORMATION_MESSAGE);
        

        // Top-level window
        final JFrame frame = new JFrame("Medusa");

        // Add GUI components to frame
        final Canvas canvas = new Canvas(server);
        Game.canvas = canvas;
        frame.add(canvas);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        // Start game
        canvas.setup();

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }

}
