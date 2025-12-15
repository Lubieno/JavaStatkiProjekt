package com.battleship.game;

import com.battleship.board.Position;

public class Rules {
    public static final int BOARD_SIZE = 10;

    public static boolean isInBounds(Position p) {
        return p != null && p.row() >= 0 && p.col() >= 0 && p.row() < BOARD_SIZE && p.col() < BOARD_SIZE;
    }

    public static boolean isValidPlacementShape() {
        return true;
    }
}
