package com.battleship.controller;

import com.battleship.board.Position;
import com.battleship.game.Action;
import com.battleship.game.Game;
import com.battleship.util.GameLogger;

public class ActionController {

    private final Game game;

    public ActionController(Game game) {
        this.game = game;
    }

    public void performShot(int row, int col) {
        GameLogger.log("ActionController: performing shot");
        game.executeAction(new Action(new Position(row, col)));
    }
}
