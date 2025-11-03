package com.battleship;

import com.battleship.game.Game;

/**
 * Punkt startowy aplikacji.
 * Obecnie tylko inicjalizuje grę i zapisuje dane do pliku.
 */
public class Main {
    public static void main(String[] args) {
        Game gra = new Game();
        gra.inicjalizuj();
        gra.zapiszPodsumowanie(); // zapis danych trwałych (pliku z wynikami)
    }
}
