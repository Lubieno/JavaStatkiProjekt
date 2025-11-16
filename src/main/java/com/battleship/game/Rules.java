package com.battleship.game;

import com.battleship.board.Board;
import com.battleship.board.Position;

public class Rules {

    public boolean isShotValid(Board enemy, Position p) {
        return enemy.inBounds(p);
    }
}
