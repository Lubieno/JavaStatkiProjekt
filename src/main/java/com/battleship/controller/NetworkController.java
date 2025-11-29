package com.battleship.controller;

import com.battleship.network.Client;
import com.battleship.network.NetworkManager;
import com.battleship.network.Message;
import com.battleship.game.Event;
import com.battleship.board.Position;
import com.battleship.util.GameLogger;
import com.battleship.player.RemotePlayer;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author Student
 *
 * Kontroler sieciowy — wrapper nad NetworkManager/Client.
 * Implementuje asynchroniczne nasłuchiwanie wiadomości od przeciwnika (P2P).
 */
public class NetworkController {
    private final NetworkManager networkManager;
    private Client client;
    private GameController gameController; // Dodana referencja do GameController
    private ScheduledExecutorService scheduler;

    public NetworkController() {
        this.networkManager = new NetworkManager();
    }

    // Setter dla GameController, ponieważ GameController jest tworzony dynamicznie
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    // Tryb Host (nasłuchiwanie) - zastępuje startServer
    public void startListening(int port, String playerName, String roomId) {
        networkManager.startListening(port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) {
            startListening(); // Uruchom asynchroniczne nasłuchiwanie
        }
    }

    // Tryb Guest (łączenie) - zastępuje connectTo(host, port)
    public void connectTo(String host, int port, String playerName, String roomId) {
        networkManager.connectTo(host, port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) {
            startListening(); // Uruchom asynchroniczne nasłuchiwanie
        }
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void sendMessage(Message msg) {
        if (client != null && client.isConnected()) {
            client.send(msg);
        } else {
            GameLogger.log("[NetworkController] Nie połączono. Wiadomość pominięta: " + msg);
        }
    }

    // Metoda nasłuchująca wiadomości asynchronicznie
    private void startListening() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Sprawdzaj kolejkę Klienta co 100ms
        scheduler.scheduleWithFixedDelay(this::processMessages, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void processMessages() {
        Message msg;
        // Sprawdzamy isConnected, aby uniknąć problemów w trakcie zamykania
        while (isConnected() && (msg = client.receiveMessage()) != null) {
            handleReceivedMessage(msg);
        }
    }

    private void handleReceivedMessage(Message msg) {
        GameLogger.log("[NetworkController] Odebrano: " + msg);

        if (gameController == null) {
            GameLogger.log("Błąd: GameController nieustawiony. Nie można przetworzyć wiadomości.");
            return;
        }

        switch (msg.type()) {
            case READY:
                // Otrzymano po uzgodnieniu Host-Guest
                String opponentName = (String) msg.payload().getOrDefault("opponentName", "Remote Player");
                boolean yourTurn = (boolean) msg.payload().getOrDefault("yourTurn", true);

                // Ustaw RemotePlayer w Game
                gameController.getGame().setOpponent(new RemotePlayer(opponentName));
                gameController.getGame().setPlayerTurn(yourTurn);

                GameLogger.log("Gra gotowa. Przeciwnik: " + opponentName + ". Twoja tura: " + yourTurn);

                break;
            case SHOT:
                // Otrzymano strzał od przeciwnika (do wykonania na naszej planszy)
                Position p = (Position) msg.payload().get("position");
                Event result = gameController.executeRemoteShot(p);
                GameLogger.log("Przeciwnik strzelił w " + p + ". Wynik: " + result.type());

                // Odesłanie wyniku strzału do przeciwnika
                Message resultMsg = new Message(Message.MsgType.RESULT, Map.of(
                        "type", result.type().name(),
                        "message", result.message(),
                        "position", p // Wysyłamy pozycję, aby strzelający mógł ją oznaczyć!
                ));
                sendMessage(resultMsg);

                // Logika TURY: Jeśli było Pudło lub Koniec Gry, ODBIERAJĄCY (MY) przejmuje turę.
                if (result.type() == Event.Type.MISS || result.type() == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(true); // ODBIERAJĄCY (MY) przejmuje turę
                } else {
                    gameController.getGame().setPlayerTurn(false); // ODBIERAJĄCY (MY) pozostaje bez tury (HIT)
                }

                break;
            case RESULT:
                // Otrzymano wynik strzału, który WYSLALIŚMY
                String typeStr = (String) msg.payload().get("type");
                Position pResult = (Position) msg.payload().get("position"); // Pobieramy pozycję

                Event.Type type = Event.Type.valueOf(typeStr);

                // *** NOWA LOGIKA WIZUALIZACJI ***
                gameController.markOpponentBoard(pResult, type);

                // Logika TURY: Jeśli było Pudło lub Koniec Gry, STRZELAJĄCY (MY) traci turę.
                if (type == Event.Type.MISS || type == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(false); // STRZELAJĄCY (MY) traci turę
                } else {
                    // HIT/SUNK - strzelający ma kolejną turę
                    gameController.getGame().setPlayerTurn(true); // Utrzymujemy turę
                }

                GameLogger.log("Wynik Twojego strzału: " + typeStr + " na " + pResult);

                break;
            case DISCONNECT:
                // Obsługa rozłączenia
                GameLogger.log("Przeciwnik rozłączony. Powód: " + msg.payload().get("reason"));
                gameController.getGame().checkFinish(); // Zakończenie gry
                break;
            default:
                GameLogger.log("Nieznany typ wiadomości: " + msg.type());
        }
    }

    public void close() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (client != null) {
            client.disconnect();
        }
    }
}