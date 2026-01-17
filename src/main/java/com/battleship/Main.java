package com.battleship;

import com.battleship.ui.FXUI;

/**
 * Główny punkt wejścia do aplikacji (Entry Point).
 * Klasa ta pełni rolę bootstrapa – jej jedynym zadaniem jest przekazanie sterowania
 * do silnika JavaFX, który zarządza cyklem życia aplikacji okienkowej.
 *
 * Separacja metody main od logiki UI pozwala na uniknięcie problemów z inicjalizacją
 * toolkitu graficznego w niektórych środowiskach uruchomieniowych (np. przy braku modułów JavaFX w classpath).
 */
public class Main {
    /**
     * Uruchamia aplikację.
     * Wywołuje statyczną metodę launch z klasy Application (poprzez FXUI).
     *
     * @param args Argumenty wiersza poleceń przekazywane do aplikacji.
     */
    public static void main(String[] args) {
        FXUI.main(args);
    }
}