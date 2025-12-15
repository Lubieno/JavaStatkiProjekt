package com.battleship.network;

import com.battleship.util.GameLogger;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread implements Closeable {
    private final String host;
    private final String playerName;
    private final String roomId;
    private final boolean isHost;

    // Pola sieciowe
    private final int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile boolean connected = false;
    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    public Client(String host, int port, Object networkManager, String playerName, String roomId, boolean isHost) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.roomId = roomId;
        this.isHost = isHost;
    }

    public void connect() {
        this.start();
    }

    @Override
    public void run() {
        try {
            if (isHost) {
                GameLogger.log("[Host] Rozpoczynanie nasłuchiwania na porcie: " + port);

                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new java.net.InetSocketAddress(port));

                socket = serverSocket.accept();
                GameLogger.log("[Host] Połączono z gościem: " + socket.getInetAddress().getHostAddress());
            } else {
                GameLogger.log("[Guest] Próba połączenia z " + host + ":" + port);
                socket = new Socket(host, port);
                GameLogger.log("[Guest] Połączono z hostem.");
            }
            performStreamSynchronization();
            connected = true;

            performRoomHandshake();

            while (connected && !Thread.currentThread().isInterrupted()) {
                Message msg = (Message) inputStream.readObject();
                receivedMessages.put(msg);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Rozłączono lub błąd: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void performStreamSynchronization() throws IOException {

        if (isHost) {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            GameLogger.log("[Host] Utworzono OOS.");

            inputStream = new ObjectInputStream(socket.getInputStream());
            GameLogger.log("[Host] Utworzono OIS (wejście).");

        } else {
            inputStream = new ObjectInputStream(socket.getInputStream());
            GameLogger.log("[Guest] Utworzono OIS.");
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            GameLogger.log("[Guest] Utworzono OOS (wyjście).");
        }

        GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Synchronizacja strumieni TCP zakończona.");
    }

    private void performRoomHandshake() throws IOException, ClassNotFoundException, InterruptedException {

        if (isHost) {
            Message joinMsg = (Message) inputStream.readObject();
            String remoteRoomId = (String) joinMsg.payload().get("roomId");
            String remoteName = (String) joinMsg.payload().get("name");

            if (roomId.equals(remoteRoomId)) {
                GameLogger.log("[Host] Gość (" + remoteName + ") dołączył do pokoju: " + remoteRoomId);
                Message readyMsg = new Message(Message.MsgType.READY, Map.of("opponentName", remoteName, "yourTurn", true));
                send(readyMsg);
                receivedMessages.put(new Message(Message.MsgType.READY, Map.of("opponentName", remoteName, "yourTurn", false)));
            } else {
                Message disconnectMsg = new Message(Message.MsgType.DISCONNECT, Map.of("reason", "Niepoprawny numer pokoju."));
                send(disconnectMsg);
                throw new IOException("Niepoprawny numer pokoju: " + remoteRoomId);
            }
        } else {
            Message joinMsg = new Message(Message.MsgType.JOIN_ROOM,
                    Map.of("name", playerName, "roomId", roomId));
            send(joinMsg);

            Message readyMsg = (Message) inputStream.readObject();
            if (readyMsg.type() == Message.MsgType.READY) {
                receivedMessages.put(readyMsg);
            } else {
                String reason = (String) readyMsg.payload().getOrDefault("reason", "Nieznany błąd.");
                throw new IOException("Nieudane uzgodnienie pokoju: " + reason);
            }
        }
    }

    public boolean isConnected() { return connected; }

    public void send(Message msg) {
        if (!connected || outputStream == null) {
            GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Nie połączono. Pominięto wysłanie: " + msg);
            return;
        }
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (IOException e) {
            GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Błąd wysyłania: " + e.getMessage());
            disconnect();
        }
    }

    public Message receiveMessage() {
        return receivedMessages.poll();
    }

    public void disconnect() {
        if (!connected) return;
        connected = false;
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            this.interrupt();
            GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Połączenie zamknięte.");
        } catch (IOException e) {
            GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Błąd zamykania zasobów: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        disconnect();
    }
}