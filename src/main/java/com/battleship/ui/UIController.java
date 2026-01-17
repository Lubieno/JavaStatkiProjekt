package com.battleship.ui;

import com.battleship.controller.ActionController;
import com.battleship.controller.GameController;
import com.battleship.controller.NetworkController;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.board.Position;
import com.battleship.network.NetworkManager;

/**
 * Kontroler dedykowany dla warstwy UI.
 * Jego zadaniem jest spinanie logiki interfejsu JavaFX z backendem aplikacji.
 * Odpowiada za inicjalizację odpowiedniego trybu gry (Lokalna/Sieciowa) i wstrzykiwanie zależności.
 */
public class UIController {
    private GameController gameController;
    private ActionController actionController;
    private final NetworkController networkController;

    public UIController() {
        this.networkController = new NetworkController();
        initLocalGame();
    }

    public void initLocalGame() {
        this.gameController = new GameController();
        this.actionController = new ActionController(gameController);
        this.networkController.setGameController(gameController);
    }

    public void initNetworkGame(String playerName, String partnerAddress, int port, String roomId, boolean isHost) {
        String opponentName = isHost ? "Gość" : "Host";

        this.gameController = new GameController(playerName, opponentName, isHost);
        this.actionController = new ActionController(gameController);
        this.networkController.setGameController(gameController);

        if (isHost) {
            this.networkController.startListening(port, playerName, roomId);
        } else {
            this.networkController.connectTo(partnerAddress, port, playerName, roomId);
        }
    }


    public GameController getGameController() { return gameController; }
    public NetworkController getNetworkController() { return networkController; }

    /**
     * Metoda fasadowa dla UI do wykonywania akcji strzału.
     */
    public Event shoot(Position p) {
        Action action = new Action(Action.Type.SHOOT, p);
        return actionController.performAction(action, networkController);
    }

    public void newGame() {
        initLocalGame();
    }

    public void closeNetwork() {
        networkController.close();
    }
}