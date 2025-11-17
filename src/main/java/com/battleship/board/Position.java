package com.battleship.board;


public record Position(int row, int col) {
    public boolean inBounds(int size) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }
}
