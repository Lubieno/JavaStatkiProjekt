package com.battleship.network;

/**
 * @Author Student
 *
 * Klasa Client - stub klienta sieciowego. W obecnej wersji nie otwiera socketów,
 * ale dostarcza API do integracji w przyszłości.
 */
public class Client {
    private final String host;
    private final int port;
    private final NetworkManager networkManager;
    private boolean connected = false;

    public Client(String host, int port, NetworkManager networkManager) {
        this.host = host;
        this.port = port;
        this.networkManager = networkManager;
    }

    public void connect() {
        // stub: nie łączymy rzeczywiście, ustawiamy flagę false
        System.out.println("[Client] Pretend connecting to " + host + ":" + port);
        // jeśli będziesz implementować: otwórz socket, wątki, strumienie itp.
        connected = false; // keep false to denote no real network in this assignment
    }

    public boolean isConnected() { return connected; }

    public void send(Message msg) {
        if (!connected) {
            System.out.println("[Client] Not connected. Would send: " + msg);
            return;
        }
        // real send implementation goes here
    }

    public Message receive() {
        if (!connected) return null;
        // read from network and deserialize
        return null;
    }

    public void disconnect() {
        if (connected) {
            // close sockets
            connected = false;
        }
    }
}
