package com.battleship.controller;

import com.battleship.network.Message;
import com.battleship.network.NetworkManager;
import com.battleship.util.GameLogger;

import java.io.IOException;
import java.util.Map;

public class NetworkController {

    private final NetworkManager net;

    public NetworkController(NetworkManager net) {
        this.net = net;
    }

    public void send(String type, Map<String, Object> data) {
        try {
            net.send(new Message(type, data));
        } catch (IOException e) {
            GameLogger.log("NetworkController send error: " + e.getMessage());
        }
    }
}
