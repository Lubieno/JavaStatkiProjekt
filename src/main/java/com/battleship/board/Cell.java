package com.battleship.board;

/**
 * Pojedyncze pole na planszy.
 */
public class Cell {
    private int x;
    private int y;
    private boolean trafiony;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.trafiony = false;
    }

    public void oznaczTrafienie() { trafiony = true; }
    public boolean isTrafiony() { return trafiony; }
}
