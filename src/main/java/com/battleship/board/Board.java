package com.battleship.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Centralna klasa logiki planszy.
 * Implementuje siatkę gry jako dwuwymiarową tablicę obiektów `Cell`.
 * Zawiera kluczowe algorytmy walidacji rozmieszczenia statków, sprawdzania kolizji
 * oraz warunków końca gry.
 */
public class Board {
    public static final int SIZE = 10;
    private final Cell[][] grid = new Cell[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();
    private final Random rand = new Random();

    /**
     * Inicjalizuje macierz gry, wypełniając ją nowymi instancjami `Cell`.
     */
    public Board() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = new Cell();
    }

    public Cell getCell(Position p) { return grid[p.row()][p.col()]; }

    /**
     * Sprawdza globalny stan przegranej (czy wszystkie statki zostały zatopione).
     * Wykorzystuje Java Stream API do zwięzłej weryfikacji predykatu na liście statków.
     *
     * @return true, jeśli każdy statek w kolekcji zwraca true dla metody isSunk().
     */
    public boolean allSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Ship getShipAt(Position p) {
        return getCell(p).getShip();
    }

    /**
     * Waliduje możliwość umieszczenia statku na zadanych pozycjach.
     * Algorytm sprawdza dwa warunki:
     * 1. Czy pozycje mieszczą się w granicach planszy.
     * 2. Czy w promieniu 1 kratki (włącznie z przekątnymi - sąsiedztwo Moore'a) nie znajduje się inny statek.
     *
     * @param coords Lista planowanych pozycji statku.
     * @return true, jeśli pozycja jest legalna.
     */
    public boolean canPlace(List<Position> coords) {
        for (Position p : coords) {
            if (!p.inBounds(SIZE)) return false;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = p.row() + dr;
                    int nc = p.col() + dc;
                    if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                        if (grid[nr][nc].hasShip()) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Generuje listę współrzędnych liniowych dla statku o zadanej długości i orientacji.
     * Służy do translacji punktu startowego i kierunku na konkretny zbiór komórek.
     */
    public List<Position> getLinearCoords(Position start, Orientation o, int size) {
        List<Position> coords = new ArrayList<>();
        int dr = 0, dc = 0;
        switch (o) {
            case UP -> dr = -1;
            case DOWN -> dr = 1;
            case LEFT -> dc = -1;
            case RIGHT -> dc = 1;
        }
        for (int i=0;i<size;i++) {
            Position p = new Position(start.row() + i*dr, start.col() + i*dc);
            coords.add(p);
        }
        return coords;
    }

    /**
     * Próbuje umieścić statek na planszy.
     * Operacja jest atomowa z punktu widzenia logiki gry - albo statek jest stawiany w całości, albo wcale.
     *
     * @param coords Współrzędne statku.
     * @return true, jeśli operacja się powiodła (walidacja przeszła pomyślnie).
     */
    public boolean placeShip(List<Position> coords) {
        if (!canPlace(coords)) return false;
        Ship s = new Ship(coords);
        ships.add(s);
        for (Position p : coords) {
            getCell(p).placeShip(s);
        }
        return true;
    }

    public void clear() {
        ships.clear();
        for(int r=0; r<SIZE; r++)
            for(int c=0; c<SIZE; c++)
                grid[r][c] = new Cell();
    }

    /**
     * Automatycznie rozmieszcza flotę przy użyciu algorytmu losowego (metoda Monte Carlo).
     * Dla każdego statku podejmowane jest do 100 prób wylosowania legalnej pozycji.
     * Jeśli próby się nie powiodą, dany statek może nie zostać postawiony (choć przy tej wielkości planszy
     * prawdopodobieństwo takiego zdarzenia jest marginalne).
     */
    public void randomPlaceFleet() {
        clear();
        int[] fleet = {4,3,3,2,2,2,1,1,1,1}; // Definicja standardowej floty

        for (int size : fleet) {
            boolean placed = false;
            int trials = 0;
            while (!placed && trials < 100) {
                int r = rand.nextInt(SIZE);
                int c = rand.nextInt(SIZE);
                Orientation o = Orientation.values()[rand.nextInt(4)];
                List<Position> coords = getLinearCoords(new Position(r, c), o, size);
                if (placeShip(coords)) placed = true;
                trials++;
            }
        }
    }
}