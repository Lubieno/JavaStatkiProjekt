package com.battleship.network;

import com.battleship.util.GameLogger;

/**
 * Wzorzec Fasady (Facade) dla warstwy sieciowej.
 * Klasa ta ukrywa szczegóły tworzenia i konfiguracji instancji `Client`.
 * Dostarcza prosty interfejs API dla kontrolerów do inicjowania połączeń.
 */
public class NetworkManager {
    private Client client;

    public static final int DEFAULT_PORT = 54321;

    public void startListening(int port, String playerName, String roomId) {
        if (client != null && client.isConnected()) {
            GameLogger.log("Klient jest już połączony/nasłuchuje.");
            return;
        }
        this.client = new Client(null, port, this, playerName, roomId, true);
        this.client.connect();
    }

    public void connectTo(String host, int port, String playerName, String roomId) {
        if (client != null && client.isConnected()) {
            GameLogger.log("Klient jest już połączony/nasłuchuje.");
            return;
        }
        this.client = new Client(host, port, this, playerName, roomId, false);
        this.client.connect();
    }

    public Client getClient() {
        return client;
    }
}