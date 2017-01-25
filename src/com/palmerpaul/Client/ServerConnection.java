package com.palmerpaul.Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.palmerpaul.Shared.Command;
import com.palmerpaul.Shared.Protocol;
import com.palmerpaul.Shared.SocketConnection;

public class ServerConnection extends SocketConnection {

    public ServerConnection(String hostName, int portNumber, Protocol protocol)
            throws UnknownHostException, IOException {
        this(new Socket(hostName, portNumber), protocol);

        System.out.printf("connected to server %s:%d\n", hostName, portNumber);
    }

    public ServerConnection(Socket socket, Protocol protocol) throws IOException {
        super(socket, protocol);
    }

    public void sendMessage(Command cmd) {
        sendMessage(cmd.toString());
    }

}
