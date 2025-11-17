package com.battleship.controller;

import com.battleship.board.Board;
import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.player.BotPlayer;
import com.battleship.player.Player;
import com.battleship.game.Game;
import com.battleship.player.HumanPlayer;

/**
 * @Author Student
 *
 * Controller for local single-player game vs bot.
 */
public class GameController {
    private final Game game;

    public GameController() {
        game = new Game();
        game.start();
    }

    public Game getGame() { return game; }

    // Player shoots at opponent board
    public String playerShoot(Position p) {
        Player opp = game.getOpponent();
        Board board = opp.getBoard();
        Cell cell = board.getCell(p);
        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return "Already shot";
        }
        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            if (game.checkFinish()) return "You win!";
            return sunk ? "Hit and sunk!" : "Hit!";
        } else {
            cell.markMiss();
            game.switchTurn();
            return "Miss";
        }
    }

    // Bot turn; returns description of bot action
    public String botTurn() {
        Player bot = game.getOpponent(); // BotPlayer
        if (!(bot instanceof BotPlayer)) return "";
        BotPlayer b = (BotPlayer) bot;
        Position shot = b.nextShot();
        if (shot == null) return "Bot has no shots";
        Player human = game.getPlayer();
        Cell cell = human.getBoard().getCell(shot);
        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return botTurn(); // shouldn't happen
        }
        if (cell.hasShip()) {
            cell.markHit();
            if (human.getBoard().allSunk()) {
                game.checkFinish();
                return "Bot hit and won!";
            }
            return "Bot hit at " + shot;
        } else {
            cell.markMiss();
            game.switchTurn();
            return "Bot missed at " + shot;
        }
    }
}
