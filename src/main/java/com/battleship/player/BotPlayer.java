package com.battleship.player;

import com.battleship.board.Board;
import com.battleship.board.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author Student
 *
 * Bot: places fleet randomly (including L shapes), and shoots randomly without repeating.
 */
public class BotPlayer extends Player {
    private final List<Position> remainingShots = new ArrayList<>();
    private final Random rand = new Random();

    public BotPlayer(String name) {
        super(name);
        getBoard().randomPlaceFleet();
        for (int r=0;r<Board.SIZE;r++)
            for (int c=0;c<Board.SIZE;c++)
                remainingShots.add(new Position(r,c));
    }

    public Position nextShot() {
        if (remainingShots.isEmpty()) return null;
        int idx = rand.nextInt(remainingShots.size());
        return remainingShots.remove(idx);
    }
}
