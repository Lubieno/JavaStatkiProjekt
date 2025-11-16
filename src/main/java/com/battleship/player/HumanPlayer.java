package com.battleship.player;

import com.battleship.board.Position;

public class HumanPlayer extends Player {

    private final String name;

    public HumanPlayer(String name) {
        this.name = name;
    }

    @Override
    public Position getNextShot() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
