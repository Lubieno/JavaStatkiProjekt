package com.battleship.controller;

import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.game.Action;
import com.battleship.game.Event;
import com.battleship.game.Rules;
import com.battleship.player.Player;

/**
 * Kontroler odpowiedzialny za walidację i wykonanie akcji gracza (Command Handler).
 * Sprawdza poprawność ruchu (czy jest tura gracza, czy pole mieści się w planszy)
 * i deleguje wykonanie do odpowiedniego modułu (sieć lub lokalna logika).
 */
public class ActionController {

    private final GameController gameController;

    public ActionController(GameController gameController) {
        this.gameController = gameController;
    }

    public Event performAction(Action action, NetworkController nc) {
        if (action == null) return new Event(Event.Type.INVALID, "No action");

        if (action.type() != Action.Type.SHOOT) {
            return new Event(Event.Type.INVALID, "Unsupported action type");
        }

        Position p = action.target();
        if (!Rules.isInBounds(p)) {
            return new Event(Event.Type.INVALID, "Out of bounds: " + p);
        }

        // Ścieżka dla gry sieciowej
        if (gameController.isNetworked()) {
            if (!gameController.getGame().isPlayerTurn()) {
                return new Event(Event.Type.INFO, "Poczekaj na swoją turę!");
            }
            gameController.playerShoot(p, nc);
            return new Event(Event.Type.INFO, "Strzał wysłany...");
        }

        // Ścieżka dla gry lokalnej (Human vs Bot)
        Player opponent = gameController.getGame().getOpponent();
        Cell cell = opponent.getBoard().getCell(p);

        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return new Event(Event.Type.ALREADY, "Już tu strzelałeś!");
        }

        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            boolean gameOver = opponent.getBoard().allSunk();

            if (gameOver) {
                return new Event(Event.Type.WIN, "Zatopiony! Wygrałeś grę!");
            }
            if (sunk) return new Event(Event.Type.SUNK, "Trafiony zatopiony!");

            return new Event(Event.Type.HIT, "Trafiony!");
        } else {
            cell.markMiss();
            gameController.getGame().setPlayerTurn(false);
            return new Event(Event.Type.MISS, "Pudło!");
        }
    }
}