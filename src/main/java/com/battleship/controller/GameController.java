package com.battleship.controller;

import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.data.Profile;
import com.battleship.data.ProfileManager;
import com.battleship.player.BotPlayer;
import com.battleship.player.Player;
import com.battleship.game.Game;
import com.battleship.player.RemotePlayer;
import com.battleship.game.Event;
import com.battleship.network.Message;
import java.util.Map;

/**
 * Główny kontroler (Controller w MVC) koordynujący przepływ gry.
 * Zarządza stanem modelu (Game), pośredniczy w komunikacji z ProfileManagerem
 * oraz obsługuje logikę tur (przełączanie między graczem a botem/siecią).
 */
public class GameController {
    private Game game;
    private boolean isNetworked;
    private ProfileManager profileManager;

    public GameController(String playerName, String opponentName, boolean isHost) {
        this.isNetworked = true;
        this.game = new Game(playerName, new RemotePlayer(opponentName), isHost);
        this.game.start();
    }

    public GameController() {
        this.isNetworked = false;
        this.game = new Game();
        this.game.start();
    }

    public void setProfileManager(ProfileManager pm) { this.profileManager = pm; }

    public Game getGame() { return game; }
    public boolean isNetworked() { return isNetworked; }

    /**
     * Aktualizuje planszę przeciwnika ("cień") na podstawie wyniku strzału otrzymanego z sieci.
     */
    public void markOpponentBoard(Position p, Event.Type type) {
        Cell c = game.getOpponent().getBoard().getCell(p);
        if (type == Event.Type.HIT || type == Event.Type.SUNK || type == Event.Type.WIN) {
            c.markHit();
            // Aktualizacja statystyk profilu
            if ((type == Event.Type.SUNK || type == Event.Type.WIN) && profileManager != null) {
                profileManager.getCurrentProfile().addSunkShip();
            }
        } else {
            c.markMiss();
        }
    }

    /**
     * Logika oddawania strzału przez gracza w trybie sieciowym.
     * Generuje obiekt Message i wysyła go przez NetworkController.
     */
    public void playerShoot(Position p, NetworkController nc) {
        Message msg = new Message(Message.MsgType.SHOT, Map.of("position", p));
        nc.sendMessage(msg);
    }

    /**
     * Przetwarza strzał otrzymany od przeciwnika sieciowego na naszej lokalnej planszy.
     * Zwraca wynik (Event), który zostanie odesłany z powrotem.
     */
    public Event executeRemoteShot(Position p) {
        Player localPlayer = game.getPlayer();
        Cell cell = localPlayer.getBoard().getCell(p);

        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return new Event(Event.Type.ALREADY, "Błąd synchronizacji.");
        }

        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            boolean gameOver = localPlayer.getBoard().allSunk();

            if (gameOver) {
                game.checkFinish();
                return new Event(Event.Type.WIN, "Przegrałeś :(");
            }
            if (sunk) return new Event(Event.Type.SUNK, "Trafiony zatopiony!");
            return new Event(Event.Type.HIT, "Trafiony!");
        } else {
            cell.markMiss();
            return new Event(Event.Type.MISS, "Pudło!");
        }
    }

    /**
     * Wykonuje turę bota (dla gry lokalnej).
     */
    public String botTurn() {
        if (isNetworked) return "";

        Player bot = game.getOpponent();
        if (!(bot instanceof BotPlayer)) return "";
        BotPlayer b = (BotPlayer) bot;
        Position shot = b.nextShot();
        if (shot == null) return "Bot śpi";

        Player human = game.getPlayer();
        Cell cell = human.getBoard().getCell(shot);

        if (cell.hasShip()) {
            cell.markHit();
            if (human.getBoard().allSunk()) {
                game.checkFinish();
                return "Bot wygrał!";
            }
            // Bot trafia, więc ma kolejny ruch (rekurencja logiczna)
            return "Bot trafił! " + botTurn();
        } else {
            cell.markMiss();
            game.setPlayerTurn(true);
            return "Bot spudłował.";
        }
    }
}