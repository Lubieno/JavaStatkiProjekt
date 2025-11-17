package com.battleship.player;

import com.battleship.board.Board;

public abstract class Player {
    protected final Board board = new Board();
    protected final String name;
    public Player(String name) { this.name = name; }
    public Board getBoard() { return board; }
    public String getName(){ return name; }
}
