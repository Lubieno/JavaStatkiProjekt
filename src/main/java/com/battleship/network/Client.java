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

/**
 * @Author Student
 *
 * Klasa Client - zaimplementowany klient sieciowy TCP w trybie Klient-Klient (P2P).
 * Może działać jako Host (nasłuchuje) lub Guest (łączy się).
 * Wprowadzono asymetryczną, standardową synchronizację ObjectStream (Host OOS -> OIS, Guest OIS -> OOS).
 */
public class Client extends Thread implements Closeable {
    // Pola dodatkowe dla trybu P2P
    private final String host; // Adres do połączenia (null dla Host)
    private final String playerName;
    private final String roomId;
    private final boolean isHost;

    // Pola sieciowe
    private final int port;
    private ServerSocket serverSocket; // Używany tylko przez Host
    private Socket socket;             // Połączenie z przeciwnikiem
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile boolean connected = false;
    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    // Zmieniony konstruktor dla P2P
    public Client(String host, int port, Object networkManager, String playerName, String roomId, boolean isHost) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.roomId = roomId;
        this.isHost = isHost;
    }

    // Metoda connect staje się punktem startowym wątku
    public void connect() {
        this.start(); // Uruchom wątek nasłuchujący/łączący
    }

    @Override
    public void run() {
        try {
            if (isHost) {
                // Tryb Host (Serwer) - czeka na Gościa
                GameLogger.log("[Host] Rozpoczynanie nasłuchiwania na porcie: " + port);

                // Użycie ServerSocket.setReuseAddress(true) dla szybszego odzyskiwania portu
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new java.net.InetSocketAddress(port));

                socket = serverSocket.accept(); // Czekaj na połączenie od Guest
                GameLogger.log("[Host] Połączono z gościem: " + socket.getInetAddress().getHostAddress());
            } else {
                // Tryb Guest (Klient) - łączy się z Hostem
                GameLogger.log("[Guest] Próba połączenia z " + host + ":" + port);
                socket = new Socket(host, port);
                GameLogger.log("[Guest] Połączono z hostem.");
            }

            // --- KRYTYCZNA SEKCJA: INICJALIZACJA ASYMETRYCZNA ---
            performStreamSynchronization();
            connected = true;

            // Faza uzgadniania pokoju
            performRoomHandshake();

            // Główna pętla nasłuchująca wiadomości
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
     * Używa standardowej asymetrii:
     * Host: OOS -> OIS (czyli Host czeka na Gościa)
     * Guest: OIS -> OOS (czyli Gość czeka na Host)
     * To jest jedyna kolejność, która jest powszechnie stosowana w P2P.
     */
    private void performStreamSynchronization() throws IOException {

        if (isHost) {
            // HOST: OOS -> OIS
            // Host tworzy OOS. Nagłówek jest wysyłany.
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            GameLogger.log("[Host] Utworzono OOS.");

            // Host czeka na nagłówek Gościa (poprzez OIS).
            // To jest krytyczny punkt, który blokuje się, jeśli Gość jeszcze nie utworzył OOS.
            inputStream = new ObjectInputStream(socket.getInputStream());
            GameLogger.log("[Host] Utworzono OIS (wejście).");

        } else {
            // GUEST: OIS -> OOS

            // Gość musi najpierw utworzyć OIS, aby odczytać nagłówek Hosta, zanim Gość utworzy OOS.
            inputStream = new ObjectInputStream(socket.getInputStream());
            GameLogger.log("[Guest] Utworzono OIS.");

            // Gość tworzy OOS. Nagłówek jest wysyłany.
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            GameLogger.log("[Guest] Utworzono OOS (wyjście).");
        }

        GameLogger.log("[" + (isHost ? "Host" : "Guest") + "] Synchronizacja strumieni TCP zakończona.");
    }

    private void performRoomHandshake() throws IOException, ClassNotFoundException, InterruptedException {

        if (isHost) {
            // Host: Oczekuje wiadomości JOIN_ROOM od Guest
            Message joinMsg = (Message) inputStream.readObject();
            String remoteRoomId = (String) joinMsg.payload().get("roomId");
            String remoteName = (String) joinMsg.payload().get("name");

            if (roomId.equals(remoteRoomId)) {
                GameLogger.log("[Host] Gość (" + remoteName + ") dołączył do pokoju: " + remoteRoomId);
                // Host czeka, Gość zaczyna. Host wysyła do Gościa 'yourTurn: true' (dla Gościa).
                Message readyMsg = new Message(Message.MsgType.READY, Map.of("opponentName", remoteName, "yourTurn", true));
                send(readyMsg);

                // Host musi też otrzymać READY, aby NetworkController go przetworzył i ustawił Game.playerTurn = false.
                receivedMessages.put(new Message(Message.MsgType.READY, Map.of("opponentName", remoteName, "yourTurn", false)));
            } else {
                // Niepoprawny pokój
                Message disconnectMsg = new Message(Message.MsgType.DISCONNECT, Map.of("reason", "Niepoprawny numer pokoju."));
                send(disconnectMsg);
                throw new IOException("Niepoprawny numer pokoju: " + remoteRoomId);
            }
        } else {
            // Guest: Wysyła wiadomość JOIN_ROOM i czeka na READY od Host
            Message joinMsg = new Message(Message.MsgType.JOIN_ROOM,
                    Map.of("name", playerName, "roomId", roomId));
            send(joinMsg);

            // Guest teraz bezpiecznie odczytuje
            Message readyMsg = (Message) inputStream.readObject();
            if (readyMsg.type() == Message.MsgType.READY) {
                // Guest otrzymuje READY - tura zostanie ustawiona w NetworkController
                receivedMessages.put(readyMsg);
            } else {
                // Odebrano DISCONNECT lub inny błąd
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
        return receivedMessages.poll(); // Zwraca wiadomość natychmiast, jeśli jest dostępna, lub null
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