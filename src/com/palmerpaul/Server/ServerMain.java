package com.palmerpaul.Server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.palmerpaul.Shared.GameModel;

public final class ServerMain {
    public static Server server;
    public static final GameModel serverModel = new GameModel();
    public static final PrintWriter LOGGER = new PrintWriter(System.out, true);

    private ServerMain() {
    }

    public static void main(String[] args) throws IOException {

        int portNumber;

        while (true) {
            String port = JOptionPane
                    .showInputDialog("Medusa Server\n" + "Enter port for the server to run on (e.g. 21212): ");

            try {
                portNumber = Integer.parseInt(port.trim());
            } catch (NumberFormatException ex) {
                // portNumber is not a valid number
                JOptionPane.showMessageDialog(null, "Port must a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            break;
        }

        server = new Server(portNumber);

        final JFrame frame = new JFrame("Medusa Server");
        JLabel message = new JLabel("Medusa server is running on port " + portNumber);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        frame.add(message);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(400, 150));

        final Timer timer = new Timer(100, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!frame.isDisplayable() || !server.isRunning()) {
                    frame.dispose();
                    server.killServer();
                    timer.stop();
                    System.exit(0);
                }
            }
        });
        timer.start();

        frame.setVisible(true);
    }
}
