package com.battleship.network;

import com.battleship.util.GameLogger;

/**
 * @Author Student
 *
 * Zaimplementowany menadżer sieci - fasada dla połączeń TCP Klient-Klient (P2P).
 * Umożliwia uruchomienie jako Host (nasłuchujący) lub Guest (łączący się).
 */
public class NetworkManager {
    private Client client;

    public static final int DEFAULT_PORT = 54321;

    // Tryb nasłuchujący (Host) - zastępuje startServer
    public void startListening(int port, String playerName, String roomId) {
        if (client != null && client.isConnected()) {
            GameLogger.log("Klient jest już połączony/nasłuchuje.");
            return;
        }
        // Host ma null host, isHost = true
        this.client = new Client(null, port, this, playerName, roomId, true);
        this.client.connect(); // Rozpoczyna nasłuchiwanie
    }

    // Tryb łączący się (Guest) - zastępuje connect
    public void connectTo(String host, int port, String playerName, String roomId) {
        if (client != null && client.isConnected()) {
            GameLogger.log("Klient jest już połączony/nasłuchuje.");
            return;
        }
        // Guest ma docelowy host, isHost = false
        this.client = new Client(host, port, this, playerName, roomId, false);
        this.client.connect();
    }

    public Client getClient() {
        return client;
    }
}