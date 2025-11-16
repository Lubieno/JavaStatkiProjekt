package com.battleship.board;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Ship implements Serializable {
    private final Set<Position> positions = new HashSet<>();
    private final Set<Position> hits = new HashSet<>();

    public Ship(Set<Position> positions) {
        this.positions.addAll(positions);
    }

    public boolean occupies(Position p) {
        return positions.contains(p);
    }

    public void registerHit(Position p) {
        if (positions.contains(p)) hits.add(p);
    }

    public boolean isSunk() {
        return hits.containsAll(positions);
    }

    public int size() {
        return positions.size();
    }
}
