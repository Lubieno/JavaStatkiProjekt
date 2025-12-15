package com.battleship.ui;

import com.battleship.controller.ActionController;
import com.battleship.controller.GameController;
import com.battleship.controller.NetworkController;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.board.Position;
import com.battleship.network.NetworkManager;

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

    public void initNetworkGame(String playerName, String partnerAddress, String roomId, boolean isHost) {
        String opponentName = isHost ? "Gość" : "Host";

        this.gameController = new GameController(playerName, opponentName, isHost);
        this.actionController = new ActionController(gameController);
        this.networkController.setGameController(gameController);

        int port = NetworkManager.DEFAULT_PORT;
        if (isHost) {
            this.networkController.startListening(port, playerName, roomId);
        } else {
            this.networkController.connectTo(partnerAddress, port, playerName, roomId);
        }
    }


    public GameController getGameController() { return gameController; }
    public NetworkController getNetworkController() { return networkController; }

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