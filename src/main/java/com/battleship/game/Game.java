package com.battleship.game;

import com.battleship.player.BotPlayer;
import com.battleship.player.HumanPlayer;
import com.battleship.player.Player;
import com.battleship.player.RemotePlayer;

public class Game {
    private final Player player;
    private Player opponent;
    private GameState state = GameState.WAITING;
    private boolean playerTurn = false;

    public Game(String playerName, Player opponent, boolean isHost) {
        this.player = new HumanPlayer(playerName);
        this.opponent = opponent;
        this.playerTurn = !isHost;
        player.getBoard().randomPlaceFleet();
    }

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
    public void switchTurn() { playerTurn = !playerTurn; }

    public boolean checkFinish() {
        if (player.getBoard().allSunk() || opponent.getBoard().allSunk()) {
            state = GameState.FINISHED;
            return true;
        }
        return false;
    }
}