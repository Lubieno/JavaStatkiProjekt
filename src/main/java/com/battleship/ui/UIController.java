package com.battleship.ui;

import com.battleship.controller.ActionController;
import com.battleship.controller.GameController;
import com.battleship.controller.NetworkController;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.board.Position;
import com.battleship.network.NetworkManager;

/**
 * Koordynuje UI i kontrolery (pośrednik między FXUI a GameController / ActionController).
 * UIController zarządza teraz logiką inicjalizacji sieci P2P.
 */
public class UIController {
    private GameController gameController;
    private ActionController actionController;
    private final NetworkController networkController; // Zmieniono na final

    public UIController() {
        this.networkController = new NetworkController();
        initLocalGame(); // Domyślna inicjalizacja lokalna
    }

    // Inicjalizacja domyślnej gry lokalnej (vs Bot)
    public void initLocalGame() {
        this.gameController = new GameController();
        this.actionController = new ActionController(gameController);
        this.networkController.setGameController(gameController);
    }

    // Inicjalizacja gry w trybie sieciowym (P2P)
    public void initNetworkGame(String playerName, String partnerAddress, String roomId, boolean isHost) {
        String opponentName = isHost ? "Gość" : "Host";

        // 1. Utwórz GameController z RemotePlayer
        // isHost jest przekazywane, aby Game ustawiło właściwą początkową turę (Host czeka, Gość zaczyna)
        this.gameController = new GameController(playerName, opponentName, isHost);
        this.actionController = new ActionController(gameController);
        this.networkController.setGameController(gameController); // Przypisz nowy GC do NC

        // 2. Uruchom połączenie
        int port = NetworkManager.DEFAULT_PORT;
        if (isHost) {
            this.networkController.startListening(port, playerName, roomId);
        } else {
            this.networkController.connectTo(partnerAddress, port, playerName, roomId);
        }
    }


    public GameController getGameController() { return gameController; }
    public NetworkController getNetworkController() { return networkController; }

    /**
     * UI request to shoot at position p. Zwraca Event z wynikiem.
     */
    public Event shoot(Position p) {
        Action action = new Action(Action.Type.SHOOT, p);

        // W trybie sieciowym przekazujemy NetworkController
        return actionController.performAction(action, networkController);
    }

    /**
     * uruchom nową grę (restart)
     */
    public void newGame() {
        initLocalGame();
    }

    public void closeNetwork() {
        networkController.close();
    }
}