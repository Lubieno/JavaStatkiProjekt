package com.battleship.game;

/**
 * Zdarzenie w grze (np. zatopienie statku).
 */
public class Event {
    private String opis;

    public Event(String opis) { this.opis = opis; }
    public String getOpis() { return opis; }
}
