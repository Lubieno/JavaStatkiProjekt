package com.battleship.game;

/**
 * Zasady gry – np. ile statków, długości, trafienia, itp.
 */
public class Rules {

    /** Sprawdza czy ruch jest dozwolony. */
    public boolean ruchDozwolony(int x, int y) {
        return x >= 0 && y >= 0 && x < 10 && y < 10;
    }

    /** Określa czy gracz dostaje dodatkową turę po trafieniu. */
    public boolean dodatkowaTuraPrzyTrafieniu() {
        return true;
    }
}
