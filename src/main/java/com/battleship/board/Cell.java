package com.battleship.board;

public class Cell {
    private boolean occupied;
    private boolean hit;

    public Cell() {
        this.occupied = false;
        this.hit = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isHit() {
        return hit;
    }

    public void occupy() {
        this.occupied = true;
    }

    public void hit() {
        this.hit = true;
    }
}
