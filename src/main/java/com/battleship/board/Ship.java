package com.battleship.board;

/**
 * Reprezentacja statku.
 */
public class Ship {
    private String nazwa;
    private int dlugosc;
    private boolean zatopiony;

    public Ship(String nazwa, int dlugosc) {
        this.nazwa = nazwa;
        this.dlugosc = dlugosc;
        this.zatopiony = false;
    }

    public void oznaczZatopienie() { zatopiony = true; }
    public boolean isZatopiony() { return zatopiony; }
}
