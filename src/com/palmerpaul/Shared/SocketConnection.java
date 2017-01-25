package com.palmerpaul.Shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A connection with a socket. On a high-level, it wraps a socket object to make I/O
 * easier. On a low-level it makes a PrintWriter from the socket's output stream,
 * a BufferedReader from the socket's input stream, and starts up a thread that waits
 * for incoming messages.
 * 
 * @author palmerpa
 */
public class SocketConnection {
    
    private Socket socket;
    private PrintWriter out;
    
    /**
     * Creates a new SocketConnection object
     * @param socket The socket being wrapped
     * @param protocol The protocol object that should be used to process incoming messages
     * @throws IOException
     */
    public SocketConnection(final Socket socket, final Protocol protocol) throws IOException {
        /* Socket is stored to prevent it from closing when the constructor exits.
         * Also allows for the socket to be gracefully closed in killConnection()
         */
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String request;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    while (!socket.isClosed() && (request = in.readLine()) != null) {
                        protocol.process(request);
                    }
                } catch (IOException e) {
                } finally {
                    killConnection();
                }
            }
        }).start();
    }
    
    /**
     * Closes all open resources associated with this connection. This includes
     * the PrintWriter and socket. Note that the BufferedReader is closed automatically
     * by the try-with statement used in the thread.
     */
    public void killConnection() {
        if (out != null) {
            out.close();
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }
    
    /**
     * Exposes the PrintWriter to the outside world so that messages can be sent
     * from other parts of the application
     * @param msg Message to be sent
     */
    public void sendMessage(String msg) {
        out.println(msg);
    }

}
