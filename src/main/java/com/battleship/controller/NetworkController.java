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

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::processMessages, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void processMessages() {
        Message msg;
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
                String opponentName = (String) msg.payload().getOrDefault("opponentName", "Remote Player");
                boolean yourTurn = (boolean) msg.payload().getOrDefault("yourTurn", true);

                gameController.getGame().setOpponent(new RemotePlayer(opponentName));
                gameController.getGame().setPlayerTurn(yourTurn);

                GameLogger.log("Gra gotowa. Przeciwnik: " + opponentName + ". Twoja tura: " + yourTurn);

                break;
            case SHOT:
                Position p = (Position) msg.payload().get("position");
                Event result = gameController.executeRemoteShot(p);
                GameLogger.log("Przeciwnik strzelił w " + p + ". Wynik: " + result.type());

                Message resultMsg = new Message(Message.MsgType.RESULT, Map.of(
                        "type", result.type().name(),
                        "message", result.message(),
                        "position", p
                ));
                sendMessage(resultMsg);

                if (result.type() == Event.Type.MISS || result.type() == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(true);
                } else {
                    gameController.getGame().setPlayerTurn(false);
                }

                break;
            case RESULT:
                String typeStr = (String) msg.payload().get("type");
                Position pResult = (Position) msg.payload().get("position");

                Event.Type type = Event.Type.valueOf(typeStr);

                gameController.markOpponentBoard(pResult, type);

                if (type == Event.Type.MISS || type == Event.Type.WIN) {
                    gameController.getGame().setPlayerTurn(false);
                } else {
                    gameController.getGame().setPlayerTurn(true);
                }

                GameLogger.log("Wynik Twojego strzału: " + typeStr + " na " + pResult);

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