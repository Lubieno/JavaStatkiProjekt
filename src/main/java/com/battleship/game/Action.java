package com.battleship.game;

/**
 * Pojedyncza akcja w grze (np. strzał).
 */
public class Action {
    private int x, y;
    private boolean trafiony;

    public Action(int x, int y, boolean trafiony) {
        this.x = x;
        this.y = y;
        this.trafiony = trafiony;
    }

    public boolean isTrafiony() { return trafiony; }
}
