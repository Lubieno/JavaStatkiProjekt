package com.battleship.model;

public class Ship {
    private final int length;
    private final boolean horizontal;
    private final int startX, startY;

    public Ship(int startX, int startY, int length, boolean horizontal) {
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.horizontal = horizontal;
    }

    public int getLength() { return length; }
    public boolean isHorizontal() { return horizontal; }
}
