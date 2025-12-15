package com.battleship.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    public static final int SIZE = 10;
    private final Cell[][] grid = new Cell[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();
    private final Random rand = new Random();

    public Board() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = new Cell();
    }

    public Cell getCell(Position p) { return grid[p.row()][p.col()]; }

    public boolean allSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Ship getShipAt(Position p) {
        return getCell(p).getShip();
    }

    public boolean canPlace(List<Position> coords) {
        for (Position p : coords) {
            if (!p.inBounds(SIZE)) return false;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = p.row() + dr;
                    int nc = p.col() + dc;
                    if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                        if (grid[nr][nc].hasShip()) return false;
                    }
                }
            }
        }
        return true;
    }

    public List<Position> getLinearCoords(Position start, Orientation o, int size) {
        List<Position> coords = new ArrayList<>();
        int dr = 0, dc = 0;
        switch (o) {
            case UP -> dr = -1;
            case DOWN -> dr = 1;
            case LEFT -> dc = -1;
            case RIGHT -> dc = 1;
        }
        for (int i=0;i<size;i++) {
            Position p = new Position(start.row() + i*dr, start.col() + i*dc);
            coords.add(p);
        }
        return coords;
    }

    public boolean placeShip(List<Position> coords) {
        if (!canPlace(coords)) return false;
        Ship s = new Ship(coords);
        ships.add(s);
        for (Position p : coords) {
            getCell(p).placeShip(s);
        }
        return true;
    }

    public void clear() {
        ships.clear();
        for(int r=0; r<SIZE; r++)
            for(int c=0; c<SIZE; c++)
                grid[r][c] = new Cell();
    }

    public void randomPlaceFleet() {
        clear();
        int[] fleet = {4,3,3,2,2,2,1,1,1,1};

        for (int size : fleet) {
            boolean placed = false;
            int trials = 0;
            while (!placed && trials++ < 1000) {
                Orientation o = Orientation.values()[rand.nextInt(4)];
                int r = rand.nextInt(SIZE), c = rand.nextInt(SIZE);
                List<Position> coords = getLinearCoords(new Position(r,c), o, size);

                if (coords != null && placeShip(coords)) placed = true;
            }
        }
    }
}