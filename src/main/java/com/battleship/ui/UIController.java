package com.battleship.ui;

import com.battleship.controller.ActionController;
import com.battleship.controller.GameController;
import com.battleship.controller.NetworkController;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.board.Position;

/**
 * @Author Student
 *
 * Koordynuje UI i kontrolery (pośrednik między FXUI a GameController / ActionController).
 * FXUI może stworzyć instancję UIController i korzystać z metod do wykonania akcji.
 */
public class UIController {
    private final GameController gameController;
    private final ActionController actionController;
    private final NetworkController networkController;

    public UIController() {
        this.gameController = new GameController();
        this.actionController = new ActionController(gameController);
        this.networkController = new NetworkController();
    }

    public GameController getGameController() { return gameController; }
    public NetworkController getNetworkController() { return networkController; }

    /**
     * UI request to shoot at position p. Zwraca Event z wynikiem.
     */
    public Event shoot(Position p) {
        Action action = new Action(Action.Type.SHOOT, p);
        Event ev = actionController.performAction(action);
        // jeśli gra sieciowa - tu wysyłalibyśmy komunikat do przeciwnika
        return ev;
    }

    /**
     * uruchom nową grę (restart)
     */
    public void newGame() {
        // replace controllers with new instance
        // (w tej prostej implementacji tworzymy nowe obiekty)
        // Note: FXUI restartuje aplikację przez utworzenie nowego okna - więc tu może nie być użyte.
    }
}
