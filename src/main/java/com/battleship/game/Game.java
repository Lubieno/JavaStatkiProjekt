package com.battleship.game;

import com.battleship.player.BotPlayer;
import com.battleship.player.HumanPlayer;
import com.battleship.player.Player;
import com.battleship.player.RemotePlayer;

/**
 * @Author Student
 *
 * Manages two-player game (local, bot, or remote).
 */
public class Game {
    private final Player player;
    private Player opponent; // Może być RemotePlayer lub BotPlayer
    private GameState state = GameState.WAITING;
    private boolean playerTurn = false; // Domyślnie gracz czeka na sygnał READY/START

    // Nowy konstruktor dla trybu sieciowego
    public Game(String playerName, Player opponent, boolean isHost) {
        this.player = new HumanPlayer(playerName);
        this.opponent = opponent;
        // Host czeka (false), Gość zaczyna (true) - ustalone w protokole P2P
        this.playerTurn = !isHost;
        player.getBoard().randomPlaceFleet();
    }

    // Stary konstruktor dla trybu lokalnego (Bot)
    public Game() {
        // Domyślna gra z Botem
        this("You", new BotPlayer("Bot"), false);
        this.playerTurn = true; // Gracz zaczyna w trybie lokalnym
    }

    public Player getPlayer() { return player; }
    public Player getOpponent() { return opponent; }
    public GameState getState() { return state; }

    public void setOpponent(Player opponent) { this.opponent = opponent; }
    public void setPlayerTurn(boolean isTurn) { this.playerTurn = isTurn; }

    public void start() { state = GameState.IN_PROGRESS; }

    public boolean isPlayerTurn() { return playerTurn; }
    public void switchTurn() { playerTurn = !playerTurn; }

    public boolean checkFinish() {
        if (player.getBoard().allSunk() || opponent.getBoard().allSunk()) {
            state = GameState.FINISHED;
            return true;
        }
        return false;
    }
}