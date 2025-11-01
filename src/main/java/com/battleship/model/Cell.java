package com.battleship.model;

public class Cell {
    private final int x, y;
    private boolean occupied;
    private boolean hit;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
        this.hit = false;
    }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }
}
