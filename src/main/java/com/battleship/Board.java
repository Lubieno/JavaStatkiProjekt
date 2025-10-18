package com.battleship;

public class Board {
    private static final int SIZE = 10;
    private final char[][] grid = new char[SIZE][SIZE];

    public Board() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = '~';
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                System.out.print(grid[i][j] + " ");
            System.out.println();
        }
    }
}
