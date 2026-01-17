package com.battleship.board;

/**
 * Klasa reprezentująca pojedynczą komórkę w logicznej siatce gry.
 * Pełni rolę kontenera stanu.
 *
 * Zastosowano tu relację asocjacji z klasą `Ship` (komórka może, ale nie musi zawierać referencji do statku).
 * Stan komórki (State) determinuje sposób jej renderowania w warstwie UI oraz logikę odpowiedzi na strzał.
 */
public class Cell {
    public enum State { EMPTY, SHIP, HIT, MISS }

    private State state = State.EMPTY;
    private Ship ship = null;

    public State getState() { return state; }

    /**
     * Sprawdza fizyczną obecność statku w komórce, niezależnie od tego, czy został już trafiony.
     * @return true, jeśli referencja do statku jest różna od null.
     */
    public boolean hasShip() { return ship != null; }

    /**
     * Przypisuje referencję statku do komórki i zmienia jej stan wewnętrzny.
     * @param s Obiekt statku, który zajmuje tę komórkę.
     */
    public void placeShip(Ship s) { ship = s; state = State.SHIP; }

    /**
     * Oznacza komórkę jako trafioną.
     * Kluczowy moment: metoda deleguje zdarzenie trafienia do obiektu `Ship`,
     * co pozwala statkowi śledzić własny poziom uszkodzeń (enkapsulacja logiki uszkodzeń).
     */
    public void markHit() { state = State.HIT; if (ship != null) ship.hit(); }

    public void markMiss() { state = State.MISS; }

    public Ship getShip() { return ship; }
}