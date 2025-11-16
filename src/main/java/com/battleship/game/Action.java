package com.battleship.game;

import com.battleship.board.Position;

public class Action {
    private final Position target;

    public Action(Position target) {
        this.target = target;
    }

    public Position getTarget() {
        return target;
    }
}
