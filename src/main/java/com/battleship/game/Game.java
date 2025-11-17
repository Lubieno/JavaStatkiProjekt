package com.battleship.game;

import com.battleship.player.BotPlayer;
import com.battleship.player.HumanPlayer;
import com.battleship.player.Player;

public class Game {
    private final Player player;
    private final Player opponent;
    private GameState state = GameState.WAITING;
    private boolean playerTurn = true;

    public Game() {
        player = new HumanPlayer("You");
        opponent = new BotPlayer("Bot");
        player.getBoard().randomPlaceFleet();
    }

    public Player getPlayer() { return player; }
    public Player getOpponent() { return opponent; }
    public GameState getState() { return state; }
    public void start() { state = GameState.IN_PROGRESS; playerTurn = true; }

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
