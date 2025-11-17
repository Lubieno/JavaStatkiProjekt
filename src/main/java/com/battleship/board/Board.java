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

    public boolean canPlace(List<Position> coords) {
        for (Position p : coords)
            if (!p.inBounds(SIZE)) return false;
        for (Position p : coords)
            if (getCell(p).hasShip()) return false;
        return true;
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

    // Randomized placement supporting straight or L-shape
    public void randomPlaceFleet() {
        int[] fleet = {4,3,3,2,2,2,1,1,1,1}; // sizes
        for (int size : fleet) {
            boolean placed = false;
            int trials = 0;
            while (!placed && trials++ < 200) {
                if (size == 1) { // single cell
                    int r = rand.nextInt(SIZE), c = rand.nextInt(SIZE);
                    List<Position> coords = List.of(new Position(r,c));
                    if (placeShip(coords)) placed = true;
                } else {
                    boolean doL = rand.nextBoolean(); // sometimes L-shaped
                    if (!doL) {
                        Orientation o = Orientation.values()[rand.nextInt(4)];
                        int r = rand.nextInt(SIZE), c = rand.nextInt(SIZE);
                        List<Position> coords = linearCoords(new Position(r,c), o, size);
                        if (coords != null && placeShip(coords)) placed = true;
                    } else {
                        // L-shape: pick bend position and two directions
                        int r = rand.nextInt(SIZE), c = rand.nextInt(SIZE);
                        Orientation dir1 = Orientation.values()[rand.nextInt(4)];
                        Orientation dir2 = Orientation.values()[rand.nextInt(4)];
                        if (dir1 == dir2) continue;
                        List<Position> coords = lShapeCoords(new Position(r,c), dir1, dir2, size);
                        if (coords != null && placeShip(coords)) placed = true;
                    }
                }
            }
            if (!placed) {
                // fallback: try every cell for linear placement
                outer:
                for (int r=0;r<SIZE;r++) for (int c=0;c<SIZE;c++) {
                    for (Orientation o : Orientation.values()) {
                        List<Position> coords = linearCoords(new Position(r,c), o, size);
                        if (coords!=null && placeShip(coords)) { placed = true; break outer; }
                    }
                }
            }
        }
    }

    private List<Position> linearCoords(Position start, Orientation o, int size) {
        List<Position> coords = new ArrayList<>();
        int dr = 0, dc = 0;
        switch (o) {
            case UP -> dr = -1;
            case DOWN -> dr = 1;
            case LEFT -> dc = -1;
            case RIGHT -> dc = 1;
        }
        int r = start.row(), c = start.col();
        for (int i=0;i<size;i++) {
            Position p = new Position(r + i*dr, c + i*dc);
            if (!p.inBounds(SIZE)) return null;
            coords.add(p);
        }
        return coords;
    }

    // Simple L-shape: go k steps in dir1, remaining in dir2
    private List<Position> lShapeCoords(Position start, Orientation dir1, Orientation dir2, int size) {
        // split: at least 1 cell in first segment and at least 1 in second
        int maxFirst = size - 1;
        int firstLen = 1 + rand.nextInt(maxFirst);
        int secondLen = size - firstLen;

        List<Position> coords = new ArrayList<>();
        int r = start.row(), c = start.col();
        int dr1=0, dc1=0, dr2=0, dc2=0;
        switch (dir1) { case UP -> dr1=-1; case DOWN -> dr1=1; case LEFT -> dc1=-1; case RIGHT -> dc1=1; }
        switch (dir2) { case UP -> dr2=-1; case DOWN -> dr2=1; case LEFT -> dc2=-1; case RIGHT -> dc2=1; }

        // first segment
        for (int i=0;i<firstLen;i++) {
            Position p = new Position(r + i*dr1, c + i*dc1);
            if (!p.inBounds(SIZE)) return null;
            coords.add(p);
        }
        // bend start
        int bendR = r + (firstLen-1)*dr1;
        int bendC = c + (firstLen-1)*dc1;

        // second segment (starts next to bend)
        for (int i=1;i<=secondLen;i++) {
            Position p = new Position(bendR + i*dr2, bendC + i*dc2);
            if (!p.inBounds(SIZE)) return null;
            coords.add(p);
        }
        return coords;
    }
}
