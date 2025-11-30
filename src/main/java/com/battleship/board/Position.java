package com.battleship.board;

import java.io.Serializable;

// Position musi być Serializable, ponieważ jest przesyłane w Message
public record Position(int row, int col) implements Serializable {
    public boolean inBounds(int size) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }
}