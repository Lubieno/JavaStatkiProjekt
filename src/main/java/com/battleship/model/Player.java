package com.battleship.model;

public class Player {
    private final String name;
    private final Board board;

    public Player(String name, int boardSize) {
        this.name = name;
        this.board = new Board(boardSize);
    }

    public Board getBoard() { return board; }
    public String getName() { return name; }
}
