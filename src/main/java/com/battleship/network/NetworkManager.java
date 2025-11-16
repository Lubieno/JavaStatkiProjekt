package com.battleship.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkManager {

    private Consumer<Message> handler;
    private Socket socket;

    public void setHandler(Consumer<Message> handler) {
        this.handler = handler;
    }

    public void startServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        new Thread(() -> {
            try {
                socket = server.accept();
                attach(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connectTo(String host, int port) throws IOException {
        socket = new Socket(host, port);
        attach(socket);
    }

    public void attach(Socket s) throws IOException {
        this.socket = s;
        // tutaj dodajesz ObjectInputStream/ObjectOutputStream i wątek odbierający
        // jak w poprzednim przykładzie
    }

    public void send(Message msg) throws IOException {
        // implementacja wysyłania wiadomości przez socket
    }
}
