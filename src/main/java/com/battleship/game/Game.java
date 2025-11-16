package com.battleship.game;

import com.battleship.board.Board;
import com.battleship.board.Position;
import com.battleship.util.GameLogger;

public class Game {

    private GameState state = GameState.SETUP;
    private final Board myBoard;
    private final Board enemyBoard;
    private final Rules rules;
    private final TurnManager tm;

    public Game(Board my, Board enemy, boolean startFirst) {
        this.myBoard = my;
        this.enemyBoard = enemy;
        this.rules = new Rules();
        this.tm = new TurnManager(startFirst);
    }

    public void start() {
        state = GameState.RUNNING;
        GameLogger.log("Game started");
    }

    public void executeAction(Action a) {
        if (state != GameState.RUNNING) return;

        Position p = a.getTarget();

        if (!rules.isShotValid(enemyBoard, p)) {
            GameLogger.log("Invalid shot");
            return;
        }

        Board.ShotResult result = enemyBoard.shoot(p);
        GameLogger.log("Shot result: " + result);

        if (enemyBoard.allSunk()) {
            state = GameState.FINISHED;
            GameLogger.log("Game finished: You win");
        }

        tm.switchTurn();
    }

    public GameState getState() {
        return state;
    }
}
