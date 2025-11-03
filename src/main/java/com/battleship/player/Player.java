package com.battleship.player;

/**
 * Podstawowy gracz.
 */
public class Player {
    protected String nazwa;
    protected int punkty;

    public Player(String nazwa) {
        this.nazwa = nazwa;
        this.punkty = 0;
    }

    public String getNazwa() { return nazwa; }
    public int getPunkty() { return punkty; }
    public void dodajPunkty(int p) { punkty += p; }
}
