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

public class GameController {
    private Game game;
    private boolean isNetworked;
    // potrzebne zeby zliczyc zbite statki do profilu
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

    public void markOpponentBoard(Position p, Event.Type type) {
        Player opp = game.getOpponent();
        Cell cell = opp.getBoard().getCell(p);

        if (type == Event.Type.HIT || type == Event.Type.WIN || type == Event.Type.SUNK) {
            cell.markHit();

            // naliczanie statystyk trafien w profilu
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                if (type == Event.Type.SUNK || type == Event.Type.WIN) {
                    profileManager.getCurrentProfile().addSunkShip();
                    // zapisujemy postep od razu
                    profileManager.saveProfiles();
                }
            }

            if (type == Event.Type.WIN) {
                game.checkFinish();
            }
        } else if (type == Event.Type.MISS) {
            cell.markMiss();
        }
    }

    public String playerShoot(Position p, NetworkController nc) {
        if (isNetworked) {
            Message shotMsg = new Message(Message.MsgType.SHOT, Map.of("position", p));
            nc.sendMessage(shotMsg);
            return "Wysłano...";
        }
        return "";
    }

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
            return "Bot trafił w " + shot;
        } else {
            cell.markMiss();
            game.switchTurn();
            return "Bot pudło w " + shot;
        }
    }
}