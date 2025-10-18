package com.battleship;

public class Game {
    private final Board playerBoard;
    private final Board enemyBoard;
    private boolean playerTurn = true;

    public Game() {
        playerBoard = new Board();
        enemyBoard = new Board();
    }

    public void attack(int x, int y) {
        // tu logika strzału
        playerTurn = !playerTurn;
    }
}
