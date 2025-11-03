package com.battleship.game;

/**
 * Zarządza kolejkami tur graczy.
 * Zasada: zbicie statku daje dodatkową turę.
 */
public class TurnManager {
    private int aktualnyGracz;

    public TurnManager() {
        aktualnyGracz = 0;
    }

    /** Przechodzi do następnego gracza, o ile nie ma dodatkowej tury. */
    public void nastepnaTura(boolean czyZbityStatek) {
        if (!czyZbityStatek) {
            aktualnyGracz = (aktualnyGracz + 1) % 2;
        }
    }

    public int getAktualnyGracz() { return aktualnyGracz; }
}
