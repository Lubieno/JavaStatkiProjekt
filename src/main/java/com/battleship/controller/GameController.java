package com.battleship.controller;

import com.battleship.board.Board;
import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.player.BotPlayer;
import com.battleship.player.Player;
import com.battleship.game.Game;
import com.battleship.player.HumanPlayer;
import com.battleship.player.RemotePlayer;
import com.battleship.game.Event;
import com.battleship.controller.NetworkController;
import com.battleship.network.Message;
import java.util.Map;

/**
 * @Author Student
 *
 * Controller for game logic (local, bot, or networked).
 */
public class GameController {
    private Game game;
    private boolean isNetworked;

    // Nowy konstruktor dla trybu sieciowego (P2P)
    public GameController(String playerName, String opponentName, boolean isHost) {
        this.isNetworked = true;
        this.game = new Game(playerName, new RemotePlayer(opponentName), isHost);
        this.game.start();
    }

    // Stary konstruktor dla trybu lokalnego
    public GameController() {
        this.isNetworked = false;
        this.game = new Game();
        this.game.start();
    }

    public Game getGame() { return game; }
    public boolean isNetworked() { return isNetworked; }

    // NOWA METODA: Oznacza wynik strzału na planszy przeciwnika
    public void markOpponentBoard(Position p, Event.Type type) {
        Player opp = game.getOpponent();
        Cell cell = opp.getBoard().getCell(p);

        if (type == Event.Type.HIT || type == Event.Type.WIN) {
            // Ponieważ nie znamy statku przeciwnika, oznaczamy tylko stan HIT
            // To jest krytyczny element wizualizacji
            cell.markHit();
        } else if (type == Event.Type.MISS) {
            cell.markMiss();
        }
        // W przypadku ALREADY/INVALID nic nie robimy
    }

    // Player shoots at opponent board - dostosowano dla trybu sieciowego
    public String playerShoot(Position p, NetworkController nc) {
        if (isNetworked) {
            // W trybie sieciowym wysyłamy akcję do przeciwnika.
            Message shotMsg = new Message(Message.MsgType.SHOT, Map.of("position", p));
            nc.sendMessage(shotMsg);

            return "Wysłano strzał do przeciwnika. Czekaj na wynik...";
        }

        // --- STARA LOGIKA LOKALNA (vs BOT) ---
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

    // Metoda do wykonania strzału OTRZYMANEGO z sieci na własnej planszy
    public Event executeRemoteShot(Position p) {
        // Ta metoda jest wywoływana przez NetworkController po otrzymaniu SHOT od przeciwnika
        Player localPlayer = game.getPlayer();
        Cell cell = localPlayer.getBoard().getCell(p);

        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return new Event(Event.Type.ALREADY, "Już tu strzelono przez sieć. Błąd synchronizacji.");
        }

        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            boolean gameOver = localPlayer.getBoard().allSunk();
            if (gameOver) {
                game.checkFinish();
                return new Event(Event.Type.WIN, sunk ? "Zatopiony. Przeciwnik wygrywa!" : "Trafiony. Przeciwnik wygrywa!");
            }
            return new Event(Event.Type.HIT, sunk ? "Trafiony zatopiony!" : "Trafiony!");
        } else {
            cell.markMiss();
            return new Event(Event.Type.MISS, "Pudło!");
        }
    }


    // Bot turn; returns description of bot action (tylko dla trybu lokalnego)
    public String botTurn() {
        if (isNetworked) return ""; // Ignoruj w trybie sieciowym

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