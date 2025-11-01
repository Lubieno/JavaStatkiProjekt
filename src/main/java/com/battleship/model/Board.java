package com.battleship.model;

import java.util.Random;

public class Board {
    private final int size;
    private final Cell[][] grid;

    public Board(int size) {
        this.size = size;
        grid = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                grid[i][j] = new Cell(i, j);
    }

    public void randomizeShips() {
        Random r = new Random();
        for (int i = 0; i < size * size / 5; i++) {
            int x = r.nextInt(size);
            int y = r.nextInt(size);
            grid[x][y].setOccupied(true);
        }
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }
}
