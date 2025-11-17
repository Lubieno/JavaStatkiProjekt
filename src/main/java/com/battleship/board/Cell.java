package com.battleship.board;


public class Cell {
    public enum State { EMPTY, SHIP, HIT, MISS }

    private State state = State.EMPTY;
    private Ship ship = null;

    public State getState() { return state; }
    public boolean hasShip() { return ship != null; }
    public void placeShip(Ship s) { ship = s; state = State.SHIP; }
    public void markHit() { state = State.HIT; if (ship != null) ship.hit(); }
    public void markMiss() { state = State.MISS; }
    public Ship getShip() { return ship; }
}
