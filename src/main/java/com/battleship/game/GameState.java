package com.battleship.game;

/**
 * Dane ulotne – stan aktualnej rozgrywki.
 */
public class GameState {
    private boolean zakonczona;
    private int aktualnyGracz;

    public GameState() {
        zakonczona = false;
        aktualnyGracz = 0;
    }

    public boolean isZakonczona() { return zakonczona; }
    public void setZakonczona(boolean zakonczona) { this.zakonczona = zakonczona; }

    public int getAktualnyGracz() { return aktualnyGracz; }
    public void setAktualnyGracz(int aktualnyGracz) { this.aktualnyGracz = aktualnyGracz; }
}
