package com.battleship.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Model domeny reprezentujący statek.
 * Odpowiada za przechowywanie listy zajmowanych pozycji (dla celów walidacji i renderowania)
 * oraz śledzenie aktualnego stanu "życia" statku.
 *
 * Logika zatopienia (`isSunk`) opiera się na prostym liczniku trafień – jeśli liczba trafień
 * zrówna się z rozmiarem statku, uznaje się go za zatopiony.
 */
public class Ship {
    private final List<Position> positions = new ArrayList<>();
    private int hits = 0;
    private final int size;

    /**
     * Tworzy nowy statek na podstawie listy współrzędnych.
     * Rozmiar statku jest determinowany dynamicznie na podstawie długości listy pozycji.
     *
     * @param coords Lista pozycji zajmowanych przez statek.
     */
    public Ship(List<Position> coords) {
        this.positions.addAll(coords);
        this.size = coords.size();
    }

    public List<Position> getPositions() { return positions; }
    public int size() { return size; }

    /**
     * Inkrementuje licznik trafień. Metoda ta powinna być wywoływana przez obiekt `Cell` w momencie trafienia.
     */
    public void hit() { hits++; }

    /**
     * Sprawdza warunek zniszczenia statku.
     * @return true, jeśli liczba trafień jest równa lub większa od początkowego rozmiaru.
     */
    public boolean isSunk() { return hits >= size; }
}