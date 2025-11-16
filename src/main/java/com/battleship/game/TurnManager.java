package com.battleship.game;

public class TurnManager {

    private boolean myTurn;

    public TurnManager(boolean start) {
        this.myTurn = start;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void switchTurn() {
        myTurn = !myTurn;
    }
}
