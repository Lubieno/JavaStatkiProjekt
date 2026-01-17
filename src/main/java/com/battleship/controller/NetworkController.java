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
 * Pośrednik w komunikacji asynchronicznej.
 * Odpowiada za cykliczne odbieranie wiadomości z kolejki klienta sieciowego
 * i delegowanie ich obsługi do {@link GameController}.
 * Wykorzystuje ScheduledExecutorService do realizacji wzorca Polling.
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

    /**
     * Uruchamia klienta w trybie nasłuchiwania (Server Socket).
     */
    public void startListening(int port, String playerName, String roomId) {
        networkManager.startListening(port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) startListening();
    }

    /**
     * Uruchamia klienta w trybie łączenia (Client Socket).
     */
    public void connectTo(String host, int port, String playerName, String roomId) {
        networkManager.connectTo(host, port, playerName, roomId);
        this.client = networkManager.getClient();
        if (client != null) startListening();
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

    /**
     * Inicjuje wątek schedulera, który przetwarza wiadomości przychodzące co 100ms.
     */
    private void startListening() {
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
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
     * Główna metoda dyspozytorska.
     * Analizuje typ otrzymanej wiadomości i podejmuje odpowiednie akcje w logice gry.
     */
    private void handleReceivedMessage(Message msg) {
        GameLogger.log("[NetworkController] Odebrano: " + msg);
        if (gameController == null) return;

        switch (msg.type()) {
            case READY:
                String opponentName = (String) msg.payload().getOrDefault("opponentName", "Remote Player");
                boolean yourTurn = (boolean) msg.payload().getOrDefault("yourTurn", true);
                gameController.getGame().setOpponent(new RemotePlayer(opponentName));
                gameController.getGame().setPlayerTurn(yourTurn);
                GameLogger.log("Gra gotowa. Tura: " + yourTurn);
                break;

            case SHOT:
                // Obsługa strzału przeciwnika
                Position p = (Position) msg.payload().get("position");
                Event result = gameController.executeRemoteShot(p);

                // Odsyłamy wynik do przeciwnika
                sendMessage(new Message(Message.MsgType.RESULT, Map.of(
                        "type", result.type().name(),
                        "message", result.message(),
                        "position", p
                )));

                // Aktualizacja tury: jeśli trafił, strzela dalej (chyba że wygrał)
                if (result.type() == Event.Type.MISS || result.type() == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(true);
                } else {
                    gameController.getGame().setPlayerTurn(false);
                }
                break;

            case RESULT:
                // Obsługa wyniku naszego strzału
                String typeStr = (String) msg.payload().get("type");
                Position pResult = (Position) msg.payload().get("position");
                Event.Type type = Event.Type.valueOf(typeStr);

                // Delegujemy aktualizację planszy (i ewentualne zakończenie gry) do GameControllera
                gameController.markOpponentBoard(pResult, type);

                if (type == Event.Type.MISS) {
                    gameController.getGame().setPlayerTurn(false);
                } else if (type == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(false);
                } else {
                    gameController.getGame().setPlayerTurn(true);
                }
                break;

            case DISCONNECT:
                GameLogger.log("Przeciwnik rozłączony.");
                gameController.getGame().checkFinish();
                break;
        }
    }

    public void close() {
        if (scheduler != null) scheduler.shutdownNow();
        if (client != null) client.disconnect();
    }
}