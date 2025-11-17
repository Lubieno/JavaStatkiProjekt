package com.battleship.controller;

import com.battleship.network.Client;
import com.battleship.network.NetworkManager;
import com.battleship.network.Message;

/**
 * Kontroler sieciowy — wrapper nad NetworkManager/Client.
 * W tej wersji implementacji działa jako stub: przechowuje konfigurację
 * i eksponuje metody do przyszłej komunikacji TCP.
 */
public class NetworkController {
    private final NetworkManager networkManager;
    private Client client;

    public NetworkController() {
        this.networkManager = new NetworkManager();
    }

    public void startServer(int port) {
        networkManager.startServer(port);
    }

    public void connectTo(String host, int port) {
        client = new Client(host, port, networkManager);
        client.connect();
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void sendMessage(Message msg) {
        if (client != null && client.isConnected()) {
            client.send(msg);
        } else {
            // network is stubbed — log or ignore
            System.out.println("[NetworkController] Not connected. Message skipped: " + msg);
        }
    }

    public Message receiveMessage() {
        if (client != null && client.isConnected()) {
            return client.receive();
        }
        return null;
    }
}
