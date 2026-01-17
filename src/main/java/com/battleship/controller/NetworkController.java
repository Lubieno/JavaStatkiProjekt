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
 * Kontroler sieciowy łączący warstwę transportową (Client) z logiką gry (GameController).
 * Wykorzystuje wzorzec Polling (odpytywanie) za pomocą `ScheduledExecutorService`,
 * aby cyklicznie pobierać wiadomości z kolejki klienta i przetwarzać je w głównym wątku aplikacji.
 */
public class NetworkController {
    private final NetworkManager networkManager;
    private Client client;
    private GameController gameController;
    private ScheduledExecutorService scheduler;

    public NetworkController() {
        this.networkManager = new NetworkManager();
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void startListening(int port, String playerName, String roomId) {
        networkManager.startListening(port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) {
            startListening();
        }
    }

    public void connectTo(String host, int port, String playerName, String roomId) {
        networkManager.connectTo(host, port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) {
            startListening();
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

    private void startListening() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        // Uruchamia cykliczne zadanie sprawdzania nowych wiadomości co 100ms
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::processMessages, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void processMessages() {
        Message msg;
        while (isConnected() && (msg = client.receiveMessage()) != null) {
            handleReceivedMessage(msg);
        }
    }

    /**
     * Maszyna stanów przetwarzająca przychodzące pakiety.
     * Decyduje o tym, jak zaktualizować model gry w odpowiedzi na komunikat z sieci.
     */
    private void handleReceivedMessage(Message msg) {
        GameLogger.log("[NetworkController] Odebrano: " + msg);

        if (gameController == null) {
            GameLogger.log("Błąd: GameController nieustawiony. Nie można przetworzyć wiadomości.");
            return;
        }

        switch (msg.type()) {
            case READY:
                // Inicjalizacja gry po udanym Handshake'u
                String opponentName = (String) msg.payload().getOrDefault("opponentName", "Remote Player");
                boolean yourTurn = (boolean) msg.payload().getOrDefault("yourTurn", true);

                gameController.getGame().setOpponent(new RemotePlayer(opponentName));
                gameController.getGame().setPlayerTurn(yourTurn);

                GameLogger.log("Gra gotowa. Przeciwnik: " + opponentName + ". Twoja tura: " + yourTurn);
                break;

            case SHOT:
                // Przeciwnik strzela w naszą planszę
                Position p = (Position) msg.payload().get("position");
                Event result = gameController.executeRemoteShot(p);
                GameLogger.log("Przeciwnik strzelił w " + p + ". Wynik: " + result.type());

                // Odsyłamy wynik strzału
                Message resultMsg = new Message(Message.MsgType.RESULT, Map.of(
                        "type", result.type().name(),
                        "message", result.message(),
                        "position", p
                ));
                sendMessage(resultMsg);

                // Aktualizacja tury
                if (result.type() == Event.Type.MISS || result.type() == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(true);
                } else {
                    gameController.getGame().setPlayerTurn(false);
                }
                break;

            case RESULT:
                // Otrzymujemy wynik naszego strzału
                String typeStr = (String) msg.payload().get("type");
                Position pResult = (Position) msg.payload().get("position");
                Event.Type type = Event.Type.valueOf(typeStr);

                gameController.markOpponentBoard(pResult, type);

                if (type == Event.Type.MISS || type == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(false);
                } else {
                    gameController.getGame().setPlayerTurn(true);
                }
                break;

            case DISCONNECT:
                GameLogger.log("Przeciwnik rozłączony. Powód: " + msg.payload().get("reason"));
                gameController.getGame().checkFinish();
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