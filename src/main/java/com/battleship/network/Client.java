package com.battleship.network;

import com.battleship.util.GameLogger;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    private final NetworkManager manager;

    public Client(NetworkManager manager) {
        this.manager = manager;
    }

    public void connect(String host, int port) {
        try {
            GameLogger.log("Client connecting to " + host + ":" + port);
            Socket s = new Socket(host, port);
            manager.attach(s);
        } catch (IOException e) {
            GameLogger.log("Client connection failed: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
