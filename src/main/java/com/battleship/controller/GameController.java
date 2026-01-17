package com.battleship.controller;

import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.data.ProfileManager;
import com.battleship.player.BotPlayer;
import com.battleship.player.Player;
import com.battleship.game.Game;
import com.battleship.player.RemotePlayer;
import com.battleship.game.Event;
import com.battleship.network.Message;
import com.battleship.util.GameLogger;

import java.util.Map;

/**
 * Kontroler główny warstwy logiki aplikacji.
 * Pełni rolę koordynatora pomiędzy modelem gry ({@link Game}), warstwą sieciową,
 * a menedżerem danych trwałych. Odpowiada za interpretację reguł gry
 * i aktualizację stanu modelu w odpowiedzi na akcje graczy.
 */
public class GameController {
    private Game game;
    private boolean isNetworked;
    private ProfileManager profileManager;

    /**
     * Inicjuje kontroler dla trybu sieciowego (PvP).
     * Tworzy instancję gry z {@link RemotePlayer}.
     */
    public GameController(String playerName, String opponentName, boolean isHost) {
        this.isNetworked = true;
        this.game = new Game(playerName, new RemotePlayer(opponentName), isHost);
        this.game.start();
    }

    /**
     * Inicjuje kontroler dla trybu lokalnego (PvE).
     * Tworzy instancję gry z domyślnym botem.
     */
    public GameController() {
        this.isNetworked = false;
        this.game = new Game();
        this.game.start();
    }

    public void setProfileManager(ProfileManager pm) { this.profileManager = pm; }
    public Game getGame() { return game; }
    public boolean isNetworked() { return isNetworked; }

    /**
     * Aktualizuje stan planszy przeciwnika ("Shadow Board") na podstawie wyniku strzału.
     * Jest to kluczowa metoda w synchronizacji stanu gry sieciowej.
     *
     * @param p Pozycja, w którą oddano strzał.
     * @param type Typ zdarzenia zwrócony przez przeciwnika (HIT, MISS, SUNK, WIN).
     */
    public void markOpponentBoard(Position p, Event.Type type) {
        Player opp = game.getOpponent();
        Cell cell = opp.getBoard().getCell(p);

        if (type == Event.Type.HIT || type == Event.Type.WIN || type == Event.Type.SUNK) {
            cell.markHit();

            // Logika aktualizacji statystyk profilu (tylko przy zatopieniu/wygranej)
            if (type == Event.Type.SUNK || type == Event.Type.WIN) {
                if(profileManager != null && profileManager.getCurrentProfile() != null) {
                    profileManager.getCurrentProfile().addSunkShip();
                    profileManager.saveProfiles();
                }
            }

            // CRITICAL FIX: Obsługa warunku zwycięstwa dla strony atakującej.
            // Ponieważ lokalna reprezentacja planszy przeciwnika (RemotePlayer)
            // nie zna pozycji wszystkich statków, nie możemy polegać na metodzie allSunk().
            // Sygnał WIN jest jednoznacznym potwierdzeniem końca gry przez serwer/przeciwnika.
            if (type == Event.Type.WIN) {
                GameLogger.log("Otrzymano potwierdzenie zwycięstwa (WIN). Kończenie gry.");
                game.finish();
            }
        } else if (type == Event.Type.MISS) {
            cell.markMiss();
        }
    }

    /**
     * Obsługuje proces wysyłania strzału przez gracza lokalnego.
     * Pakuje żądanie w obiekt {@link Message} i przekazuje do kontrolera sieci.
     */
    public void playerShoot(Position p, NetworkController nc) {
        if (isNetworked) {
            Message shotMsg = new Message(Message.MsgType.SHOT, Map.of("position", p));
            nc.sendMessage(shotMsg);
        }
    }

    /**
     * Przetwarza strzał otrzymany od zdalnego przeciwnika.
     * Sprawdza wynik na lokalnej planszy i zwraca odpowiednie zdarzenie {@link Event}.
     *
     * @param p Pozycja ataku przeciwnika.
     * @return Zdarzenie opisujące skutek (Trafienie, Pudło, Przegrana).
     */
    public Event executeRemoteShot(Position p) {
        Player localPlayer = game.getPlayer();
        Cell cell = localPlayer.getBoard().getCell(p);

        // Zapobieganie wielokrotnym strzałom w to samo pole
        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return new Event(Event.Type.ALREADY, "Już tu strzelano.");
        }

        if (cell.hasShip()) {
            cell.markHit();
            boolean sunk = cell.getShip().isSunk();
            // Sprawdzenie warunku przegranej (wszystkie statki gracza lokalnego zniszczone)
            boolean gameOver = localPlayer.getBoard().allSunk();

            if (gameOver) {
                game.checkFinish(); // Ustawia stan gry na FINISHED u przegranego
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
     * Implementuje logikę tury bota w trybie offline.
     * Wykonuje strzał AI, sprawdza wynik i zarządza przełączaniem tur.
     *
     * @return Komunikat tekstowy opisujący ruch bota.
     */
    public String botTurn() {
        if (isNetworked) return "";
        Player bot = game.getOpponent();
        if (!(bot instanceof BotPlayer)) return "";
        BotPlayer b = (BotPlayer) bot;

        Position shot = b.nextShot();
        if (shot == null) return "Bot nie ma ruchów";

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
            game.setPlayerTurn(true); // Oddanie tury graczowi
            return "Bot pudło w " + shot;
        }
    }
}