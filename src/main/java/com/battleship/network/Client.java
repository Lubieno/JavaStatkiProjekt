package com.battleship.network;

import com.battleship.util.GameLogger;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Klasa realizująca niskopoziomową obsługę komunikacji sieciowej TCP/IP.
 * Rozszerza klasę `Thread`, aby operacje wejścia/wyjścia (blokujące `readObject`)
 * wykonywały się w tle, nie zamrażając interfejsu użytkownika (UI).
 *
 * Implementuje logikę zarówno dla strony Hosta (ServerSocket) jak i Gościa (Socket),
 * w zależności od parametru `isHost` przekazanego w konstruktorze.
 */
public class Client extends Thread implements Closeable {
    private final String host;
    private final String playerName;
    private final String roomId;
    private final boolean isHost;

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

    /**
     * Uruchamia wątek sieciowy.
     */
    public void connect() {
        this.start();
    }

    /**
     * Główna pętla wątku sieciowego.
     * Odpowiada za nawiązanie połączenia, synchronizację strumieni oraz ciągły nasłuch wiadomości.
     */
    @Override
    public void run() {
        try {
            if (isHost) {
                GameLogger.log("[Host] Rozpoczynanie nasłuchiwania na porcie: " + port);

                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port));

                socket = serverSocket.accept();
                GameLogger.log("[Host] Połączono z gościem: " + socket.getInetAddress().getHostAddress());
            } else {
                GameLogger.log("[Guest] Próba połączenia z " + host + ":" + port);
                socket = new Socket();
                // Ustawienie timeoutu połączenia na 5000ms zapobiega nieskończonemu wiszeniu przy błędnym IP
                socket.connect(new InetSocketAddress(host, port), 5000);
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

    /**
     * Inicjalizuje strumienie obiektów (Object Input/Output).
     * Kluczowa jest tu kolejność: Host najpierw tworzy Output i flushuje nagłówek,
     * Gość najpierw tworzy Input. Odwrotna kolejność lub jednoczesne tworzenie Input
     * po obu stronach prowadziłoby do zakleszczenia (deadlock) na etapie odczytu nagłówka.
     */
    private void performStreamSynchronization() throws IOException {
        if (isHost) {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } else {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
        }
        GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Synchronizacja strumieni TCP zakończona.");
    }

    /**
     * Protokół weryfikacji pokoju.
     * Sprawdza, czy obie strony próbują dołączyć do tej samej sesji (roomId).
     * Zapobiega przypadkowym połączeniom z niepożądanymi klientami w tej samej sieci.
     */
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

    /**
     * Wysyła wiadomość do gniazda. Metoda jest thread-safe dzięki naturze strumieni blokujących.
     */
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

    /**
     * Pobiera odebraną wiadomość z kolejki blokującej.
     * @return Wiadomość lub null, jeśli kolejka pusta.
     */
    public Message receiveMessage() {
        return receivedMessages.poll();
    }

    /**
     * Bezpiecznie zamyka połączenie i zwalnia zasoby systemowe.
     * Implementacja z interfejsu Closeable.
     */
    public void disconnect() {
        if (!connected && socket == null) return;
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