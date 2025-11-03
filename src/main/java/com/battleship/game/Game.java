package com.battleship.game;

import com.battleship.board.Board;
import com.battleship.player.Player;
import com.battleship.util.GameLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa główna logiki gry.
 * Przechowuje dane ulotne (plansze, gracze, stan gry).
 */
public class Game {
    private List<Player> gracze;
    private List<Board> plansze;
    private GameState stanGry;
    private TurnManager turnManager;

    public Game() {
        gracze = new ArrayList<>();
        plansze = new ArrayList<>();
        stanGry = new GameState();
        turnManager = new TurnManager();
    }

    /** Inicjalizacja danych ulotnych. */
    public void inicjalizuj() {
        System.out.println("Inicjalizacja gry...");
        gracze.add(new Player("Gracz 1"));
        gracze.add(new Player("Gracz 2"));
        plansze.add(new Board(10, 10));
        plansze.add(new Board(10, 10));
    }

    /** Zapis danych trwałych po zakończeniu gry. */
    public void zapiszPodsumowanie() {
        GameLogger logger = new GameLogger();
        logger.zapiszDoPliku(stanGry);
    }
}
