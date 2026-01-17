package com.battleship.game;

import com.battleship.board.Position;

/**
 * Klasa statyczna zawierająca stałe konfiguracyjne oraz uniwersalne reguły gry.
 * Zapewnia jedno źródło prawdy dla parametrów takich jak rozmiar planszy,
 * co ułatwia ewentualną zmianę zasad w przyszłości.
 */
public class Rules {
    public static final int BOARD_SIZE = 10;

    /**
     * Statyczna metoda pomocnicza do szybkiej weryfikacji poprawności współrzędnych.
     */
    public static boolean isInBounds(Position p) {
        return p != null && p.row() >= 0 && p.col() >= 0 && p.row() < BOARD_SIZE && p.col() < BOARD_SIZE;
    }
}