package com.battleship.player;

import com.battleship.board.Board;
import com.battleship.board.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementacja prostego bota (AI).
 * Bot inicjalizuje swoją planszę losowo w konstruktorze.
 * Posiada listę `remainingShots` zawierającą wszystkie możliwe pola, z której losuje
 * kolejne strzały, zapewniając, że nigdy nie strzeli dwa razy w to samo miejsce.
 */
public class BotPlayer extends Player {
    private final List<Position> remainingShots = new ArrayList<>();
    private final Random rand = new Random();

    public BotPlayer(String name) {
        super(name);
        getBoard().randomPlaceFleet();
        // Inicjalizacja puli dostępnych ruchów
        for (int r=0;r<Board.SIZE;r++)
            for (int c=0;c<Board.SIZE;c++)
                remainingShots.add(new Position(r,c));
    }

    /**
     * Wybiera losowo następny cel ataku z puli dostępnych pól.
     * @return Pozycja strzału lub null, jeśli brak ruchów.
     */
    public Position nextShot() {
        if (remainingShots.isEmpty()) return null;
        int idx = rand.nextInt(remainingShots.size());
        return remainingShots.remove(idx);
    }
}