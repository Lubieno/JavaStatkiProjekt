package com.battleship.game;

import com.battleship.board.Position;

/**
 * Zbiór reguł gry — sprawdzanie poprawności ruchów, ograniczeń.
 */
public class Rules {
    public static final int BOARD_SIZE = 10;

    public static boolean isInBounds(Position p) {
        return p != null && p.row() >= 0 && p.col() >= 0 && p.row() < BOARD_SIZE && p.col() < BOARD_SIZE;
    }

    /**
     * Dodatkowe reguły walidacji rozmieszczenia statków:
     * - nie nachodzą na siebie (to sprawdza Board::canPlace)
     * - dopuszczalne kształty: linear lub L (to jest wspierane przez Board.randomPlaceFleet)
     *
     * Pozostawione jako miejsce rozszerzeń.
     */
    public static boolean isValidPlacementShape() {
        return true;
    }
}
