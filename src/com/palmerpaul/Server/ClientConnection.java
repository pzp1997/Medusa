package com.palmerpaul.Server;

import java.io.IOException;
import java.net.Socket;

import com.palmerpaul.Shared.SocketConnection;

class ClientConnection extends SocketConnection {

    private final String userId;

    public ClientConnection(String userId, Socket clientSocket) throws IOException {
        super(clientSocket, new ServerProtocol(userId, ServerMain.serverModel));
        this.userId = userId;
    }

    @Override
    public void killConnection() {
        super.killConnection();
        ServerMain.server.disconnect(userId);
    }

}
