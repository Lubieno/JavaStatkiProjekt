package com.battleship.controller;

import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.game.Rules;
import com.battleship.player.Player;

/**
 * ActionController wykonuje pojedyncze akcje (np. shot) i zwraca Event z wynikiem.
 */
public class ActionController {

    private final GameController gameController;

    public ActionController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Wykonaj akcję (strzał) gracza na pozycji p.
     * Zwraca Event z wynikiem (HIT/MISS/ALREADY/INVALID/WIN).
     */
    public Event performAction(Action action) {
        if (action == null) return new Event(Event.Type.INVALID, "No action");

        if (action.type() != Action.Type.SHOOT) {
            return new Event(Event.Type.INVALID, "Unsupported action type");
        }

        Position p = action.target();
        if (!Rules.isInBounds(p)) {
            return new Event(Event.Type.INVALID, "Out of bounds: " + p);
        }

        // assuming human is always player in GameController design
        Player opponent = gameController.getGame().getOpponent();
        Cell cell = opponent.getBoard().getCell(p);
        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return new Event(Event.Type.ALREADY, "Already fired at " + p);
        }

        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            boolean gameOver = opponent.getBoard().allSunk();
            if (gameOver) {
                return new Event(Event.Type.WIN, sunk ? "Hit and sunk. You win!" : "Hit. You win!");
            }
            return new Event(Event.Type.HIT, sunk ? "Hit and sunk at " + p : "Hit at " + p);
        } else {
            cell.markMiss();
            // change turn in game controller
            gameController.getGame().switchTurn();
            return new Event(Event.Type.MISS, "Miss at " + p);
        }
    }
}
