package com.battleship.player;

import com.battleship.board.Position;

public abstract class Player {
    public abstract Position getNextShot();
    public abstract String getName();
}
