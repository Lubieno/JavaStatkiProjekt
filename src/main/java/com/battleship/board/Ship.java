package com.battleship.board;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final List<Position> positions = new ArrayList<>();
    private int hits = 0;
    private final int size;

    public Ship(List<Position> coords) {
        this.positions.addAll(coords);
        this.size = coords.size();
    }

    public List<Position> getPositions() { return positions; }
    public int size() { return size; }
    public void hit() { hits++; }
    public boolean isSunk() { return hits >= size; }
}
