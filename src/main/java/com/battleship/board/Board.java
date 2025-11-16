package com.battleship.board;

import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {

    public static final int DEFAULT_ROWS = 10;
    public static final int DEFAULT_COLS = 10;

    private final int rows;
    private final int cols;
    private final Cell[][] cells;
    private final List<Ship> ships = new ArrayList<>();

    public Board() {
        this(DEFAULT_ROWS, DEFAULT_COLS);
    }

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                cells[r][c] = new Cell();
    }

    public boolean inBounds(Position p) {
        return p.row >= 0 && p.row < rows && p.col >= 0 && p.col < cols;
    }

    public boolean placeShip(Set<Position> positions) {
        for (Position p : positions) {
            if (!inBounds(p)) return false;
            if (cells[p.row][p.col].isOccupied()) return false;
        }

        Ship s = new Ship(positions);
        ships.add(s);

        for (Position p : positions)
            cells[p.row][p.col].occupy();

        return true;
    }

    public ShotResult shoot(Position p) {
        if (!inBounds(p)) return ShotResult.INVALID;

        Cell cell = cells[p.row][p.col];
        if (cell.isHit()) return ShotResult.ALREADY;

        cell.hit();

        for (Ship s : ships) {
            if (s.occupies(p)) {
                s.registerHit(p);
                return s.isSunk() ? ShotResult.SANK : ShotResult.HIT;
            }
        }

        return ShotResult.MISS;
    }

    public boolean allSunk() {
        for (Ship s : ships)
            if (!s.isSunk()) return false;
        return true;
    }

    public enum ShotResult {
        HIT, MISS, SANK, ALREADY, INVALID
    }
}
