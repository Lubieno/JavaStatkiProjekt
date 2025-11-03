package com.battleship.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Plansza gracza (dane ulotne).
 */
public class Board {
    private int szerokosc;
    private int wysokosc;
    private Cell[][] pola;
    private List<Ship> statki;

    public Board(int szerokosc, int wysokosc) {
        this.szerokosc = szerokosc;
        this.wysokosc = wysokosc;
        this.pola = new Cell[szerokosc][wysokosc];
        this.statki = new ArrayList<>();
        for (int x = 0; x < szerokosc; x++) {
            for (int y = 0; y < wysokosc; y++) {
                pola[x][y] = new Cell(x, y);
            }
        }
    }

    public void dodajStatek(Ship s) { statki.add(s); }
    public List<Ship> getStatki() { return statki; }
}
