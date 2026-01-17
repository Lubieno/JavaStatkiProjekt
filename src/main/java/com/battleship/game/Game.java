package com.battleship.game;

import com.battleship.player.BotPlayer;
import com.battleship.player.HumanPlayer;
import com.battleship.player.Player;
import com.battleship.player.RemotePlayer;

/**
 * Główna klasa kontrolująca stan sesji gry (Session State).
 * Inicjalizuje odpowiednie typy graczy (w zależności od trybu: lokalny vs sieciowy)
 * oraz zarządza flagą określającą czyja jest tura.
 *
 * Wykorzystuje polimorfizm klasy `Player`, aby traktować bota i gracza zdalnego w jednolity sposób.
 */
public class Game {
    private final Player player;
    private Player opponent;
    private GameState state = GameState.WAITING;
    private boolean playerTurn = false;

    /**
     * Konstruktor dla gry sieciowej.
     * W tym trybie przeciwnikiem jest `RemotePlayer` (proxy).
     * Kolejność tur jest ustalana na podstawie roli (Host vs Guest).
     */
    public Game(String playerName, Player opponent, boolean isHost) {
        this.player = new HumanPlayer(playerName);
        this.opponent = opponent;
        this.playerTurn = !isHost; // Zasada: Gość zaczyna
        player.getBoard().randomPlaceFleet();
    }

    /**
     * Konstruktor domyślny dla gry lokalnej z Botem.
     */
    public Game() {
        this("You", new BotPlayer("Bot"), false);
        this.playerTurn = true;
    }

    public Player getPlayer() { return player; }
    public Player getOpponent() { return opponent; }
    public GameState getState() { return state; }

    public void setOpponent(Player opponent) { this.opponent = opponent; }
    public void setPlayerTurn(boolean isTurn) { this.playerTurn = isTurn; }

    public void start() { state = GameState.IN_PROGRESS; }

    public boolean isPlayerTurn() { return playerTurn; }

    /**
     * Sprawdza warunki zakończenia gry.
     * Jeśli którakolwiek z plansz ma zatopione wszystkie statki, zmienia stan gry na FINISHED.
     *
     * @return true, jeśli gra się zakończyła.
     */
    public boolean checkFinish() {
        if (player.getBoard().allSunk() || opponent.getBoard().allSunk()) {
            state = GameState.FINISHED;
            return true;
        }
        return false;
    }
}